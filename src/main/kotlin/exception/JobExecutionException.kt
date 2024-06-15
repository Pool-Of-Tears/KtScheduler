package dev.ktscheduler.exception

/**
 * An exception that is thrown when a job execution fails.
 *
 * @param message The exception message.
 * @param cause The exception cause.
 */
class JobExecutionException(
    message: String, cause: Throwable
) : Exception(message, cause)
