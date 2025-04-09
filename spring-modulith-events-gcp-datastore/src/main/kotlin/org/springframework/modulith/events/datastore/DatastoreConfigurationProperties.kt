package org.springframework.modulith.events.datastore

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

/**
 * Configuration properties for GCP Datastore event publication.
 *
 * @author Your Name
 */
@ConfigurationProperties("spring.modulith.events.datastore")
class DatastoreConfigurationProperties @ConstructorBinding constructor(
    /**
     * Whether to enable schema initialization.
     */
    val schemaInitialization: SchemaInitialization = SchemaInitialization(),

    /**
     * Configuration for index file.
     */
    val indexFile: IndexFile = IndexFile()
) {
    /**
     * Configuration for schema initialization.
     */
    class SchemaInitialization {
        /**
         * Whether to enable schema initialization.
         */
        val enabled: Boolean = false
    }

    /**
     * Configuration for index file.
     */
    class IndexFile {
        /**
         * The name of the index file, including the module name to avoid classpath conflicts.
         * Default is "spring-modulith-events-gcp-datastore-indexes.yaml".
         */
        val name: String = "spring-modulith-events-gcp-datastore-indexes.yaml"
    }
}
