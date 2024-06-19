import javax.xml.parsers.DocumentBuilderFactory

plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.kotlinx.kover") version "0.8.1"
    id("org.jetbrains.dokka") version "1.9.20"
    `maven-publish`
}

group = "dev.starry.ktscheduler"
version = "1.1.2"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    // Test
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
    testImplementation("org.mockito:mockito-core:4.6.1")
    testImplementation("org.mockito:mockito-inline:4.6.1")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:2.0.0")
}

kover {
    reports {
        // Require 95% minimum test coverage.
        verify { rule { minBound(95) } }
    }
}

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

val sourcesJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = group.toString()
            artifactId = "ktscheduler"
            version = version.toString()
            from(components["java"])

            pom {
                name.set("KtScheduler")
                description.set("Coroutine-based task/job scheduler for Kotlin.")
                url.set("https://github.com/Pool-Of-Tears/KtScheduler")

                licenses {
                    license {
                        name.set("Apache-2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("starry-shivam")
                        name.set("Starry Shivam")
                        email.set("starry@krsh.dev")
                    }
                }
            }

            afterEvaluate {
                // Add Javadoc JAR to the publication.
                artifact(javadocJar)
                // Add sources JAR to the publication.
                artifact(sourcesJar)
            }
        }
    }
}

// Print line coverage percentage to console, so we can generate badge in CI.
tasks.register("printLineCoverage") {
    group = "verification"
    dependsOn("koverXmlReport")
    doLast {
        val report = file("$buildDir/reports/kover/report.xml")
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(report)
        val rootNode = doc.firstChild
        var childNode = rootNode.firstChild
        var coveragePercent = 0.0

        while (childNode != null) {
            if (childNode.nodeName == "counter") {
                val typeAttr = childNode.attributes.getNamedItem("type")
                if (typeAttr.textContent == "LINE") {
                    val missedAttr = childNode.attributes.getNamedItem("missed")
                    val coveredAttr = childNode.attributes.getNamedItem("covered")
                    val missed = missedAttr.textContent.toLong()
                    val covered = coveredAttr.textContent.toLong()
                    // Calculate coverage percentage.
                    coveragePercent = (covered * 100.0) / (missed + covered)
                    break
                }
            }
            childNode = childNode.nextSibling
        }
        println("%.1f".format(coveragePercent))
    }
}