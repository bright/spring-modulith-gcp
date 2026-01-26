package pl.brightinventions.spring.modulith.events.datastore

import org.slf4j.LoggerFactory
import org.springframework.retry.RetryCallback
import org.springframework.retry.RetryContext
import org.springframework.retry.RetryListener

/**
 * Retry listener that logs Datastore operation retry attempts.
 *
 * This listener is registered as a bean in [DatastoreRetryConfiguration].
 *
 * @author Adam Waniak
 */
class DatastoreRetryListener : RetryListener {

    private val logger = LoggerFactory.getLogger(DatastoreRetryListener::class.java)

    override fun <T, E : Throwable> onError(
        context: RetryContext,
        callback: RetryCallback<T, E>,
        throwable: Throwable
    ) {
        logger.warn(
            "Datastore operation retry attempt {} failed: {}",
            context.retryCount,
            throwable.message
        )
    }

    override fun <T, E : Throwable> close(
        context: RetryContext,
        callback: RetryCallback<T, E>,
        throwable: Throwable?
    ) {
        if (throwable != null) {
            logger.error(
                "Datastore operation failed after {} attempts: {}",
                context.retryCount,
                throwable.message
            )
        } else if (context.retryCount > 0) {
            logger.info(
                "Datastore operation succeeded after {} retry attempts",
                context.retryCount
            )
        }
    }
}
