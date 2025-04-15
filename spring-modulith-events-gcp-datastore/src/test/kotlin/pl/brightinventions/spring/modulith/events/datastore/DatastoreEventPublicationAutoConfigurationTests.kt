package pl.brightinventions.spring.modulith.events.datastore

import com.google.cloud.spring.data.datastore.core.DatastoreOperations
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.modulith.events.core.EventSerializer

/**
 * Tests for [DatastoreEventPublicationAutoConfiguration].
 *
 * @author Piotr Mionskowski
 */
class DatastoreEventPublicationAutoConfigurationTests {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(DatastoreEventPublicationAutoConfiguration::class.java))
        .withUserConfiguration(TestConfiguration::class.java)

    @Configuration
    class TestConfiguration {
        @Bean
        fun datastoreOperations(): DatastoreOperations = mock(DatastoreOperations::class.java)

        @Bean
        fun eventSerializer(): EventSerializer = mock(EventSerializer::class.java)
    }

    @Nested
    inner class RepositoryConfigurationTests {

        @Test
        fun `should register repository`() {
            // given & when
            contextRunner.run { context ->
                // then
                assertThat(context).hasSingleBean(DatastoreEventPublicationRepository::class.java)
            }
        }
    }
}
