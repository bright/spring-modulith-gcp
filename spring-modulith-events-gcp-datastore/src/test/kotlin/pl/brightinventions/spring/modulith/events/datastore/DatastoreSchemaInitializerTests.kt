package pl.brightinventions.spring.modulith.events.datastore

import com.google.cloud.datastore.Datastore
import com.google.cloud.firestore.v1.FirestoreAdminClient
import com.google.cloud.spring.autoconfigure.datastore.GcpDatastoreProperties
import com.google.firestore.admin.v1.CollectionGroupName
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.result.shouldBeSuccess
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.TestPropertySource


@IntegrationTest
@TestPropertySource(
    properties = ["pl.brightinventions.spring.modulith.events.datastore.schema-initialization.enabled=true"]
)
class DatastoreSchemaInitializerTests @Autowired constructor(
    private val schemaInitializer: DatastoreSchemaInitializer,
) {

    @Test
    fun `should check entity registration`() {
        // given
        // when
        val result = runCatching { schemaInitializer.afterPropertiesSet() }

        // then
        result.shouldBeSuccess()
    }
}

@E2ETest
@TestPropertySource(
    properties = [
        "pl.brightinventions.spring.modulith.events.datastore.schema-initialization.enabled=true",
        "spring.cloud.gcp.credentials.location=file://\${HOME}/.config/gcloud/legacy_credentials/piotr.mionskowski@brightinventions.pl/adc.json"
    ]
)
class DatastoreSchemaInitializerE2ETests @Autowired constructor(
    private val schemaInitializer: DatastoreSchemaInitializer,
    private val firestoreAdminClient: FirestoreAdminClient,
    private val gcpDatastoreProperties: GcpDatastoreProperties,
    private val datastore: Datastore
) {

    @Test
    fun `should check entity registration`() {
        // given
        // when
        val result = runCatching { schemaInitializer.afterPropertiesSet() }

        // then
        result.shouldBeSuccess()
    }

    @Test
    fun `indexes are initialised properly`() {
        // when
        val result = runCatching { schemaInitializer.afterPropertiesSet() }
        result.shouldBeSuccess()

        // then
        val eventPublicationIndexes = firestoreAdminClient.listIndexes(
            CollectionGroupName.of(datastore.options.projectId, gcpDatastoreProperties.databaseId, "EventPublication")
        ).iteratePages().flatMap { it.iterateAll() }.toList()
        eventPublicationIndexes.shouldNotBeEmpty()
        val indexesByFieldNamesSet = eventPublicationIndexes.associateBy {
            it.allFields.keys.map { it.name }.toSet()
        }
        indexesByFieldNamesSet[setOf("completionDate", "publicationDate")].shouldNotBeNull()
        indexesByFieldNamesSet[setOf("listenerId", "publicationDate")].shouldNotBeNull()
        indexesByFieldNamesSet[setOf("completionDate")].shouldNotBeNull()
    }
}