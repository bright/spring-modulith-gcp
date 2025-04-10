package pl.brightinventions.spring.modulith.examples.product

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository
import org.springframework.stereotype.Repository

/**
 * Repository for managing Product entities in GCP Datastore.
 *
 * @author Piotr Mionskowski
 */
@Repository
interface ProductRepository : DatastoreRepository<Product, String>