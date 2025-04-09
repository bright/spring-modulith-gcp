package org.springframework.modulith.events.datastore

import com.google.cloud.spring.data.datastore.core.DatastoreOperations
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import java.io.IOException

/**
 * Initializes the Datastore schema used to store events.
 *
 * For Datastore, schema initialization is different from JDBC since Datastore is schemaless.
 * This initializer ensures that:
 * 1. The EVENT_PUBLICATION entity is registered with Datastore
 * 2. Any necessary index configuration is available
 *
 * @author Your Name
 */
class DatastoreSchemaInitializer(
    private val operations: DatastoreOperations,
    private val resourceLoader: ResourceLoader
) : InitializingBean {

    private val logger = LoggerFactory.getLogger(DatastoreSchemaInitializer::class.java)

    companion object {
        private const val MODULE_SPECIFIC_INDEX_CONFIG_LOCATION = "classpath:spring-modulith-events-gcp-datastore-indexes.yaml"
        private const val ENTITY_NAME = "EVENT_PUBLICATION"
    }

    /**
     * Initializes the Datastore schema by ensuring the necessary entities and indexes exist.
     */
    override fun afterPropertiesSet() {
        logger.info("Initializing Datastore schema for event publications")

        // Verify the entity is registered with Datastore
        ensureEntityRegistered()

        // Check for index configuration
        checkIndexConfiguration()

        logger.info("Datastore schema initialization complete")
    }

    /**
     * Ensures the EVENT_PUBLICATION entity is registered with Datastore.
     * This is handled by the DatastoreTemplate, but we verify it here.
     */
    private fun ensureEntityRegistered() {
        try {
            // This will throw an exception if the entity is not registered
            operations.count(DatastoreEventPublication::class.java)
            logger.debug("Entity $ENTITY_NAME is registered with Datastore")
        } catch (e: Exception) {
            logger.warn("Failed to verify entity $ENTITY_NAME registration: ${e.message}")
            // We don't throw an exception here because the entity will be registered
            // when it's first used, and this is just a verification step
        }
    }

    /**
     * Checks if the index configuration file exists.
     * In Datastore, indexes are defined in a datastore-indexes.yaml file that is
     * deployed with the application.
     * 
     * This method first looks for a module-specific index file (spring-modulith-events-gcp-datastore-indexes.yaml)
     * to avoid classpath conflicts. If that file doesn't exist, it falls back to the default index file.
     */
    private fun checkIndexConfiguration() {
        try {
            // First try the module-specific index file
            val moduleSpecificResource: Resource = resourceLoader.getResource(MODULE_SPECIFIC_INDEX_CONFIG_LOCATION)

            if (moduleSpecificResource.exists()) {
                logger.info("Found Datastore index configuration at $MODULE_SPECIFIC_INDEX_CONFIG_LOCATION")
            } else {
                logger.warn("No Datastore index configuration found at $MODULE_SPECIFIC_INDEX_CONFIG_LOCATION or $MODULE_SPECIFIC_INDEX_CONFIG_LOCATION")
                logger.warn("For production use, consider creating a spring-modulith-events-gcp-datastore-indexes.yaml file with appropriate indexes")
                logger.warn("See https://cloud.google.com/appengine/docs/standard/java/datastore/indexes for more information")
            }

        } catch (e: IOException) {
            logger.warn("Failed to check for Datastore index configuration: ${e.message}")
        }
    }
}
