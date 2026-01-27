package pl.brightinventions.spring.modulith.events.datastore

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.retry.annotation.EnableRetry

/**
 * Auto-configuration class that enables Spring Retry support for Datastore operations.
 *
 * Retry is enabled by default and can be disabled via configuration property:
 * pl.brightinventions.spring.modulith.events.datastore.retry.enabled=false
 *
 * @author Adam Waniak
 */
@AutoConfiguration
@ConditionalOnClass(name = ["org.springframework.retry.annotation.EnableRetry"])
@ConditionalOnProperty(
    prefix = "pl.brightinventions.spring.modulith.events.datastore.retry",
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = true
)
class DatastoreRetryConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = ["datastoreRetryListener"])
    fun datastoreRetryListener(): DatastoreRetryListener {
        return DatastoreRetryListener()
    }
}
