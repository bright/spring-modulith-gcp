package pl.brightinventions.spring.modulith.events.datastore

import com.google.cloud.datastore.Datastore
import com.google.cloud.firestore.v1.FirestoreAdminClient
import com.google.cloud.spring.data.datastore.core.DatastoreOperations
import com.google.cloud.spring.data.datastore.core.DatastoreQueryOptions
import com.google.firestore.admin.v1.CollectionGroupName
import com.google.firestore.admin.v1.CreateIndexRequest
import com.google.firestore.admin.v1.Index
import com.google.firestore.admin.v1.Index.IndexField
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.Resource
import org.springframework.core.io.ResourceLoader
import org.yaml.snakeyaml.Yaml
import java.io.IOException
import java.util.function.Supplier

/**
 * Initializes the Datastore schema used to store events.
 *
 * For Datastore, schema initialization is different from JDBC since Datastore is schemaless.
 * This initializer ensures that:
 * 1. The EventPublication entity is registered with Datastore
 * 2. Any necessary index configuration is available
 *
 * @author Piotr Mionskowski
 */
class DatastoreSchemaInitializer(
    private val operations: DatastoreOperations,
    private val datastore: Supplier<Datastore>,
    private val resourceLoader: ResourceLoader,
    private val firestoreAdminClient: FirestoreAdminClient
) : InitializingBean {

    private val logger = LoggerFactory.getLogger(DatastoreSchemaInitializer::class.java)

    companion object {
        private const val MODULE_SPECIFIC_INDEX_CONFIG_LOCATION =
            "classpath:spring-modulith-events-gcp-datastore-indexes.yaml"
        private const val ENTITY_NAME = "EventPublication"
    }

    /**
     * Initializes the Datastore schema by ensuring the necessary entities and indexes exist.
     */
    override fun afterPropertiesSet() {
        logger.info("Initializing Datastore schema for event publications")

        ensureEntityRegistered()

        applyIndexConfiguration()

        logger.info("Datastore schema initialization complete")
    }

    /**
     * Ensures the EventPublication entity is registered with Datastore.
     * This is handled by the DatastoreTemplate, but we verify it here.
     */
    private fun ensureEntityRegistered() {
        try {
            // This will throw an exception if the entity is not registered
            operations.findAll(
                DatastoreEventPublication::class.java, DatastoreQueryOptions.Builder().setLimit(1).build()
            )
            logger.debug("Entity $ENTITY_NAME is registered with Datastore")
        } catch (e: Exception) {
            logger.warn("Failed to verify entity $ENTITY_NAME registration: ${e.message}")
            throw e
        }
    }

    private fun applyIndexConfiguration() {
        try {
            val moduleSpecificResource: Resource = resourceLoader.getResource(MODULE_SPECIFIC_INDEX_CONFIG_LOCATION)

            if (moduleSpecificResource.exists()) {
                logger.info("Found Datastore index configuration at $MODULE_SPECIFIC_INDEX_CONFIG_LOCATION")

                // Parse the YAML file to get index information
                val yaml = Yaml()
                val indexConfig = yaml.load<Map<String, Any>>(moduleSpecificResource.inputStream)

                @Suppress("UNCHECKED_CAST") val indexes =
                    indexConfig["indexes"] as? List<Map<String, Any>> ?: emptyList()

                if (indexes.isNotEmpty()) {
                    logger.info("Found ${indexes.size} indexes in configuration file")

                    // Log information about each index
                    indexes.forEachIndexed { _, inexProps ->
                        val kind = inexProps["kind"] as? String ?: "Unknown"

                        @Suppress("UNCHECKED_CAST") val properties =
                            inexProps["properties"] as? List<Map<String, Any>> ?: emptyList()

                        val indexProperties = properties.map { prop ->
                            val name =
                                prop["name"] as? String ?: throw IllegalArgumentException("Property 'name' is missing")
                            val direction = prop["direction"] as? String ?: "asc"
                            val indexDirection = if ("asc".equals(direction, ignoreCase = true)) {
                                IndexField.Order.ASCENDING
                            } else {
                                IndexField.Order.DESCENDING
                            }
                            IndexField.newBuilder().setFieldPath(name).setOrder(indexDirection).build()
                        }

                        val datastoreOptions = datastore.get().options
                        val result = firestoreAdminClient.createIndexAsync(
                            CreateIndexRequest.newBuilder().setParent(
                                CollectionGroupName.of(
                                    datastoreOptions.projectId,
                                    datastoreOptions.databaseId ?: "(default)",
                                    kind
                                ).toString()
                            ).setIndex(
                                Index.newBuilder().addAllFields(indexProperties)
                                    .setApiScope(Index.ApiScope.DATASTORE_MODE_API)
                                    .setQueryScope(Index.QueryScope.COLLECTION_GROUP)
                                    .build()
                            ).build()
                        )

                        logger.info("Requested index creation {}", result)
                    }
                } else {
                    logger.warn("No indexes found in configuration file")
                }
            } else {
                logger.warn("No Datastore index configuration found at $MODULE_SPECIFIC_INDEX_CONFIG_LOCATION")
                logger.warn("For production use, consider creating a spring-modulith-events-gcp-datastore-indexes.yaml file with appropriate indexes")
                logger.warn("See https://cloud.google.com/appengine/docs/standard/java/datastore/indexes for more information")
            }

        } catch (e: IOException) {
            logger.warn("Failed to check for Datastore index configuration: ${e.message}")
            throw e
        }
    }
}
