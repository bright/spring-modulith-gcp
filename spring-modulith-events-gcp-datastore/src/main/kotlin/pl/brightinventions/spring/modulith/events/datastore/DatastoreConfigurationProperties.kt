package pl.brightinventions.spring.modulith.events.datastore

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for GCP Datastore event publication.
 *
 * @author Piotr Mionskowski
 */
@ConfigurationProperties("pl.brightinventions.spring.modulith.events.datastore")
class DatastoreConfigurationProperties {
    var retry: RetryProperties = RetryProperties()

    class RetryProperties {
        /** Whether retry is enabled */
        var enabled: Boolean = true
        /** Maximum number of retry attempts */
        var maxAttempts: Int = 5
        /** Initial delay in milliseconds */
        var initialInterval: Long = 100
        /** Maximum delay in milliseconds */
        var maxInterval: Long = 2000
        /** Backoff multiplier */
        var multiplier: Double = 2.0
    }
}
