package pl.brightinventions.spring.modulith.examples.audit

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

/**
 * REST controller for audit log operations.
 *
 * @author Piotr Mionskowski
 */
@RestController
@RequestMapping("/api/audit-logs")
class AuditLogController(private val auditLogService: AuditLogService) {

    /**
     * Retrieves all audit logs.
     */
    @GetMapping
    fun getAllAuditLogs(): List<AuditLogResponse> {
        return auditLogService.getAllAuditLogs().map { AuditLogResponse.fromAuditLog(it) }
    }
}

/**
 * Response object for audit log data.
 */
data class AuditLogResponse(
    val id: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val details: String,
    val timestamp: String
) {
    companion object {
        fun fromAuditLog(auditLog: AuditLog): AuditLogResponse {
            return AuditLogResponse(
                id = auditLog.id,
                action = auditLog.action,
                entityType = auditLog.entityType,
                entityId = auditLog.entityId,
                details = auditLog.details,
                timestamp = auditLog.timestamp.toString()
            )
        }
    }
}