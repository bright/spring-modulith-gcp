package pl.brightinventions.spring.modulith.starter.datastore

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import pl.brightinventions.spring.modulith.events.datastore.DatastoreEventPublicationAutoConfiguration

/**
 * Auto-configuration for Spring Modulith with GCP Datastore.
 *
 * @author Piotr Mionskowski
 */
@AutoConfiguration
@ImportAutoConfiguration(
    DatastoreEventPublicationAutoConfiguration::class
)
class DatastoreModulithAutoConfiguration