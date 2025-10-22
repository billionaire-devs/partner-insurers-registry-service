# Architecture Analysis: Event Publishing Design

## Current State Analysis

### Two Event Publishing Systems

Your codebase currently has **TWO SEPARATE** event publishing mechanisms:

#### 1. **SpringEventPublisher** (In-Memory, Synchronous)
- **Purpose**: Dispatch events to in-process handlers
- **Implementation**: `SpringEventPublisher` implements `EventPublisher` interface
- **Handlers**: `DomainEventHandler<T>` implementations
- **Scope**: Same JVM, same transaction context
- **Use Case**: Internal side effects (logging, read model updates, local workflows)

#### 2. **DomainEventPublisher** (Outbox, Asynchronous)
- **Purpose**: Persist events to outbox for external systems
- **Implementation**: Direct class (not implementing `EventPublisher`)
- **Handlers**: RabbitMQ consumers in external services
- **Scope**: Cross-service, eventual consistency
- **Use Case**: External integration, microservice communication

---

## The Problem: Architectural Confusion

### ❌ Issues with Current Design

1. **Naming Confusion**
   - `EventPublisher` interface suggests generic event publishing
   - `DomainEventPublisher` doesn't implement `EventPublisher`
   - Both handle `DomainEvent` but serve different purposes

2. **Unused Components**
   - `SpringEventPublisher` is **NOT CURRENTLY USED** anywhere
   - `EventPublisher` interface has no active usage
   - Event handlers (`PartnerInsurerCreatedEventHandler`) are **ORPHANED**

3. **Pattern Mixing**
   - Trying to combine **Event Notification** pattern with **Event-Carried State Transfer**
   - Mixing synchronous and asynchronous event handling
   - Unclear separation of concerns

4. **Microservice Anti-Pattern**
   - In-process event handlers in a microservice context
   - Should rely on message broker for event distribution
   - Violates bounded context isolation

---

## Architectural Decision Required

### Option 1: **Pure Outbox Pattern** (RECOMMENDED) ✅

**Remove in-process event handling entirely. Use only outbox for all events.**

#### What to Keep:
- ✅ `DomainEventPublisher` (outbox)
- ✅ `OutboxMessageProcessor`
- ✅ `OutboxRepository`
- ✅ RabbitMQ infrastructure

#### What to Remove:
- ❌ `SpringEventPublisher`
- ❌ `EventPublisher` interface
- ❌ `DomainEventHandler` interface
- ❌ `PartnerInsurerCreatedEventHandler`
- ❌ `PartnerInsurerStatusChangedEventHandler`

#### Architecture:
```
Command Handler
    ↓
Domain Logic (events collected)
    ↓
DomainEventPublisher → Outbox Table
    ↓
OutboxProcessor → RabbitMQ
    ↓
External Services (consume events)
```

#### Benefits:
- ✅ **Simplicity**: One event publishing mechanism
- ✅ **Consistency**: All events go through outbox
- ✅ **Reliability**: Guaranteed delivery via outbox
- ✅ **Microservice-friendly**: Clear bounded context separation
- ✅ **Scalability**: Async by default
- ✅ **Testability**: Easy to test with outbox table

#### When to Use Internal Handlers:
**NEVER in a microservice architecture.** If you need internal side effects:
- Use **Application Services** for orchestration
- Use **Domain Services** for complex domain logic
- Use **Saga Pattern** for distributed transactions
- Let **external services** consume events and call back if needed

---

### Option 2: **Hybrid Pattern** (NOT RECOMMENDED) ⚠️

**Keep both systems but clarify their roles.**

#### What to Keep:
- ✅ `DomainEventPublisher` (outbox) - for external events
- ✅ `SpringEventPublisher` - for internal events
- ✅ Both handler types

#### What to Change:
- Rename `EventPublisher` → `InternalEventPublisher`
- Rename `SpringEventPublisher` → `InProcessEventPublisher`
- Create clear distinction between internal/external events
- Add `@Internal` annotation to mark internal-only events

#### Architecture:
```
Command Handler
    ↓
Domain Logic (events collected)
    ↓
    ├─→ InProcessEventPublisher → DomainEventHandlers (internal)
    └─→ DomainEventPublisher → Outbox → RabbitMQ (external)
```

#### Problems:
- ❌ **Complexity**: Two systems to maintain
- ❌ **Confusion**: When to use which publisher?
- ❌ **Testing**: Need to test both paths
- ❌ **Transaction Issues**: Internal handlers run in same transaction
- ❌ **Coupling**: Internal handlers couple domain to infrastructure
- ❌ **Microservice Anti-Pattern**: Violates bounded context isolation

---

### Option 3: **Event Sourcing** (OVERKILL) ⚠️

**Store all events as source of truth.**

#### Not Recommended Because:
- ❌ Massive architectural change
- ❌ Requires event store infrastructure
- ❌ Complex event replay mechanisms
- ❌ Overkill for current requirements
- ❌ Steep learning curve

---

## Recommended Architecture: Pure Outbox Pattern

### Clean Architecture Layers

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                        │
│  (Controllers, DTOs, API Contracts)                         │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                   Application Layer                          │
│  • Command Handlers (orchestration)                         │
│  • Query Handlers (read operations)                         │
│  • Application Services                                     │
│  • DTOs, Mappers                                            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                             │
│  • Aggregates (PartnerInsurer)                              │
│  • Domain Events (PartnerInsurerCreatedEvent)               │
│  • Value Objects (DomainEntityId, TaxId)                    │
│  • Domain Services                                          │
│  • Repository Interfaces (Ports)                            │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  Infrastructure Layer                        │
│  • Repository Implementations (R2DBC)                       │
│  • DomainEventPublisher (Outbox)                            │
│  • OutboxProcessor (RabbitMQ)                               │
│  • Database Migrations                                      │
└─────────────────────────────────────────────────────────────┘
```

### Event Flow (Recommended)

```
1. API Request → Controller
2. Controller → Command Handler
3. Command Handler:
   a. Create/Update Aggregate (events collected)
   b. Save Aggregate (transaction starts)
   c. Publish events to Outbox (same transaction)
   d. Commit transaction
4. OutboxProcessor (separate process):
   a. Poll outbox table
   b. Publish to RabbitMQ
   c. Mark as processed
5. External Services:
   a. Consume from RabbitMQ
   b. Process event
   c. Optionally call back via API
```

---

## Pattern Alignment Analysis

### ✅ Clean Architecture
- **Dependency Rule**: Domain doesn't depend on infrastructure ✅
- **Use Cases**: Command/Query handlers ✅
- **Entities**: Aggregates ✅
- **Interfaces**: Repository ports ✅

### ✅ Hexagonal Architecture (Ports & Adapters)
- **Ports**: Repository interfaces, Command/Query interfaces ✅
- **Adapters**: R2DBC repositories, REST controllers ✅
- **Domain Core**: Pure business logic ✅

### ✅ Domain-Driven Design (DDD)
- **Aggregates**: PartnerInsurer ✅
- **Value Objects**: DomainEntityId, TaxId ✅
- **Domain Events**: PartnerInsurerCreatedEvent ✅
- **Bounded Context**: Partner Insurers ✅
- **Ubiquitous Language**: Domain terms in code ✅

### ✅ Event-Driven Architecture (EDA)
- **Event Publishing**: Via outbox ✅
- **Async Communication**: RabbitMQ ✅
- **Eventual Consistency**: Accepted ✅
- **Event Sourcing**: Not needed ❌

### ✅ Transactional Outbox Pattern
- **Atomic Persistence**: Aggregate + Events ✅
- **Guaranteed Delivery**: Outbox processor ✅
- **At-Least-Once**: Idempotent consumers ✅

### ❌ Event Notifications (REMOVE)
- **In-Process Handlers**: Not needed in microservices ❌
- **SpringEventPublisher**: Remove ❌
- **DomainEventHandler**: Remove ❌

### ✅ Microservices
- **Bounded Context**: Clear separation ✅
- **Independent Deployment**: Yes ✅
- **Database per Service**: Yes (R2DBC) ✅
- **Async Communication**: RabbitMQ ✅
- **No Shared Libraries**: Domain is internal ✅

---

## Recommended Changes

### Files to DELETE ❌

```
src/main/kotlin/.../core/infrastructure/events/SpringEventPublisher.kt
src/main/kotlin/.../core/domain/EventPublisher.kt
src/main/kotlin/.../core/domain/DomainEventHandler.kt
src/main/kotlin/.../registry/application/events/PartnerInsurerCreatedEventHandler.kt
src/main/kotlin/.../registry/application/events/PartnerInsurerStatusChangedEventHandler.kt
```

### Files to KEEP ✅

```
src/main/kotlin/.../core/infrastructure/events/DomainEventPublisher.kt
src/main/kotlin/.../core/application/ports/input/OutboxMessageProcessor.kt
src/main/kotlin/.../core/application/ports/output/OutboxRepository.kt
src/main/kotlin/.../core/infrastructure/outbox/OutboxMessagesTable.kt
src/main/kotlin/.../core/infrastructure/outbox/config/OutboxConfig.kt
```

### Rename for Clarity (Optional)

```
DomainEventPublisher → OutboxEventPublisher
```

---

## When You WOULD Need In-Process Handlers

### Monolithic Application
If this were a **monolith** (not microservice):
- ✅ Keep `SpringEventPublisher` for internal handlers
- ✅ Use for updating read models in same database
- ✅ Use for triggering internal workflows
- ✅ Keep transaction boundaries clear

### CQRS with Separate Read Models
If implementing **CQRS** with read models:
- ✅ Use in-process handlers to update read models
- ✅ Keep read models in same database
- ✅ Ensure handlers run in same transaction

### Complex Domain Workflows
If you have **complex sagas** within bounded context:
- ✅ Use domain services instead
- ✅ Use application services for orchestration
- ❌ Don't use event handlers for this

---

## Current Handler Analysis

### PartnerInsurerCreatedEventHandler
```kotlin
override suspend fun handle(event: PartnerInsurerCreatedEvent) {
    println("Partner insurer created: ID: ${event.aggregateId}, ...")
}
```

**Purpose**: Just logging  
**Recommendation**: ❌ **DELETE** - Use proper logging framework in command handler

### PartnerInsurerStatusChangedEventHandler
```kotlin
override suspend fun handle(event: PartnerInsurerStatusChangedEvent) {
    when (event.newStatus) {
        "ACTIVE" -> handleActivation(event)
        "SUSPENDED" -> handleSuspension(event)
        "INACTIVE" -> handleDeactivation(event)
    }
}
```

**Purpose**: Side effects based on status  
**Recommendation**: ❌ **DELETE** - Move logic to:
1. **Domain Service** if it's domain logic
2. **Application Service** if it's orchestration
3. **External Service** consuming RabbitMQ events

---

## Migration Path

### Step 1: Verify No Usage
```bash
# Search for SpringEventPublisher usage
grep -r "SpringEventPublisher" src/
grep -r "EventPublisher" src/
grep -r "DomainEventHandler" src/
```

### Step 2: Delete Unused Files
```bash
rm src/main/kotlin/.../SpringEventPublisher.kt
rm src/main/kotlin/.../EventPublisher.kt
rm src/main/kotlin/.../DomainEventHandler.kt
rm src/main/kotlin/.../PartnerInsurerCreatedEventHandler.kt
rm src/main/kotlin/.../PartnerInsurerStatusChangedEventHandler.kt
```

### Step 3: Move Handler Logic
- Move logging to command handlers
- Move business logic to domain/application services
- Create external services to consume RabbitMQ events if needed

### Step 4: Update Documentation
- Update architecture diagrams
- Update developer guides
- Clarify event publishing strategy

---

## Final Recommendation

### 🎯 **DELETE** the following components:

1. ❌ `SpringEventPublisher` - Not used, adds complexity
2. ❌ `EventPublisher` interface - Not needed
3. ❌ `DomainEventHandler` interface - Wrong pattern for microservices
4. ❌ `PartnerInsurerCreatedEventHandler` - Just logging
5. ❌ `PartnerInsurerStatusChangedEventHandler` - Move logic elsewhere

### ✅ **KEEP** the following:

1. ✅ `DomainEventPublisher` - Core of outbox pattern
2. ✅ `OutboxMessageProcessor` - Processes outbox
3. ✅ `OutboxRepository` - Persistence
4. ✅ All outbox infrastructure

### 📝 **REASONING**:

**In a microservice architecture:**
- Each service has its own bounded context
- Services communicate via events (RabbitMQ)
- Internal side effects should be handled by:
  - Domain services (domain logic)
  - Application services (orchestration)
  - External services (consuming events)
- In-process event handlers create tight coupling
- Outbox pattern ensures reliable event delivery

**Your architecture is:**
- ✅ Clean Architecture
- ✅ Hexagonal Architecture  
- ✅ DDD
- ✅ EDA (via outbox + RabbitMQ)
- ✅ Microservices
- ❌ NOT Event Notifications (remove this pattern)

The in-process event handling components are **architectural debt** from an earlier design that doesn't fit your current microservice architecture.

---

## Conclusion

**DELETE the in-process event handling system.** It adds complexity without value in a microservice architecture. Your outbox pattern is correctly implemented and sufficient for all event publishing needs.

Focus on:
1. **One way to publish events**: Outbox
2. **One way to consume events**: RabbitMQ
3. **Clear bounded contexts**: No shared handlers
4. **Explicit orchestration**: Application services, not event handlers
