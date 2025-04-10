package pl.brightinventions.spring.modulith.examples.audit

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Service for managing audit logs.
 *
 * @author Piotr Mionskowski
 */
@Service
class AuditLogService(private val auditLogRepository: AuditLogRepository) {

    /**
     * Creates a new audit log entry.
     */
    @Transactional
    fun createAuditLog(action: String, entityType: String, entityId: String, details: String): AuditLog {
        val auditLog = AuditLog(
            action = action,
            entityType = entityType,
            entityId = entityId,
            details = details
        )
        
        return auditLogRepository.save(auditLog)
    }
    
    /**
     * Retrieves all audit logs.
     */
    fun getAllAuditLogs(): List<AuditLog> {
        return auditLogRepository.findAll().toList()
    }
}