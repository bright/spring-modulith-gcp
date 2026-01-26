package pl.brightinventions.spring.modulith.events.datastore

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.AutoConfigurations
import org.springframework.boot.test.context.runner.ApplicationContextRunner

/**
 * Tests for [DatastoreRetryConfiguration].
 *
 * @author Adam Waniak
 */
class DatastoreRetryConfigurationTests {

    private val contextRunner = ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(DatastoreRetryConfiguration::class.java))

    @Nested
    inner class RetryConfigurationTests {

        @Test
        fun `should enable retry by default`() {
            // given & when
            contextRunner.run { context ->
                // then
                assertThat(context).hasSingleBean(DatastoreRetryConfiguration::class.java)
                assertThat(context).hasSingleBean(DatastoreRetryListener::class.java)
            }
        }

        @Test
        fun `should disable retry when property is set to false`() {
            // given & when
            contextRunner
                .withPropertyValues("pl.brightinventions.spring.modulith.events.datastore.retry.enabled=false")
                .run { context ->
                    // then
                    assertThat(context).doesNotHaveBean(DatastoreRetryConfiguration::class.java)
                    assertThat(context).doesNotHaveBean(DatastoreRetryListener::class.java)
                }
        }

        @Test
        fun `should enable retry when property is explicitly set to true`() {
            // given & when
            contextRunner
                .withPropertyValues("pl.brightinventions.spring.modulith.events.datastore.retry.enabled=true")
                .run { context ->
                    // then
                    assertThat(context).hasSingleBean(DatastoreRetryConfiguration::class.java)
                    assertThat(context).hasSingleBean(DatastoreRetryListener::class.java)
                }
        }
    }
}
