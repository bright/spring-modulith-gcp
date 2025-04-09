package pl.brightinventions.spring.modulith.events.datastore

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Configuration properties for GCP Datastore event publication.
 *
 * @author Piotr Mionskowski
 */
@ConfigurationProperties("pl.brightinventions.spring.modulith.events.datastore")
class DatastoreConfigurationProperties {
    /**
     * Whether to enable schema initialization.
     */
    var schemaInitialization: SchemaInitialization = SchemaInitialization()

    /**
     * Configuration for index file.
     */
    var indexFile: IndexFile = IndexFile()
    /**
     * Configuration for schema initialization.
     */
    class SchemaInitialization {
        /**
         * Whether to enable schema initialization.
         */
        var enabled: Boolean = false
    }

    /**
     * Configuration for index file.
     */
    class IndexFile {
        /**
         * The name of the index file, including the module name to avoid classpath conflicts.
         * Default is "spring-modulith-events-gcp-datastore-indexes.yaml".
         */
        var name: String = "spring-modulith-events-gcp-datastore-indexes.yaml"
    }
}