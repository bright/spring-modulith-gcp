package pl.brightinventions.spring.modulith.examples.product

import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for managing products.
 *
 * @author Piotr Mionskowski
 */
@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val eventPublisher: ApplicationEventPublisher
) {

    /**
     * Creates a new product and publishes a ProductCreated event.
     */
    @Transactional
    fun createProduct(name: String, description: String, price: Double): Product {
        val product = Product(
            name = name,
            description = description,
            price = price
        )
        
        val savedProduct = productRepository.save(product)
        
        // Publish the ProductCreated event
        eventPublisher.publishEvent(
            ProductCreated(
                productId = savedProduct.id,
                productName = savedProduct.name
            )
        )
        
        return savedProduct
    }
    
    /**
     * Retrieves a product by its ID.
     */
    fun getProductById(id: String): Product? {
        return productRepository.findById(id).orElse(null)
    }
    
    /**
     * Retrieves all products.
     */
    fun getAllProducts(): List<Product> {
        return productRepository.findAll().toList()
    }
}