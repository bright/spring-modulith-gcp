# Spring Modulith Events GCP Datastore

This module provides integration between Spring Modulith Events and Google Cloud Platform (GCP) Datastore.

## Features

- Persistent event publication storage using GCP Datastore
- Automatic configuration for Spring Boot applications
- Support for transactional event publication

## Getting Started

### Prerequisites

- Java 21 or later
- Spring Boot 3.4.4 or later
- GCP Datastore access

### Installation

Add the following dependency to your project:

```gradle
implementation("pl.brightinventions.spring.modulith:spring-modulith-events-gcp-datastore")
```

## Configuration

The module is auto-configured when the required dependencies are present.

### Datastore Indexes

For efficient querying of event publications, it's necessary to create indexes in Datastore. You can create these indexes using the gcloud CLI with the provided `spring-modulith-events-gcp-datastore-indexes.yaml` file:

```bash
gcloud datastore indexes create spring-modulith-events-gcp-datastore-indexes.yaml
```

The `spring-modulith-events-gcp-datastore-indexes.yaml` file contains the following indexes:

```yaml
# Datastore indexes for Spring Modulith Events
#
# This file defines the indexes needed for efficient querying of event publications.
# For more information on Datastore indexes, see:
# https://cloud.google.com/appengine/docs/standard/java/datastore/indexes

indexes:
  # Index for finding incomplete publications
  - kind: EventPublication
    properties:
      - name: completionDate
        direction: asc
      - name: publicationDate
        direction: asc

  # Index for finding publications by listener ID
  - kind: EventPublication
    properties:
      - name: listenerId
        direction: asc
      - name: publicationDate
        direction: asc

  # Index for finding completed publications
  - kind: EventPublication
    properties:
      - name: completionDate
        direction: desc
```

These indexes are essential for the proper functioning of the event publication system, especially for queries that filter or sort by completion date, publication date, or listener ID.

## Usage

Once configured, the module will automatically handle the persistence of event publications in GCP Datastore.

For more information on using Spring Modulith Events, refer to the [Spring Modulith documentation](https://docs.spring.io/spring-modulith/docs/current/reference/html/).