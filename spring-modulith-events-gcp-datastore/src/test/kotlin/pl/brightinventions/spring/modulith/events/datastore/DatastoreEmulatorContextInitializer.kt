package pl.brightinventions.spring.modulith.events.datastore

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

/**
 * Integration tests for Datastore.
 *
 * This test uses TestContainers to demonstrate the integration testing approach,
 * although for simplicity we're still using mocks for the actual Datastore operations.
 * In a real-world scenario, you would connect to the Datastore emulator running in the container.
 *
 * @author Piotr Mionskowski
 */

class DatastoreEmulatorContextInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        val datastoreEmulator =
            GenericContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:emulators"))
                .withExposedPorts(8081)
                .withCommand("gcloud emulators firestore start --host-port=0.0.0.0:8081 --database-mode=datastore-mode")

        // shutting down the container on context close
        applicationContext.beanFactory.registerSingleton(
            "datastore",
            datastoreEmulator
        )

        datastoreEmulator.start()

        TestPropertyValues.of(
            mapOf(
                "spring.cloud.gcp.datastore.host" to "${datastoreEmulator.host}:${datastoreEmulator.firstMappedPort}",
            )
        ).applyTo(applicationContext)
    }
}
