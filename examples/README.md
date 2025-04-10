# Spring Modulith GCP Datastore Example

This example demonstrates how to use Spring Modulith with GCP Datastore for event publication and handling.

## Overview

The application consists of two modules:

1. **Product Module**: Manages products and publishes events when products are created.
2. **Audit Module**: Listens for product events and creates audit logs.

## Requirements

- JDK 21
- Gradle 8.13+
- A Google Cloud Platform (GCP) project with Datastore enabled
- GCP credentials configured (either through environment variables, application properties, or a service account key file)

## Configuration

Before running the application, update the `application.properties` file with your GCP project ID:

```properties
spring.cloud.gcp.project-id=your-gcp-project-id
```

## Running the Application

To run the application:

```bash
./gradlew :examples:bootRun
```

## API Endpoints

The application exposes the following REST endpoints:

### Product API

- **Create a product**:
  ```
  POST /api/products
  ```
  Request body:
  ```json
  {
    "name": "Example Product",
    "description": "This is an example product",
    "price": 19.99
  }
  ```

- **Get all products**:
  ```
  GET /api/products
  ```

- **Get a product by ID**:
  ```
  GET /api/products/{id}
  ```

### Audit Log API

- **Get all audit logs**:
  ```
  GET /api/audit-logs
  ```

## How It Works

1. When a product is created through the REST API, the `ProductService` saves the product to GCP Datastore and publishes a `ProductCreated` event.
2. The `ProductEventListener` in the Audit module listens for `ProductCreated` events and creates an audit log entry in GCP Datastore.
3. The event publication and handling are managed by Spring Modulith, with events stored in GCP Datastore through the `spring-modulith-starter-gcp-datastore` module.

## Architecture

The application follows a modular architecture using Spring Modulith:

- Each module is a package under `pl.brightinventions.spring.modulith.examples`
- Modules communicate through events, not direct method calls
- Events are published using Spring's `ApplicationEventPublisher`
- Event listeners are annotated with `@ApplicationModuleListener`