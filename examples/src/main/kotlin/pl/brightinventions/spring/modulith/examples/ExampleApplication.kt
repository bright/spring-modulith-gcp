package pl.brightinventions.spring.modulith.examples

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.modulith.Modulith

/**
 * Example Spring Boot application demonstrating Spring Modulith with GCP Datastore.
 *
 * @author Piotr Mionskowski
 */
@SpringBootApplication
@Modulith
class ExampleApplication

fun main(args: Array<String>) {
    runApplication<ExampleApplication>(*args)
}