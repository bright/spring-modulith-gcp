package pl.brightinventions.spring.modulith.events.datastore

import com.google.api.gax.core.CredentialsProvider
import com.google.cloud.datastore.Datastore
import com.google.cloud.firestore.v1.FirestoreAdminClient
import com.google.cloud.firestore.v1.FirestoreAdminSettings
import com.google.cloud.spring.data.datastore.core.DatastoreOperations
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.core.io.ResourceLoader
import org.springframework.modulith.events.config.EventPublicationAutoConfiguration
import org.springframework.modulith.events.config.EventPublicationConfigurationExtension
import org.springframework.modulith.events.core.EventSerializer
import org.springframework.modulith.events.support.CompletionMode
import org.springframework.transaction.PlatformTransactionManager
import java.util.function.Supplier

/**
 * Auto-configuration for GCP Datastore based event publication.
 *
 * @author Piotr Mionskowski
 */
@AutoConfiguration
@AutoConfigureBefore(EventPublicationAutoConfiguration::class)
@ConditionalOnClass(DatastoreOperations::class)
@ConditionalOnBean(DatastoreOperations::class)
@EnableConfigurationProperties(DatastoreConfigurationProperties::class)
class DatastoreEventPublicationAutoConfiguration : EventPublicationConfigurationExtension {

    @Bean
    @ConditionalOnMissingBean
    fun datastoreEventPublicationRepository(
        operations: DatastoreOperations,
        serializer: EventSerializer,
        environment: Environment,
        platformTransactionManager: PlatformTransactionManager,
    ): DatastoreEventPublicationRepository {
        return DatastoreEventPublicationRepository(
            operations, serializer, CompletionMode.from(environment), platformTransactionManager
        )
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(
        name = ["pl.brightinventions.spring.modulith.events.datastore.schema-initialization.enabled"],
        havingValue = "true"
    )
    fun firestoreAdminClient(datastore: Datastore, credentialsProvider: CredentialsProvider): FirestoreAdminClient {
        return FirestoreAdminClient.create(
            FirestoreAdminSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .build()
        )
    }

    @Bean
    @ConditionalOnProperty(
        name = ["pl.brightinventions.spring.modulith.events.datastore.schema-initialization.enabled"],
        havingValue = "true"
    )
    fun datastoreSchemaInitializer(
        operations: DatastoreOperations, datastore: Supplier<Datastore>, resourceLoader: ResourceLoader,
        firestoreAdminClient: FirestoreAdminClient
    ): DatastoreSchemaInitializer {
        return DatastoreSchemaInitializer(operations, datastore, resourceLoader, firestoreAdminClient)
    }
}
