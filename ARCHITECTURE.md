# Project Architecture: `partner-insurers-registry-service`

This document details the software architecture of the `partner-insurers-registry-service`, which is based on the **Explicit Architecture** pattern (also known as Hexagonal, Onion, or Clean Architecture).

## 1. Core Principles

The primary goal of this architecture is to separate the core business logic from the technical infrastructure. This creates a system that is:

- **Independent of Frameworks:** The core logic does not depend on Spring Boot or any other framework.
- **Testable:** Business rules can be tested in isolation, without requiring a running database or web server.
- **Independent of UI:** The user interface (in this case, a REST API) can be swapped out without changing the core application.
- **Independent of Database:** The core domain is not tied to a specific database technology like PostgreSQL.
- **Independent of External Services:** The application is decoupled from external services like RabbitMQ.

The fundamental rule is the **Dependency Rule**: *source code dependencies can only point inwards*. Nothing in an inner layer can know anything at all about something in an outer layer.

```mermaid
graph TD
    subgraph Presentation
        A[REST Controllers / DTOs]
    end
    subgraph Infrastructure
        B[PostgreSQL (R2DBC)]
        C[RabbitMQ Publisher]
    end
    subgraph Application
        D[Application Services <br/> (Use Cases)]
        E[Commands & Queries]
    end
    subgraph Domain
        F[Entities & Aggregates]
        G[Repository Interfaces]
        H[Domain Events]
    end

    A --> D
    B --> G
    C --> D
    D --> F
    D --> G
    D --> H
```

## 2. Architectural Layers

The project is divided into four main concentric layers:

### a. Domain Layer (Innermost)

This is the heart of the application. It contains the enterprise-wide business rules and entities.

- **Entities & Aggregates:** (`PartnerInsurer`, `Contact`, etc.) These are the core business objects with their own identity and lifecycle. They contain the most critical business logic and validation rules.
- **Repository Interfaces:** (`PartnerInsurerRepository`, etc.) These define the contracts (ports) for data persistence. The domain layer defines *what* it needs from persistence, but not *how* it's implemented.
- **Domain Events:** (`PartnerInsurerCreatedEvent`, etc.) These represent significant state changes within the domain that other parts of the system might be interested in.
- **Value Objects:** (`TaxIdentificationNumber`, etc.) Objects that represent a descriptive aspect of the domain with no conceptual identity.

### b. Application Layer

This layer orchestrates the use cases of the application. It does not contain business logic but directs the domain entities to perform their roles.

- **Application Services:** These services execute the application's use cases. For example, `CreatePartnerInsurerHandler` orchestrates the creation of a new partner insurer.
- **Commands & Queries (CQRS):**
    - **Commands** (`CreatePartnerInsurerCommand`): Represent an intent to change the state of the system. They are handled by command handlers that use the domain entities to perform the change.
    - **Queries** (`GetPartnerInsurerQuery`): Represent a request for data. They are handled by query handlers that typically bypass the domain entities and fetch data directly from the persistence layer for efficiency. This separation simplifies the model and improves performance.
- **Ports:** This layer defines interfaces for outbound ports, such as event publishers (`DomainEventPublisher`).

### c. Infrastructure Layer

This layer contains the concrete implementations of the interfaces defined in the application and domain layers. It is the "glue" that connects the core application to the outside world.

- **Persistence Adapters:** (`R2dbcPartnerInsurerRepository`) Implements the repository interfaces from the domain layer using R2DBC to connect to the PostgreSQL database.
- **Messaging Adapters:** (`RabbitMqEventPublisher`) Implements the event publisher interface from the application layer to send domain events to a RabbitMQ exchange.
- **Configuration:** Spring Boot configuration, security settings, etc.

### d. Presentation Layer (Outermost)

This layer is responsible for interacting with the outside world. In this service, it is a REST API.

- **REST Controllers:** (`PartnerInsurerController`) Expose the application's functionality via HTTP endpoints. They handle incoming requests, validate them, and map them to application layer commands or queries.
- **Data Transfer Objects (DTOs):** (`CreatePartnerInsurerRequest`, `PartnerInsurerDto`) These are simple objects used to transfer data between the presentation layer and the application layer. They ensure that the internal domain model is not exposed to the outside world.

## 3. Communication Flow (Example: Creating a Partner)

1.  A `POST /partner-insurers` request hits the `PartnerInsurerController`.
2.  The controller validates the incoming `CreatePartnerInsurerRequest` DTO.
3.  The controller creates a `CreatePartnerInsurerCommand` and dispatches it to the application layer.
4.  The `CreatePartnerInsurerHandler` receives the command.
5.  The handler uses a domain factory or the `PartnerInsurer` aggregate to create a new partner insurer instance, enforcing all business rules.
6.  The handler uses the `PartnerInsurerRepository` interface (the port) to persist the new aggregate. The infrastructure layer's R2DBC implementation (the adapter) handles the actual database insertion.
7.  The handler publishes a `PartnerInsurerCreatedEvent` using the `DomainEventPublisher` interface. The RabbitMQ adapter in the infrastructure layer sends the event.
8.  The controller returns a success response to the client.
