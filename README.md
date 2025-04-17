# Bright Spring Modulith GCP

This project aims to integrate [Spring Modulith](https://spring.io/projects/spring-modulith) with Google Cloud Platform
services. The initial focus is on supporting GCP Datastore through a custom, work-in-progress transaction manager that
overcomes the limitations of the official GCP Spring library.

## Modules

- **spring-gcp-datastore**  
  Core module providing GCP Datastore integration with Spring.

- **spring-modulith-events-gcp-datastore**  
  Integration of Spring Modulith Events with GCP Datastore for event storage.

- **spring-modulith-starter-gcp-datastore**  
  Spring Boot starter for easy integration of Spring Modulith with GCP Datastore.

## Project Goals

- **Spring Modulith & GCP Datastore:**  
  Implement a dedicated transaction manager to seamlessly integrate Spring Modulith with GCP Datastore, removing the
  limitations of the existing manager provided by the official library.

- **Future Enhancements:**  
  Support for GCP Firebase and Spanner is planned for future development.

## Guidelines

Please refer to the [guidelines](.junie/guidelines.md) for further information on code style and testing practices. Note
that the project adheres to Spring Boot Starter conventions and Gradle's Kotlin DSL using version 8.13, as well as JVM
target 21.

## Release Process

This project uses GitHub Actions for continuous integration and automated releases to Maven Central.

### Continuous Integration

The CI workflow runs on every push to the main branch and on pull requests. It builds the project and runs all tests.

### Releasing to Maven Central

To release a new version to Maven Central:

1. Go to the "Actions" tab in the GitHub repository
2. Select the "Release" workflow
3. Click "Run workflow"
4. Enter the release version (e.g., "0.1.0") and the next development version (e.g., "0.1.1-SNAPSHOT")
5. Click "Run workflow"

The workflow will:
- Update the version in the build files
- Build and test the project
- Sign the artifacts with GPG
- Publish to Maven Central
- Create a Git tag for the release
- Update the version to the next development version

### Required Secrets

The following secrets must be configured in the GitHub repository:

- `SONATYPE_USERNAME`: Sonatype OSSRH username
- `SONATYPE_PASSWORD`: Sonatype OSSRH password
- `GPG_SIGNING_KEY`: GPG private key for signing artifacts
- `GPG_SIGNING_PASSWORD`: Password for the GPG key

## Contributors

- Bright Inventions Team  
  For more information about the company behind this project, please
  visit [Bright Inventions](https://brightinventions.pl).
