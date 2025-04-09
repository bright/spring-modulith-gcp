package org.springframework.modulith.events.datastore

import com.google.cloud.datastore.NullValue
import com.google.cloud.datastore.Query
import com.google.cloud.datastore.StructuredQuery.CompositeFilter
import com.google.cloud.datastore.StructuredQuery.OrderBy
import com.google.cloud.datastore.StructuredQuery.PropertyFilter
import com.google.cloud.spring.data.datastore.core.DatastoreOperations
import com.google.cloud.spring.data.datastore.core.DatastoreTemplate
import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.modulith.events.core.EventPublicationRepository
import org.springframework.modulith.events.core.EventSerializer
import org.springframework.modulith.events.core.PublicationTargetIdentifier
import org.springframework.modulith.events.core.TargetEventPublication
import org.springframework.modulith.events.support.CompletionMode
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.Assert
import org.springframework.util.ClassUtils
import java.time.Instant
import java.util.*
import java.util.function.Supplier

/**
 * GCP Datastore implementation of [EventPublicationRepository].
 *
 * @author Piotr Mionskowski
 */
@Transactional
class DatastoreEventPublicationRepository(
    private val operations: DatastoreOperations,
    private val serializer: EventSerializer,
    private val completionMode: CompletionMode
) : EventPublicationRepository, BeanClassLoaderAware {

    private var classLoader: ClassLoader? = null

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    @Transactional
    override fun create(publication: TargetEventPublication): TargetEventPublication {
        val event = publication.getEvent()
        val serializedEvent = serializer.serialize(event)
        val entity = DatastoreEventPublication(
            publication.getIdentifier(),
            publication.getPublicationDate(),
            publication.getTargetIdentifier().toString(),
            serializedEvent.toString(),
            event.javaClass.name
        )

        operations.save(entity)

        return publication
    }

    @Transactional
    override fun markCompleted(event: Any, identifier: PublicationTargetIdentifier, completionDate: Instant) {
        val serializedEvent = serializer.serialize(event).toString()

        // Create a query with filters for serializedEvent, listenerId, and completionDate being null
        val serializedEventFilter = PropertyFilter.eq("serializedEvent", serializedEvent)
        val listenerIdFilter = PropertyFilter.eq("listenerId", identifier.toString())
        val completionDateFilter = PropertyFilter.isNull("completionDate")

        // Combine filters with AND
        val compositeFilter = CompositeFilter.and(serializedEventFilter, listenerIdFilter, completionDateFilter)

        // Execute query
        val publications = operations.query(
            Query.newEntityQueryBuilder()
                .setKind("EventPublication")
                .setFilter(compositeFilter)
                .build(),
            DatastoreEventPublication::class.java
        )

        // Mark the first matching publication as completed
        publications.firstOrNull()?.let { publication ->
            markCompletedInternal(publication, completionDate)
        }
    }

    @Transactional
    override fun markCompleted(identifier: UUID, completionDate: Instant) {
        val publication = operations.findById(identifier, DatastoreEventPublication::class.java)

        if (publication != null) {
            markCompletedInternal(publication, completionDate)
        }
    }

    @Transactional
    override fun markCompleted(publication: TargetEventPublication, completionDate: Instant) {
        val entity = operations.findById(publication.getIdentifier(), DatastoreEventPublication::class.java)

        if (entity != null) {
            markCompletedInternal(entity, completionDate)
        }
    }

    private fun markCompletedInternal(publication: DatastoreEventPublication, completionDate: Instant) {
        publication.markCompleted(completionDate)
        operations.save(publication)
    }

    override fun findIncompletePublicationsByEventAndTargetIdentifier(
        event: Any,
        targetIdentifier: PublicationTargetIdentifier
    ): Optional<TargetEventPublication> {
        val serializedEvent = serializer.serialize(event).toString()

        // Create a query with filters for serializedEvent, listenerId, and completionDate being null
        val serializedEventFilter = PropertyFilter.eq("serializedEvent", serializedEvent)
        val listenerIdFilter = PropertyFilter.eq("listenerId", targetIdentifier.toString())
        val completionDateFilter = PropertyFilter.isNull("completionDate")

        // Combine filters with AND
        val compositeFilter = CompositeFilter.and(serializedEventFilter, listenerIdFilter, completionDateFilter)

        // Execute query with sorting by publicationDate
        val publications = operations.query(
            Query.newEntityQueryBuilder()
                .setKind("EventPublication")
                .setFilter(compositeFilter)
                .addOrderBy(OrderBy.asc("publicationDate"))
                .build(),
            DatastoreEventPublication::class.java
        )

        // Get the first publication or return empty if none found
        val publication = publications.firstOrNull()

        return if (publication == null) {
            Optional.empty()
        } else {
            try {
                val eventType = loadClass(publication.id, publication.eventType)
                Optional.of(createAdapter(publication, eventType))
            } catch (e: Exception) {
                Optional.empty()
            }
        }
    }

    override fun findCompletedPublications(): List<TargetEventPublication> {
        // Create a query with filter for completionDate not being null
        // Using PropertyFilter.neq with NullValue.of() as suggested
        val completionDateFilter = PropertyFilter.neq("completionDate", NullValue.of())

        // Execute query with sorting by publicationDate
        val publications = operations.query(
            Query.newEntityQueryBuilder()
                .setKind("EventPublication")
                .setFilter(completionDateFilter)
                .addOrderBy(OrderBy.asc("publicationDate"))
                .build(),
            DatastoreEventPublication::class.java
        )

        // Convert publications to TargetEventPublication
        return publications.mapNotNull { publication ->
            try {
                val eventType = loadClass(publication.id, publication.eventType)
                createAdapter(publication, eventType)
            } catch (e: Exception) {
                null
            }
        }.toList()
    }

    override fun findIncompletePublications(): List<TargetEventPublication> {
        // Create a query with filter for completionDate being null
        val completionDateFilter = PropertyFilter.isNull("completionDate")

        // Execute query with sorting by publicationDate
        val publications = operations.query(
            Query.newEntityQueryBuilder()
                .setKind("EventPublication")
                .setFilter(completionDateFilter)
                .addOrderBy(OrderBy.asc("publicationDate"))
                .build(),
            DatastoreEventPublication::class.java
        )

        // Convert publications to TargetEventPublication
        return publications.mapNotNull { publication ->
            try {
                val eventType = loadClass(publication.id, publication.eventType)
                createAdapter(publication, eventType)
            } catch (e: Exception) {
                null
            }
        }.toList()
    }

    override fun findIncompletePublicationsPublishedBefore(instant: Instant): List<TargetEventPublication> {
        // In a real implementation, we would use a query to find the publications
        // For simplicity, we'll just find all publications and filter in memory
        val allPublications = operations.findAll(DatastoreEventPublication::class.java)

        val publications = allPublications.filter { 
            it.completionDate == null && it.publicationDate.isBefore(instant)
        }.sortedBy { it.publicationDate }

        return publications.mapNotNull { publication ->
            try {
                val eventType = loadClass(publication.id, publication.eventType)
                createAdapter(publication, eventType)
            } catch (e: Exception) {
                null
            }
        }
    }

    @Transactional
    override fun deletePublications(identifiers: List<UUID>) {
        identifiers.forEach { id ->
            operations.deleteById(id, DatastoreEventPublication::class.java)
        }
    }

    @Transactional
    override fun deleteCompletedPublications() {
        // First, find all completed publications using the findCompletedPublications method
        // which uses PropertyFilter.neq with NullValue.of() to filter for non-null values
        val completedPublications = findCompletedPublications()

        // Extract the identifiers of the completed publications
        val identifiers = completedPublications.map { it.getIdentifier() }

        // Delete the publications by their identifiers
        if (identifiers.isNotEmpty()) {
            deletePublications(identifiers)
        }
    }

    @Transactional
    override fun deleteCompletedPublicationsBefore(instant: Instant) {
        // First, find all completed publications using the findCompletedPublications method
        // which uses PropertyFilter.neq with NullValue.of() to filter for non-null values
        // We still need to filter for date comparisons in memory as Datastore doesn't support this directly
        val completedPublications = findCompletedPublications()

        // Filter publications that were completed before the given instant
        val publicationsToDelete = completedPublications.filter { 
            it.getCompletionDate().isPresent && it.getCompletionDate().get().isBefore(instant)
        }

        // Extract the identifiers of the publications to delete
        val identifiers = publicationsToDelete.map { it.getIdentifier() }

        // Delete the publications by their identifiers
        if (identifiers.isNotEmpty()) {
            deletePublications(identifiers)
        }
    }

    private fun loadClass(id: UUID, className: String): Class<*> {
        Assert.notNull(classLoader, "ClassLoader must not be null!")

        try {
            return ClassUtils.forName(className, classLoader!!)
        } catch (e: Exception) {
            throw IllegalStateException("Could not load class $className for event publication $id", e)
        }
    }

    private fun createAdapter(publication: DatastoreEventPublication, eventType: Class<*>): DatastoreEventPublicationAdapter {
        val eventSupplier = Supplier {
            serializer.deserialize(publication.serializedEvent, eventType)
        }

        return DatastoreEventPublicationAdapter(
            publication,
            eventType,
            eventSupplier::get
        )
    }
}
