package pl.brightinventions.spring.modulith.examples.product

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for product operations.
 *
 * @author Piotr Mionskowski
 */
@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    /**
     * Creates a new product.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createProduct(@RequestBody request: CreateProductRequest): ProductResponse {
        val product = productService.createProduct(
            name = request.name,
            description = request.description,
            price = request.price
        )
        return ProductResponse.fromProduct(product)
    }

    /**
     * Retrieves a product by its ID.
     */
    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: String): ResponseEntity<ProductResponse> {
        val product = productService.getProductById(id)
        return if (product != null) {
            ResponseEntity.ok(ProductResponse.fromProduct(product))
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Retrieves all products.
     */
    @GetMapping
    fun getAllProducts(): List<ProductResponse> {
        return productService.getAllProducts().map { ProductResponse.fromProduct(it) }
    }
}

/**
 * Request object for creating a product.
 */
data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double
)

/**
 * Response object for product data.
 */
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val createdAt: String
) {
    companion object {
        fun fromProduct(product: Product): ProductResponse {
            return ProductResponse(
                id = product.id,
                name = product.name,
                description = product.description,
                price = product.price,
                createdAt = product.createdAt.toString()
            )
        }
    }
}