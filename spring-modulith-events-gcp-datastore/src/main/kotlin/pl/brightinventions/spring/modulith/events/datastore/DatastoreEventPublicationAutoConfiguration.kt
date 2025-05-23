package pl.brightinventions.spring.modulith.events.datastore

import com.google.cloud.spring.data.datastore.core.DatastoreOperations
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.env.Environment
import org.springframework.modulith.events.config.EventPublicationAutoConfiguration
import org.springframework.modulith.events.config.EventPublicationConfigurationExtension
import org.springframework.modulith.events.core.EventSerializer
import org.springframework.modulith.events.support.CompletionMode
import org.springframework.transaction.PlatformTransactionManager

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
    ): DatastoreEventPublicationRepository {
        return DatastoreEventPublicationRepository(
            operations, serializer, CompletionMode.from(environment)
        )
    }
}
