# How to write tests

Please separate blocks of test code using comments like

- // given
- // when
- // then

Prefer integration style tests. Use testcontainers when needed.
Strongly avoid using mocks. Do not differentiate between unit and integration style test in the test file name

# Spring Modulith

The build/spring-modulith contains the source of Spring Modulith.
Do not modify it. Use it as a documentation source.

# Spring Boot

Follow Spring Boot Starter conventions.
Rely on auto configurations and configuration properties.
Use Spring Boot version 3.4.4

## Version management

Rely on Gradle's BOM support. Do not use Spring Boot's dependency management plugin instead add implementation platform
dependency using Spring Boot BOM.

Whenever there's a need for dependency in build.gradle.kts use Gradle's version catalogue.

## GCP Spring Boot

Use the following BOM artefact to manage GCP dependencies:
`com.google.cloud:spring-cloud-gcp-dependencies:6.1.1`
For datastore support rely on `com.google.cloud:spring-cloud-gcp-starter-data-datastore`

# Kotlin

Use the latest version of Kotlin 2.1.20.
Follow idiomatic Kotlin guidelines.

# Gradle

Use Gradle version 8.13
Use Gradle's Kotlin script e.g. build.gradle.kts files.
The top-level module should not contain any source files.

## Version catalogue

Use Gradle's version catalogue defined in settings.gradle.kts

# JVM

Target JVM 21

# Comments

If you're generating Javadoc comments, then use Bright Inventions as the author tag Java Docs.

Do not add comments in the method implementations when the code is self-documenting.
Other than documentation only add comments for some special cases, workarounds, shortcuts, todos. 





