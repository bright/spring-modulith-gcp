@file:OptIn(ExperimentalAtomicApi::class)

package pl.brightinventions.spring.modulith.starter.datastore

import com.google.cloud.spring.data.datastore.core.mapping.Entity
import com.google.cloud.spring.data.datastore.repository.DatastoreRepository
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.nulls.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.annotation.Id
import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.modulith.events.core.EventPublicationRepository
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import pl.brightinventions.spring.modulith.events.datastore.IntegrationTest
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch

@SpringBootApplication
class TestApplication

@Entity
data class Product(@Id val id: String, val name: String)

@Repository
interface ProductDataStoreRepository : DatastoreRepository<Product, String> {
    fun findProductById(id: String): Product?
}

@Service
class BusinessService(
    private val productsRepository: ProductDataStoreRepository, private val eventPublisher: ApplicationEventPublisher
) {
    @Transactional
    fun someImportantBusinessGoal(): Product {
        val product = productsRepository.save(Product("p.${counter.incrementAndFetch()}", "First"))

        eventPublisher.publishEvent(ProductCreated(product.id))

        return product
    }

    companion object {
        val counter = AtomicInt(0)
    }
}

data class ProductCreated(val productId: String)

abstract class ProductCreatedListenerTestHelper {
    open val latches = ConcurrentHashMap<String, CountDownLatch>()
    open val eventsReceived = CopyOnWriteArrayList<ProductCreated>()

    open fun receiveEvent(productCreated: ProductCreated) {
        val latch = waiterFor(productCreated.productId)

        eventsReceived.add(productCreated)

        latch.countDown()
    }

    open fun waitFor(productId: String) {
        val latch = waiterFor(productId)
        latch.await()
    }

    private fun waiterFor(productId: String): CountDownLatch {
        return latches.computeIfAbsent(productId) { CountDownLatch(1) }
    }
}

@Component
class SuccessfulProductCreatedListener : ProductCreatedListenerTestHelper() {

    @ApplicationModuleListener
    fun onProductCreated(productCreated: ProductCreated) {
        receiveEvent(productCreated)
    }

}

@Component
class FirstFailedProductCreatedListener : ProductCreatedListenerTestHelper() {

    @ApplicationModuleListener
    fun onProductCreated(productCreated: ProductCreated) {
        receiveEvent(productCreated)

        throw RuntimeException("Some failure")
    }

}

@Component
class SecondFailedProductCreatedListener : ProductCreatedListenerTestHelper() {

    @ApplicationModuleListener
    fun onProductCreated(productCreated: ProductCreated) {
        receiveEvent(productCreated)

        throw RuntimeException("Some failure")
    }

}

@IntegrationTest
class DataStoreApplicationModuleListenerTests @Autowired constructor(
    val businessService: BusinessService,
    val productsRepository: ProductDataStoreRepository,
    val successfulProductCreatedListener: SuccessfulProductCreatedListener,
    val firstFailedProductCreatedListener: FirstFailedProductCreatedListener,
    val secondFailedProductCreatedListener: SecondFailedProductCreatedListener,
    val eventPublicationRepository: EventPublicationRepository,
) {
    @Test
    fun `can save product`() {
        // when
        businessService.someImportantBusinessGoal()

        val product = productsRepository.findProductById("p.1")

        product.shouldNotBeNull()
    }

    @Test
    fun `invokes successful listener product`() {
        // when
        val product = businessService.someImportantBusinessGoal()

        successfulProductCreatedListener.waitFor(product.id)

        successfulProductCreatedListener.eventsReceived.shouldHaveSingleElement {
            it.productId == product.id
        }
    }

    @Test
    fun `invokes first failed listener product`() {
        // when
        val product = businessService.someImportantBusinessGoal()

        firstFailedProductCreatedListener.waitFor(product.id)

        firstFailedProductCreatedListener.eventsReceived.shouldHaveSingleElement {
            it.productId == product.id
        }
        eventPublicationRepository.findIncompletePublications()
            .shouldHaveSingleElement {
                val eventMatches = (it.event as ProductCreated).productId == product.id
                val firstFailedListenerMatches =
                    it.targetIdentifier.value.contains(FirstFailedProductCreatedListener::class.java.name)
                eventMatches && firstFailedListenerMatches
            }
    }

    @Test
    fun `invokes second failed listener product`() {
        // when
        val product = businessService.someImportantBusinessGoal()

        secondFailedProductCreatedListener.waitFor(product.id)

        secondFailedProductCreatedListener.eventsReceived.shouldHaveSingleElement {
            it.productId == product.id
        }

        eventPublicationRepository.findIncompletePublications()
            .shouldHaveSingleElement {
                val eventMatches = (it.event as ProductCreated).productId == product.id
                val firstFailedListenerMatches =
                    it.targetIdentifier.value.contains(SecondFailedProductCreatedListener::class.java.name)
                eventMatches && firstFailedListenerMatches
            }
    }

}