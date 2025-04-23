# Spring Modulith Events GCP Datastore

This module provides an enhanced version of DatastoreTransactionManager. The improved DatastoreTransactionManager
supports more transaction propagation modes. The propagation modes are required by Spring Modulith.

See https://github.com/GoogleCloudPlatform/spring-cloud-gcp/issues/3727 for details.

## Features

- Improved DatastoreTransactionManager

### Prerequisites

- Java 21 or later
- Spring Boot 3.4.4 or later
- GCP Datastore access

### Installation

Add the following dependency to your project:

```gradle
implementation("pl.brightinventions.spring.modulith:spring-gcp-datastore")
```

## Configuration

The module is auto-configured when the required dependencies are present.

