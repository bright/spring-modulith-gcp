package pl.brightinventions.spring.modulith.events.datastore

import com.google.cloud.spring.data.datastore.core.mapping.Entity
import com.google.cloud.spring.data.datastore.core.mapping.Field
import org.springframework.modulith.events.core.PublicationTargetIdentifier
import org.springframework.modulith.events.core.TargetEventPublication
import java.time.Instant
import java.util.*

/**
 * GCP Datastore entity to represent event publications.
 *
 * @author Piotr Mionskowski
 */
@Entity(name = "EventPublication")
class DatastoreEventPublication(
    @Field(name = "id") val id: UUID,
    @Field(name = "publicationDate") val publicationDate: Instant,
    @Field(name = "listenerId") val listenerId: String,
    @Field(name = "serializedEvent") val serializedEvent: String,
    @Field(name = "eventType") val eventType: String,
    @Field(name = "completionDate") var completionDate: Instant? = null
) {

    /**
     * Marks the publication as completed with the given completion date.
     */
    fun markCompleted(completionDate: Instant) {
        this.completionDate = completionDate
    }

    /**
     * Returns whether the publication is completed.
     */
    fun isCompleted(): Boolean {
        return completionDate != null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DatastoreEventPublication) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

/**
 * Adapter to expose a [DatastoreEventPublication] as [TargetEventPublication].
 */
class DatastoreEventPublicationAdapter(
    private val publication: DatastoreEventPublication,
    private val eventType: Class<*>,
    private val eventSupplier: () -> Any
) : TargetEventPublication {

    override fun getIdentifier(): UUID = publication.id

    override fun getEvent(): Any = eventSupplier()

    override fun getTargetIdentifier(): PublicationTargetIdentifier = 
        PublicationTargetIdentifier.of(publication.listenerId)

    override fun getPublicationDate(): Instant = publication.publicationDate

    override fun getCompletionDate(): Optional<Instant> = 
        Optional.ofNullable(publication.completionDate)

    override fun isPublicationCompleted(): Boolean = publication.isCompleted()

    override fun markCompleted(instant: Instant) {
        publication.markCompleted(instant)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DatastoreEventPublicationAdapter) return false
        return publication == other.publication
    }

    override fun hashCode(): Int {
        return publication.hashCode()
    }
}