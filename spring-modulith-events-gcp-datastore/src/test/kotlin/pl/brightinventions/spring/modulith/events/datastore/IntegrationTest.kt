package pl.brightinventions.spring.modulith.events.datastore

import com.google.api.gax.core.CredentialsProvider
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.Credentials
import com.google.cloud.datastore.Datastore
import com.google.cloud.datastore.admin.v1.DatastoreAdminClient
import com.google.cloud.datastore.admin.v1.DatastoreAdminSettings
import com.google.cloud.firestore.v1.FirestoreAdminClient
import com.google.cloud.firestore.v1.FirestoreAdminSettings
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import java.net.URI
import java.util.function.Supplier

@SpringBootTest(args = ["--debug"])
@ActiveProfiles("tests")
@ContextConfiguration(initializers = [DatastoreEmulatorContextInitializer::class])
annotation class IntegrationTest

@SpringBootTest(args = ["--debug"])
@ActiveProfiles(profiles = ["tests", "tests-e2e"])
annotation class E2ETest


@SpringBootConfiguration
@EnableAutoConfiguration
class ModulithEventsDatastoreTestApplication {
    @Bean
    @ConditionalOnProperty(
        "spring.cloud.gcp.credentials.location",
        havingValue = "MISSING_VALUE_PLACEHOLDER",
        matchIfMissing = true
    )
    fun credentialsProvider(): CredentialsProvider = FixedCredentialsProvider.create(
        EmulatorCredentials()
    )

    @Bean
    fun firestoreAdminClient(
        datastore: Supplier<Datastore>,
        credentialsProvider: CredentialsProvider
    ): FirestoreAdminClient {
        return FirestoreAdminClient.create(
            FirestoreAdminSettings.newBuilder()
                .setCredentialsProvider(credentialsProvider)
                .apply {
                    val datastoreHost = datastore.get().options.host
                    if (!datastoreHost.startsWith("https")) {
                        setEndpoint(datastoreHost)
                        setTransportChannelProvider(
                            DatastoreAdminSettings.defaultGrpcTransportProviderBuilder()
                                .setChannelConfigurator { it.usePlaintext() }
                                .build())
                    }

                }

                .build()
        )
    }
}

class EmulatorCredentials : Credentials() {
    override fun getAuthenticationType(): String? = null
    override fun getRequestMetadata(p0: URI?): Map<String, List<String>> {
        return emptyMap()
    }

    override fun hasRequestMetadata(): Boolean = true
    override fun hasRequestMetadataOnly(): Boolean = false
    override fun refresh(): Unit = Unit
}
