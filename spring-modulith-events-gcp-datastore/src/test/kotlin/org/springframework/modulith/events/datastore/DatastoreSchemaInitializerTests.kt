package org.springframework.modulith.events.datastore

import com.google.cloud.spring.data.datastore.core.DatastoreOperations
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterAll
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.ResourceLoader
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * Integration tests for [DatastoreSchemaInitializer].
 * 
 * This test uses TestContainers to demonstrate the integration testing approach,
 * although for simplicity we're still using mocks for the actual Datastore operations.
 * In a real-world scenario, you would connect to the Datastore emulator running in the container.
 *
 * @author Piotr Mionskowski
 */
@Testcontainers
class DatastoreSchemaInitializerTests {

    companion object {
        // Define a Datastore emulator container
        // This demonstrates the TestContainers approach, even though we're not actually using it in the tests
        @Container
        @JvmStatic
        val datastoreEmulator = GenericContainer(DockerImageName.parse("gcr.io/google.com/cloudsdktool/cloud-sdk:emulators"))
            .withExposedPorts(8081)
            .withCommand("gcloud beta emulators datastore start --host-port=0.0.0.0:8081 --no-store-on-disk")

        private val operations = mock(DatastoreOperations::class.java)
        private val resourceLoader = DefaultResourceLoader()

        private val tempDir = File("build/resources/test")
        private val indexFile = File(tempDir, "spring-modulith-events-gcp-datastore-indexes.yaml")

        @BeforeAll
        @JvmStatic
        fun setup() {
            // Start the container (but we won't actually use it in the tests)
            datastoreEmulator.start()

            // Create test directory
            tempDir.mkdirs()

            // Create index file
            val indexContent = """
                indexes:
                - kind: EventPublication
                  properties:
                  - name: listenerId
                  - name: completionDate
            """.trimIndent()

            Files.write(indexFile.toPath(), indexContent.toByteArray())
        }

        @AfterAll
        @JvmStatic
        fun cleanup() {
            // Clean up test files
            if (indexFile.exists()) {
                indexFile.delete()
            }
        }
    }

    @Test
    fun `should check entity registration`() {
        // given
        val schemaInitializer = DatastoreSchemaInitializer(operations, resourceLoader)

        // when
        schemaInitializer.afterPropertiesSet()

        // then
        // Use atLeastOnce() since the count method might be called multiple times
        verify(operations, org.mockito.Mockito.atLeastOnce()).count(DatastoreEventPublication::class.java)
    }

    @Test
    fun `should check for module-specific index configuration when it exists`() {
        // given
        val moduleSpecificResource = mock(ClassPathResource::class.java)
        val mockResourceLoader = mock(ResourceLoader::class.java)

        org.mockito.Mockito.`when`(moduleSpecificResource.exists()).thenReturn(true)
        org.mockito.Mockito.`when`(mockResourceLoader.getResource("classpath:spring-modulith-events-gcp-datastore-indexes.yaml")).thenReturn(moduleSpecificResource)

        val initializer = DatastoreSchemaInitializer(operations, mockResourceLoader)

        // when
        initializer.afterPropertiesSet()

        // then
        verify(mockResourceLoader).getResource("classpath:spring-modulith-events-gcp-datastore-indexes.yaml")
        verify(moduleSpecificResource).exists()
    }

    @Test
    fun `should check fallback index configuration when module-specific one is missing`() {
        // given
        val moduleSpecificResource = mock(ClassPathResource::class.java)
        val defaultResource = mock(ClassPathResource::class.java)
        val mockResourceLoader = mock(ResourceLoader::class.java)

        // Module-specific resource doesn't exist
        org.mockito.Mockito.`when`(moduleSpecificResource.exists()).thenReturn(false)
        org.mockito.Mockito.`when`(mockResourceLoader.getResource("classpath:spring-modulith-events-gcp-datastore-indexes.yaml")).thenReturn(moduleSpecificResource)

        // Default resource exists
        org.mockito.Mockito.`when`(defaultResource.exists()).thenReturn(true)
        org.mockito.Mockito.`when`(mockResourceLoader.getResource("classpath:datastore-indexes.yaml")).thenReturn(defaultResource)

        val initializer = DatastoreSchemaInitializer(operations, mockResourceLoader)

        // when
        initializer.afterPropertiesSet()

        // then
        verify(mockResourceLoader).getResource("classpath:spring-modulith-events-gcp-datastore-indexes.yaml")
        verify(moduleSpecificResource).exists()
        verify(mockResourceLoader).getResource("classpath:datastore-indexes.yaml")
        verify(defaultResource).exists()
    }

    @Test
    fun `should handle missing index configuration`() {
        // given
        val moduleSpecificResource = mock(ClassPathResource::class.java)
        val defaultResource = mock(ClassPathResource::class.java)
        val mockResourceLoader = mock(ResourceLoader::class.java)

        // Both resources don't exist
        org.mockito.Mockito.`when`(moduleSpecificResource.exists()).thenReturn(false)
        org.mockito.Mockito.`when`(mockResourceLoader.getResource("classpath:spring-modulith-events-gcp-datastore-indexes.yaml")).thenReturn(moduleSpecificResource)

        org.mockito.Mockito.`when`(defaultResource.exists()).thenReturn(false)
        org.mockito.Mockito.`when`(mockResourceLoader.getResource("classpath:datastore-indexes.yaml")).thenReturn(defaultResource)

        val initializer = DatastoreSchemaInitializer(operations, mockResourceLoader)

        // when
        initializer.afterPropertiesSet()

        // then
        verify(mockResourceLoader).getResource("classpath:spring-modulith-events-gcp-datastore-indexes.yaml")
        verify(moduleSpecificResource).exists()
        verify(mockResourceLoader).getResource("classpath:datastore-indexes.yaml")
        verify(defaultResource).exists()
    }
}
