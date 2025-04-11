package pl.brightinventions.spring.modulith.examples.audit

import com.google.cloud.spring.data.datastore.core.mapping.Entity
import org.springframework.data.annotation.Id
import java.time.Instant
import java.util.UUID

/**
 * AuditLog entity stored in GCP Datastore.
 *
 * @author Piotr Mionskowski
 */
@Entity(name = "AuditLog")
data class AuditLog(
    @Id
    val id: String = UUID.randomUUID().toString(),
    val action: String,
    val entityType: String,
    val entityId: String,
    val details: String,
    val timestamp: Instant = Instant.now()
)