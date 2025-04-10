package pl.brightinventions.spring.modulith.examples.audit

import org.springframework.modulith.events.ApplicationModuleListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import pl.brightinventions.spring.modulith.examples.product.ProductCreated

/**
 * Listener for product-related events.
 * Creates audit logs when products are created.
 *
 * @author Piotr Mionskowski
 */
@Component
class ProductEventListener(private val auditLogService: AuditLogService) {

    /**
     * Handles ProductCreated events by creating an audit log.
     */
    @ApplicationModuleListener
    fun handleProductCreated(event: ProductCreated) {
        auditLogService.createAuditLog(
            action = "CREATED",
            entityType = "PRODUCT",
            entityId = event.productId,
            details = "Product '${event.productName}' was created at ${event.timestamp}"
        )
    }
}