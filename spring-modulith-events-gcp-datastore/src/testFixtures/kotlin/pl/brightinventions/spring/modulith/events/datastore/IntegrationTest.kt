package pl.brightinventions.spring.modulith.events.datastore

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest(args = ["--debug"])
@ActiveProfiles("tests")
@ContextConfiguration(initializers = [DatastoreEmulatorContextInitializer::class])
annotation class IntegrationTest