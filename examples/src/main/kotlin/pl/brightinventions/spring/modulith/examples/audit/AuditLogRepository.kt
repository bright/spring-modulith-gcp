package pl.brightinventions.spring.modulith.examples.audit

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository
import org.springframework.stereotype.Repository

/**
 * Repository for managing AuditLog entities in GCP Datastore.
 *
 * @author Piotr Mionskowski
 */
@Repository
interface AuditLogRepository : DatastoreRepository<AuditLog, String>