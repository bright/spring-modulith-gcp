package pl.brightinventions.spring.modulith.examples.product

import com.google.cloud.spring.data.datastore.core.mapping.Entity
import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.UUID

/**
 * Product entity stored in GCP Datastore.
 *
 * @author Piotr Mionskowski
 */
@Entity
data class Product(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val price: Double,
    val createdAt: Instant = Instant.now()
)