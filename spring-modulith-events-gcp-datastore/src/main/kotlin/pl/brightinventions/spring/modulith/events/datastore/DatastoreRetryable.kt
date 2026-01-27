package pl.brightinventions.spring.modulith.events.datastore

import com.google.cloud.datastore.DatastoreException
import org.springframework.retry.annotation.Backoff
import org.springframework.retry.annotation.Retryable
import org.springframework.transaction.TransactionSystemException

/**
 * Custom annotation for retrying Datastore operations.
 *
 * This annotation applies retry logic with exponential backoff for handling
 * transient failures like "too much contention on these datastore entities".
 *
 * Configuration can be customized via application properties:
 * - pl.brightinventions.spring.modulith.events.datastore.retry.max-attempts
 * - pl.brightinventions.spring.modulith.events.datastore.retry.initial-interval
 * - pl.brightinventions.spring.modulith.events.datastore.retry.max-interval
 * - pl.brightinventions.spring.modulith.events.datastore.retry.multiplier
 *
 * @author Adam Waniak
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Retryable(
    listeners = ["datastoreRetryListener"],
    retryFor = [DatastoreException::class, TransactionSystemException::class],
    maxAttemptsExpression = "\${pl.brightinventions.spring.modulith.events.datastore.retry.max-attempts:5}",
    backoff = Backoff(
        delayExpression = "\${pl.brightinventions.spring.modulith.events.datastore.retry.initial-interval:100}",
        maxDelayExpression = "\${pl.brightinventions.spring.modulith.events.datastore.retry.max-interval:2000}",
        multiplierExpression = "\${pl.brightinventions.spring.modulith.events.datastore.retry.multiplier:2.0}"
    )
)
annotation class DatastoreRetryable
