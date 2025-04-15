package pl.brightinventions.spring.modulith.events.datastore

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for GCP Datastore event publication.
 *
 * @author Piotr Mionskowski
 */
@ConfigurationProperties("pl.brightinventions.spring.modulith.events.datastore")
class DatastoreConfigurationProperties {
    // Configuration properties can be added here as needed
}
