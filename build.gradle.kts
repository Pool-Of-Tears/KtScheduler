plugins {
    kotlin("jvm") version "2.0.0"
    `maven-publish`
}

group = "dev.starry.ktscheduler"
version = "1.0.0"

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


publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = "dev.starry.ktscheduler"
            artifactId = "ktscheduler"
            version = "1.0.0"
            from(components["java"])
        }
    }
}


