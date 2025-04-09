package org.springframework.modulith.starter.datastore

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.modulith.events.datastore.DatastoreEventPublicationAutoConfiguration

/**
 * Auto-configuration for Spring Modulith with GCP Datastore.
 *
 * @author Your Name
 */
@AutoConfiguration
@ImportAutoConfiguration(
    DatastoreEventPublicationAutoConfiguration::class
)
class DatastoreModulithAutoConfiguration