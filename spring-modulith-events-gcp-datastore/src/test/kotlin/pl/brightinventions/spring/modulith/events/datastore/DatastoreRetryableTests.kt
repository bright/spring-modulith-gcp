package pl.brightinventions.spring.modulith.events.datastore

import com.google.cloud.datastore.DatastoreException
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.atomic.AtomicInteger

/**
 * Tests for [DatastoreRetryable] annotation.
 *
 * @author Adam Waniak
 */
@SpringBootTest(
    classes = [
        ModulithEventsDatastoreTestApplication::class,
        DatastoreRetryableTests.TestConfiguration::class
    ],
    properties = [
        "pl.brightinventions.spring.modulith.events.datastore.retry.max-attempts=3",
        "pl.brightinventions.spring.modulith.events.datastore.retry.initial-interval=10",
        "pl.brightinventions.spring.modulith.events.datastore.retry.max-interval=50",
        "pl.brightinventions.spring.modulith.events.datastore.retry.multiplier=1.5"
    ]
)
class DatastoreRetryableTests {

    @Autowired
    lateinit var retryableService: RetryableTestService

    @Test
    fun `should retry on DatastoreException and eventually succeed`() {
        // given
        retryableService.reset()
        retryableService.failCount = 2

        // when
        val result = retryableService.operationWithRetryableFailure()

        // then
        result shouldBe "success"
        retryableService.attemptCount.get() shouldBe 3
    }

    @Test
    fun `should not retry when operation succeeds immediately`() {
        // given
        retryableService.reset()
        retryableService.failCount = 0

        // when
        val result = retryableService.operationWithRetryableFailure()

        // then
        result shouldBe "success"
        retryableService.attemptCount.get() shouldBe 1
    }

    @Test
    fun `should exhaust retries and throw exception`() {
        // given
        retryableService.reset()
        retryableService.failCount = 10

        // when & then
        try {
            retryableService.operationWithRetryableFailure()
            throw AssertionError("Expected DatastoreException to be thrown")
        } catch (e: DatastoreException) {
            retryableService.attemptCount.get() shouldBe 3
        }
    }

    @Configuration
    class TestConfiguration {

        @Bean
        fun retryableTestService(): RetryableTestService {
            return RetryableTestService()
        }
    }
}

open class RetryableTestService {

    open val attemptCount: AtomicInteger = AtomicInteger(0)
    open var failCount: Int = 0

    open fun reset() {
        attemptCount.set(0)
        failCount = 0
    }

    @DatastoreRetryable
    open fun operationWithRetryableFailure(): String {
        val currentAttempt = attemptCount.incrementAndGet()
        if (currentAttempt <= failCount) {
            throw DatastoreException(10, "too much contention on these datastore entities", "ABORTED")
        }
        return "success"
    }
}
