package pl.brightinventions.spring.modulith.examples.product

import java.time.Instant

/**
 * Event published when a new product is created.
 *
 * @author Piotr Mionskowski
 */
data class ProductCreated(
    val productId: String,
    val productName: String,
    val timestamp: Instant = Instant.now()
)