# Changelog

All notable changes to this project will be documented in this file.

## [1.0.0-beta.10](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.9...v1.0.0-beta.10) (2025-11-15)

### 🚀 Features

* add PIS-REG-122 - Partner contact soft deletion ([#47](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/47)) ([a526c37](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a526c371b98d4aca89f4bff853176ff5d6a91655))

## [1.0.0-alpha-PIS-REG-122.2](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-alpha-PIS-REG-122.1...v1.0.0-alpha-PIS-REG-122.2) (2025-11-15)

### 🐛 Bug Fixes

* **ci:** correct semantic-release exec command quoting ([223817d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/223817d51ca4f144d0e64482c1c44c5e8678e895))
* **ci:** correct semantic-release exec command quoting ([bd0262d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bd0262df87b0e67b97b4ff445a71d4095863b433))

## 1.0.0-alpha-PIS-REG-122.1 (2025-11-15)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### ✅ Tests

* add integration tests for contact deletion ([cca624f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/cca624f568ebeb93de2a070e60df34cbc5de3caf))
* fix delete contact handler deletion expectations` ([18dae4a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/18dae4ad05cc2a4b427dc938d2d6965babebdad3))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-beta.4 [skip ci] ([5aad6c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5aad6c090be1418318d012ba146b126aa8ead318)), closes [#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)
* **release:** 1.0.0-beta.5 [skip ci] ([ef65c16](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ef65c1621a84372ea66a25cfba3e96ff02931e39)), closes [#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)
* **release:** 1.0.0-beta.6 [skip ci] ([af45a0f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/af45a0f8fdc5fcb9f23478535de8d4aaa72bd26e)), closes [#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)
* **release:** 1.0.0-beta.7 [skip ci] ([0bfe60c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/0bfe60c286dbe3578b03b964637e09c8496ea5be)), closes [#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)
* **release:** 1.0.0-beta.8 [skip ci] ([d0ab245](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/d0ab24549c47e216a919a5e1512e085d1404ecd1)), closes [#44](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/44) [#45](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/45)
* **release:** 1.0.0-beta.9 [skip ci] ([353aa88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/353aa88e163ba35d60ed5af3402ea02f66372f3d)), closes [#46](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/46)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** merge release/1.0.0 back to develop - Partner Insurers Management Features ([#44](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/44)) ([e54510b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e54510b5acb9b9bc058f6e4c8a4420639e3eb292))
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* Add partner insurer contact management (PIS-REG-120) ([#45](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/45)) ([ecdd212](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ecdd21230b843978c9bc2c9561c0e0c04be57476))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* add PIS-REG-104 - Mise à jour d’un assureur partenaire ([#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)) ([9b38c68](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9b38c688f336f1ad8f662a337b86bfd4707bb984))
* Add PIS-REG-105 - Changement de statut ([#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)) ([88b31ce](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88b31ce5e86e189b92279c45d8e35a4cd9d25405))
* add PIS-REG-106 - Suppression logique d’un partenaire assureur ([#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)) ([f53d80d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f53d80d28975e57e6d56ec4f57a9c46fa4de0ab7))
* add PIS-REG-121 - Partner Contact Update ([#46](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/46)) ([5529181](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/552918148370f2035237129373761716e4b4b043))
* add soft-deletion for partner contacts ([f673a7f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f673a7fc047c458181c49a8d3ec789ab4a980389))
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## [1.0.0-beta.9](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.8...v1.0.0-beta.9) (2025-11-15)

### 🚀 Features

* add PIS-REG-121 - Partner Contact Update ([#46](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/46)) ([5529181](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/552918148370f2035237129373761716e4b4b043))

## 1.0.0-alpha-PIS-REG-121.1 (2025-11-15)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### ✅ Tests

* add comprehensive test suite for contact update functionality ([60f9a91](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/60f9a91c86be1bab343d570f10fea5a6492480b7))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* **contacts:** resolve response format and error handling in AddPartnerInsurerContactCommandHandler ([8838fa7](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8838fa75cf2cbe3fad4ed619c0d26ab35cb60f0d))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-beta.4 [skip ci] ([5aad6c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5aad6c090be1418318d012ba146b126aa8ead318)), closes [#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)
* **release:** 1.0.0-beta.5 [skip ci] ([ef65c16](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ef65c1621a84372ea66a25cfba3e96ff02931e39)), closes [#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)
* **release:** 1.0.0-beta.6 [skip ci] ([af45a0f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/af45a0f8fdc5fcb9f23478535de8d4aaa72bd26e)), closes [#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)
* **release:** 1.0.0-beta.7 [skip ci] ([0bfe60c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/0bfe60c286dbe3578b03b964637e09c8496ea5be)), closes [#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)
* **release:** 1.0.0-beta.8 [skip ci] ([d0ab245](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/d0ab24549c47e216a919a5e1512e085d1404ecd1)), closes [#44](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/44) [#45](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/45)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** merge release/1.0.0 back to develop - Partner Insurers Management Features ([#44](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/44)) ([e54510b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e54510b5acb9b9bc058f6e4c8a4420639e3eb292))
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* Add partner insurer contact management (PIS-REG-120) ([#45](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/45)) ([ecdd212](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ecdd21230b843978c9bc2c9561c0e0c04be57476))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* add PIS-REG-104 - Mise à jour d’un assureur partenaire ([#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)) ([9b38c68](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9b38c688f336f1ad8f662a337b86bfd4707bb984))
* Add PIS-REG-105 - Changement de statut ([#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)) ([88b31ce](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88b31ce5e86e189b92279c45d8e35a4cd9d25405))
* add PIS-REG-106 - Suppression logique d’un partenaire assureur ([#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)) ([f53d80d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f53d80d28975e57e6d56ec4f57a9c46fa4de0ab7))
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* implement contact update functionality per PIS-REG-121 ([c66d57c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c66d57cfeb573a7ed58b64327146637a5b1980f8))
* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## [1.0.0-beta.8](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.7...v1.0.0-beta.8) (2025-11-14)

### 🔧 Chores

* **release:** merge release/1.0.0 back to develop - Partner Insurers Management Features ([#44](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/44)) ([e54510b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e54510b5acb9b9bc058f6e4c8a4420639e3eb292))

### 🚀 Features

* Add partner insurer contact management (PIS-REG-120) ([#45](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/45)) ([ecdd212](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ecdd21230b843978c9bc2c9561c0e0c04be57476))

## 1.0.0-alpha-PIS-REG-120.1 (2025-11-14)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### ✅ Tests

* **integration:** add contact addition integration tests ([5d9e3da](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5d9e3da349e2823a1b2c3d78bb7823751150b0d4))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-beta.4 [skip ci] ([5aad6c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5aad6c090be1418318d012ba146b126aa8ead318)), closes [#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)
* **release:** 1.0.0-beta.5 [skip ci] ([ef65c16](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ef65c1621a84372ea66a25cfba3e96ff02931e39)), closes [#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)
* **release:** 1.0.0-beta.6 [skip ci] ([af45a0f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/af45a0f8fdc5fcb9f23478535de8d4aaa72bd26e)), closes [#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)
* **release:** 1.0.0-beta.7 [skip ci] ([0bfe60c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/0bfe60c286dbe3578b03b964637e09c8496ea5be)), closes [#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** merge release/1.0.0 back to develop - Partner Insurers Management Features ([#44](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/44)) ([e54510b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e54510b5acb9b9bc058f6e4c8a4420639e3eb292))
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* add PIS-REG-104 - Mise à jour d’un assureur partenaire ([#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)) ([9b38c68](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9b38c688f336f1ad8f662a337b86bfd4707bb984))
* Add PIS-REG-105 - Changement de statut ([#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)) ([88b31ce](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88b31ce5e86e189b92279c45d8e35a4cd9d25405))
* add PIS-REG-106 - Suppression logique d’un partenaire assureur ([#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)) ([f53d80d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f53d80d28975e57e6d56ec4f57a9c46fa4de0ab7))
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* implement PIS-REG-120 add partner insurer contact ([2e89bff](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2e89bffde2b9d31794b86c558461623641420f37))
* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## 1.0.0-rc-1-0-0.1 (2025-11-13)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### ✅ Tests

* add comprehensive release validation test suite ([2e14ce6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2e14ce69c682293f63ead80186c2c8e7e2d21aa1))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **build:** resolve GitHub Packages authentication during dependency resolution ([5d2faea](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5d2faeaccfa8c921c69856883d7f80b80b44e0ac))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-beta.4 [skip ci] ([5aad6c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5aad6c090be1418318d012ba146b126aa8ead318)), closes [#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)
* **release:** 1.0.0-beta.5 [skip ci] ([ef65c16](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ef65c1621a84372ea66a25cfba3e96ff02931e39)), closes [#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)
* **release:** 1.0.0-beta.6 [skip ci] ([af45a0f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/af45a0f8fdc5fcb9f23478535de8d4aaa72bd26e)), closes [#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)
* **release:** 1.0.0-beta.7 [skip ci] ([0bfe60c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/0bfe60c286dbe3578b03b964637e09c8496ea5be)), closes [#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* add PIS-REG-104 - Mise à jour d’un assureur partenaire ([#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)) ([9b38c68](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9b38c688f336f1ad8f662a337b86bfd4707bb984))
* Add PIS-REG-105 - Changement de statut ([#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)) ([88b31ce](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88b31ce5e86e189b92279c45d8e35a4cd9d25405))
* add PIS-REG-106 - Suppression logique d’un partenaire assureur ([#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)) ([f53d80d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f53d80d28975e57e6d56ec4f57a9c46fa4de0ab7))
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## [1.0.0-beta.7](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.6...v1.0.0-beta.7) (2025-11-13)

### 🚀 Features

* add PIS-REG-106 - Suppression logique d’un partenaire assureur ([#42](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/42)) ([f53d80d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f53d80d28975e57e6d56ec4f57a9c46fa4de0ab7))

## [1.0.0-alpha-PIS-REG-106.2](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-alpha-PIS-REG-106.1...v1.0.0-alpha-PIS-REG-106.2) (2025-11-13)

### 🐛 Bug Fixes

* **ci:** fix semantic-release GitHub plugin repository access error ([f84cc89](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f84cc89a5a4a28b31ee2eada2869da19ee1c17c3))

## 1.0.0-alpha-PIS-REG-106.1 (2025-11-13)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### ✅ Tests

* add comprehensive tests for partner insurer soft deletion ([2dfc44c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2dfc44c5b139c32e61e04ef8afe4999690e02c0c))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))
* upgrade shared-kernel to v1.3.0 ([1f3b759](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1f3b7596c94b9b5d5c1d9b2195bbd5dae9a7c706))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-beta.4 [skip ci] ([5aad6c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5aad6c090be1418318d012ba146b126aa8ead318)), closes [#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)
* **release:** 1.0.0-beta.5 [skip ci] ([ef65c16](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ef65c1621a84372ea66a25cfba3e96ff02931e39)), closes [#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)
* **release:** 1.0.0-beta.6 [skip ci] ([af45a0f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/af45a0f8fdc5fcb9f23478535de8d4aaa72bd26e)), closes [#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* add PIS-REG-104 - Mise à jour d’un assureur partenaire ([#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)) ([9b38c68](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9b38c688f336f1ad8f662a337b86bfd4707bb984))
* Add PIS-REG-105 - Changement de statut ([#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)) ([88b31ce](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88b31ce5e86e189b92279c45d8e35a4cd9d25405))
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* implement soft delete for partner insurers (PIS-REG-106) ([21f85a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21f85a1f9e07ca38da3dcf98fb7eae95bc5745f5))
* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## [1.0.0-beta.6](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.5...v1.0.0-beta.6) (2025-11-13)

### 🚀 Features

* Add PIS-REG-105 - Changement de statut ([#41](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/41)) ([88b31ce](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88b31ce5e86e189b92279c45d8e35a4cd9d25405))

## 1.0.0-alpha-PIS-REG-105.1 (2025-11-13)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### ✅ Tests

* **status:** add comprehensive tests for partner status changes ([ed195c7](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ed195c7a270cf94356658ae677673100e5b03ada))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))
* upgrade shared-kernel to 1.2.0 ([a1bd473](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a1bd473281524bdfb9dbf629c5fd8fda1a2fcdc2))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-beta.4 [skip ci] ([5aad6c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5aad6c090be1418318d012ba146b126aa8ead318)), closes [#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)
* **release:** 1.0.0-beta.5 [skip ci] ([ef65c16](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ef65c1621a84372ea66a25cfba3e96ff02931e39)), closes [#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* add PIS-REG-104 - Mise à jour d’un assureur partenaire ([#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)) ([9b38c68](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9b38c688f336f1ad8f662a337b86bfd4707bb984))
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))
* **status:** implement partner insurer status change (PIS-REG-105) ([a209477](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a20947712bfa8dddb9c5057d6cab8607952dd920))

## [1.0.0-beta.5](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.4...v1.0.0-beta.5) (2025-11-13)

### 🚀 Features

* add PIS-REG-104 - Mise à jour d’un assureur partenaire ([#40](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/40)) ([9b38c68](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9b38c688f336f1ad8f662a337b86bfd4707bb984))

## 1.0.0-alpha-PIS-REG-104.1 (2025-11-13)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-beta.4 [skip ci] ([5aad6c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5aad6c090be1418318d012ba146b126aa8ead318)), closes [#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* **api:** implement PATCH partner insurer update endpoint ([a7e342d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a7e342d6e29f2d4bfd1ce71bd29bbc072308fffc))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## [1.0.0-beta.4](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.3...v1.0.0-beta.4) (2025-11-12)

### 🚀 Features

* **registry:** add PIS-REG-103 Recherche filtrée et pagination des partenaires assureurs ([#39](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/39)) ([88e9fa9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/88e9fa9de90826a2f33157bb518450dbb3f4c0c8))

## 1.0.0-alpha-PIS-REG-103.1 (2025-11-12)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))

### ✅ Tests

* fix mock injection for GetPartnerInsurersSummariesQueryHandler ([4d75d3b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d75d3be20adf319ffb0485254b00915462dac6d))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-beta.3 [skip ci] ([bc3d6a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bc3d6a1c4adea1d2187d95fd0d68a96eac3dfdb6)), closes [#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* **registry:** implement filtered search and pagination for partner insurers ([5a39c0f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5a39c0f705f96f904ec2586f663065aa2835b451))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## [1.0.0-beta.3](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.2...v1.0.0-beta.3) (2025-11-12)

### 🚀 Features

* **api:** add PIS-REG-102 - Consultation d’un partenaire assureur ([#38](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/38)) ([4fc541e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4fc541e32c31ecc4eda60911616e6baec008a2fa))

## 1.0.0-alpha-PIS-REG-102.1 (2025-11-11)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* • Remove in-process event handling system (SpringEventPublisher, EventPublisher interface)
• Remove unused DomainEventHandler implementations
• Replace duplicate event publishing mechanisms with single outbox-based approach

Core Infrastructure:
• Add transactional outbox pattern with OutboxRepository and OutboxMessageProcessor
• Implement RabbitMQ integration for external event publishing
• Add custom serializers for DomainEntityId and kotlin.time.Instant
• Configure scheduled outbox processor with per-message transaction handling

Domain Model Enhancements:
• Convert DomainEvent to abstract serializable class with proper type safety
• Add comprehensive domain events (PartnerInsurerCreatedEvent, PartnerInsurerStatusChangedEvent)
• Implement rich domain entities (Contact, BrokerPartnerInsurerAgreement, PaymentTerms)
• Add value objects (Email, Phone, Address, Url, TaxIdentificationNumber)
• Introduce insurance catalog with branches and families

Application Layer:
• Add CQRS command/query handlers with proper transaction boundaries
• Implement CreatePartnerInsurerCommand with full validation
• Add query handlers for partner insurer summaries with pagination/filtering
• Create REST API controllers with comprehensive validation

Infrastructure:
• Add R2DBC repository implementations with proper mapping
• Implement outbox message processing with RabbitMQ publishing
• Add configuration properties for outbox processing intervals
• Create database migrations for partner insurers and outbox tables

Architecture Documentation:
• Include business glossary with ubiquitous language definitions

Bug Fixes:
• Fix DomainEntityId.fromString() validation logic (isEmpty -> isNotEmpty)
• Correct event deduplication in AggregateRoot using value class comparison
• Align database schema names in repository queries
• Remove HealthController to clean up application entry point

This refactor establishes a clean, scalable microservice architecture following DDD principles,
CQRS pattern, and event-driven design with guaranteed event delivery through the outbox pattern.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Couldn't find PersistentEntity for type java.util.UUID

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Unsupported kotlin native types not handled by R2DBC

Kotlin natives types like Uuid, JsonElement or Instant are not yet compatible/supported by R2DBC
I needed to replace them all by corresponding Java types which are fully compatible. For those that were not, I had to create converters

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(outbox): use FOR UPDATE SKIP LOCKED and per-row transactional processing

- Add OutboxRepository.fetchNextUnprocessedForUpdateSkipLocked() to atomically fetch & lock one message.
- Process up to batchSize messages by repeatedly fetching one locked row in a short transaction.
- Publish message as JSON string (SimpleMessageConverter-compatible).
- Use java.time.Instant for processedAt binding and log update counts.
- Add robust logging around locking, publish, and mark-as-processed.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :bug: fix: Failed to instantiate com.bamboo.assur.partnerinsurersservice.registry.application.queries.PartnerInsurerSummary using constructor fun <init>(java.util.UUID, kotlin.String, kotlin.String, kotlin.String, kotlin.String, kotlin.String?, kotlin.String) due to converter issue for JsonElement

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(api): implement ApiResponse structure and global exception handling for consistent API responses

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat(partner-insurer): add contacts creation while creating the partner insurer

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :sparkles: feat: add get partner insurer by id endpoint and response DTO conversion logic

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Created Postman collection

* :sparkles: feat(status): add partner insurer status management API

Add PATCH endpoint for changing partner insurer status with proper validation, command handling, and enhanced error responses. Includes new DEACTIVATED status and fixes existing status transition logic.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :lock: fix(security): escape HTML in exception handler responses

Properly sanitize all user-controlled data in error messages to prevent XSS
vulnerabilities in API error responses.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* refactor(api): improve handler interface and API routing consistency

- Change CommandHandler and QueryHandler from handle() to invoke() operator
- Update all handler implementations to use invoke() method
- Simplify controller calls by using handler instances as functions
- Add API versioning to RequestMapping with version = "1"
- Configure context path in application.yml for service routing
- Add PutMapping import for future endpoints
- Remove unused kotlin.uuid.Uuid import

This improves API ergonomics by making handlers callable as functions
and establishes consistent service routing patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :sparkles: feat(partner-insurers): add update partner insurer functionality

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor(infrastructure): reorganize messaging and serialization configuration

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor(partner-insurer): add partial update method and enhance update handling for efficiency and consistency

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor: Refactor domain event handling to use a dynamic event type name generator

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* Updated Postman collection

* Updated Postman collection

* :recycle: refactor: Simplify event type name generation and add utility functions for aggregate type retrieval

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### ♻️ Code Refactoring

* **packages:** update package imports and move files to correct package structure ([#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28fd07a94a4901d062b7a92b60595ccf7bdc))
* **repository:** split command and query responsibilities ([68fa212](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/68fa212d5db241d5eed56b51e191feef079b23d3))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))
* upgrade shared-kernel to 1.1.0 ([75f9c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/75f9c889d791c9de1ed5a36f72dd63ce42e6044d))

### 🐛 Bug Fixes

* add GitHub credentials to Gradle build step in CI ([#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d0dbc959434d3307a1a399f1f9e576dee0))
* **ci:** add GitHub authentication for shared-kernel dependency access ([#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359cc8d1de871242c51bf7135bb78dd0c803))
* **ci:** remove redundant build step from `deploy` workflow ([#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d685f3fb75b69dc3a35d7f2ae565355359))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d75665d023850477ebbcf57e207b7db6861847))
* **ci:** resolve GitHub Packages authentication in semantic-release workflow ([#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279df7185063f4c94774f0421c93be6956ac7))
* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* **deploy:** add Docker Buildx setup for GHA cache support ([#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdbdd2d0de4cfe07580f47cfc13aae99d0ec))
* **docker:** add environment variables for github credentials ([#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d7857ca1b9cdb4ae79205a870f88b96495))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))
* **docker:** remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e0aea8ba46ebe2585e0910914e9bd96b60))
* **docker:** upgrade to Gradle 8.14 ([#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61be315d7c26271d40a04ebc441d192c3838))
* **typo:** fix environment variable type in deploy workflow ([#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6198bb5994468ec4fc372738d9d92b9e68))

### 👷 CI/CD

* consolidate publishing and reusable workflows ([#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5bd1f8ff422e6833231d0981b82600617df))
* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))
* fix bad release configuration and remove failing vulnerability scan ([#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a28680fac8a746f944fc9436daad5477e494c))
* integrate semantic-release with orchestrated Docker build workflow ([#36](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/36)) ([791971a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/791971a8cf8abd0b88a1f65894477653c9a1e1c8))
* provide gradle credentials for semantic-release ([#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580ee83300354b8ff2dd9bc1f3c0acba99d68))
* run semantic-release on node 22 and allow sarif upload ([#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7ca50d1eeec429e886d8c5ea2c3d80d1710))

### 🔧 Chores

* **build:** add credentials to `Docker` and `deploy` workflow step ([#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf4565bb75c3e5fea144fee1f340982aeaa))
* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.2 [skip ci] ([a2a1ebc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a2a1ebc5fc004cbf2871d75a875bda0a882ad2f4)), closes [#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** centralize conventional commit parser options ([f65dc23](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f65dc2383584b98e923490954092791285461772))
* update deploy workflow to use correct github token for package access ([#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a9875b664a66764b6049c6760df6cee61902c))

### 🚀 Features

* add Docker infrastructure with dev/prod environments ([#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37aecc15c12c0b36921cc7df75807b4348fd))
* Add domain-driven design core components and glossary ([#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336f0bd34a141a6e0da11eefa4e7a7396dd0))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))
* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([28e77a6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/28e77a6c3fe4568ef14920cbed1faf26aa2de0c5)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **api:** expose multi-view partner insurer lookup ([bd0af64](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/bd0af64a7a1ef98981c15a9b3156777110fd93b8))
* extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* rename project to partner-insurers-registry-service ([#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0149e57d916fa00c725fdaa368942b0db4))

## [1.0.0-beta.2](https://github.com/billionaire-devs/partner-insurers-registry-service/compare/v1.0.0-beta.1...v1.0.0-beta.2) (2025-11-09)

### 🚀 Features

* add PIS-REG-101 — Création d’un partenaire  assureur ([#37](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/37)) ([2d2c2bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2d2c2bc3bf4fd75d43740b9edae8250ef613c89b))

## 1.0.0-alpha-PIS-REG-101.1 (2025-11-08)

### ⚠ BREAKING CHANGES

* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 💥 feat!: extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))
* 🧹 Chore: Enhance release CI and Code Quality Infrastructure ([#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34def6661b87b86205acdba0ca585e7fff7))

### 🏗️ Build System

* **deps:** Bump jvm from 2.2.10 to 2.2.20 ([#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9f3053bb187b85ba5792be987edafe35b7))
* **deps:** Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806f6d57ca050b04b4dd68d306374f1be3cf))

### 🐛 Bug Fixes

* Correct username formatting in dependabot.yml ([#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))

### 👷 CI/CD

* **deps:** Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3db047d360589759654bd7e79df8e38290d))
* **deps:** Bump actions/setup-java from 4 to 5 ([#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307b8688fc8335a5df328fdaa955954056dc))
* **deps:** Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88fda4bb07e259c6eb763ac656bc7c5fc14))

### 🔧 Chores

* consolidate `feature/partner-management` WIP into `develop` ([#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539cba26208b0f68a8d3dd8876348efc84330))
* **release:** 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167b10954ab3ee7b0024b78e8a7114a8797e)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc2fc4386f0c48854b86d9b7cb0860a00c7)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06a475b4eccbab3b1d308f6ef4d436f2d79)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abcc0081681f1592dc1ef03dc4adb8890971)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16) [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15) [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28) [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1) [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26) [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21) [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19) [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22) [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32) [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31) [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17) [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18) [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29) [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2) [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20) [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24) [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14) [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10) [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4) [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7) [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8) [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5) [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12) [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6) [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9) [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11) [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)
* **release:** 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144650e5f8a3df91f158d63981bf24386784)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* **release:** 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f62e3c90c5264d122d6110cc97887db030)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* **release:** 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726b650c34f064091dbe3a2457b699da1fd9)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)

## 1.0.0-fix-ci-race-condition.1 (2025-11-07)

* :bug: fix(docker): upgrade to Gradle 8.14 (#16) ([4d9c61b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d9c61b)), closes [#16](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/16)
* :construction_worker: fix(deploy): add Docker Buildx setup for GHA cache support (#15) ([3839bdb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3839bdb)), closes [#15](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/15)
* :see_no_evil: fix(docker): remove gradle/ from dockerignore ([658f26e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/658f26e))
* ♻️ refactor(packages): update package imports and move files to correct package structure (#28) ([a88a28f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a88a28f)), closes [#28](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/28)
* ✨ feat: Add domain-driven design core components and glossary (#1) ([3379336](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3379336)), closes [#1](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/1)
* 🎉 init ([7960ce3](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7960ce3))
* 🐛  fix(docker): add environment variables for github credentials (#26) ([a4c781d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a4c781d)), closes [#26](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/26)
* 🐛 fix: add GitHub credentials to Gradle build step in CI (#21) ([b3b538d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b3b538d)), closes [#21](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/21)
* 🐛 fix: Invalid workflow file: .github/workflows/release.yml ([242cd70](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/242cd70))
* 🐛 fix(ci): add GitHub authentication for shared-kernel dependency access (#19) ([9c97359](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9c97359)), closes [#19](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/19)
* 🐛 fix(ci): remove redundant build step from `deploy` workflow (#23) ([4d1ab8d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4d1ab8d)), closes [#23](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/23)
* 🐛 fix(ci): resolve GitHub Packages authentication in semantic-release workflow (#34) ([a8d7566](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d7566)), closes [#34](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/34)
* 🐛 fix(ci): resolve GitHub Packages authentication in semantic-release workflow (#35) ([003279d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/003279d)), closes [#35](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/35)
* 🐛 fix(typo): fix environment variable type in deploy workflow (#22) ([ac55cf6](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ac55cf6)), closes [#22](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/22)
* 👷 ci: consolidate publishing and reusable workflows (#30) ([9262d5b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9262d5b)), closes [#30](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/30)
* 👷 ci: fix bad release configuration and remove failing vulnerability scan (#32) ([7a5a286](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7a5a286)), closes [#32](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/32)
* 👷 ci: integrate semantic-release with orchestrated Docker build workflow ([7fbf098](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7fbf098))
* 👷 ci: provide gradle credentials for semantic-release (#33) ([3ff580e](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3ff580e)), closes [#33](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/33)
* 👷 ci: run semantic-release on node 22 and allow sarif upload (#31) ([5dc3c7c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5dc3c7c)), closes [#31](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/31)
* 💥 feat!: extract shared kernel library for partner insurers domain (#17) ([49c5574](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/49c5574)), closes [#17](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/17)
* 🔧 feat: add Docker infrastructure with dev/prod environments (#18) ([476e37a](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/476e37a)), closes [#18](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/18)
* 🧹 Chore: Enhance release CI and Code Quality Infrastructure (#29) ([e3f1a34](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e3f1a34)), closes [#29](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/29)
* 🚀 Configure CI/CD pipeline with GitHub Actions and Docker support (#2) ([9f2c284](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9f2c284)), closes [#2](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/2)
* 🛠️ chore: update deploy workflow to use correct github token for package access (#20) ([a48a987](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a48a987)), closes [#20](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/20)
* 🛠️ chore(build): add credentials to `Docker` and `deploy` workflow step (#24) ([475a6cf](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/475a6cf)), closes [#24](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/24)
* 🛠️ feat!: rename project to partner-insurers-registry-service (#27) ([8a688c0](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/8a688c0)), closes [#27](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/27)
* Create README.md ([f87b1ca](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/f87b1ca))
* Created Postman collection ([6194a99](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/6194a99))
* Fix CI for tests and release (#14) ([da6b76f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/da6b76f)), closes [#14](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/14)
* Merge branch 'develop' into dependabot/docker/gradle-9.1-jdk21 ([fdd829c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/fdd829c))
* Merge pull request #10 from BillionaireDevs/dependabot/gradle/plugin.spring-2.2.20 ([a8d916b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/a8d916b)), closes [#10](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/10)
* Merge pull request #4 from BillionaireDevs/dependabot/github_actions/actions/checkout-5 ([2af38ca](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/2af38ca)), closes [#4](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/4)
* Merge pull request #7 from BillionaireDevs/dependabot/github_actions/cycjimmy/semantic-release-actio ([b45112c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b45112c)), closes [#7](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/7)
* Merge pull request #8 from BillionaireDevs/dependabot/docker/eclipse-temurin-25-jre-alpine ([14148eb](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14148eb)), closes [#8](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/8)
* build(deps): Bump jvm from 2.2.10 to 2.2.20 (#5) ([3f5b9a9](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/3f5b9a9)), closes [#5](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/5)
* build(deps): Bump plugin.spring from 2.2.10 to 2.2.20 ([4e23806](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/4e23806))
* chore: consolidate `feature/partner-management` WIP into `develop` (#12) ([5da539c](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5da539c)), closes [#12](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/12)
* chore(release): 1.0.0-beta.1 [skip ci] ([02a5167](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/02a5167))
* chore(release): 1.0.0-beta.1 [skip ci] ([01967bc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/01967bc))
* chore(release): 1.0.0-beta.1 [skip ci] ([7bcfd06](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/7bcfd06))
* chore(release): 1.0.0-beta.1 [skip ci] ([9401abc](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/9401abc))
* chore(release): 1.0.0-develop.1 [skip ci] ([e73a144](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/e73a144))
* chore(release): 1.0.0-develop.2 [skip ci] ([612bb8f](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/612bb8f))
* chore(release): 1.0.0-develop.3 [skip ci] ([1be3726](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/1be3726))
* ci(deps): Bump actions/checkout from 4 to 5 ([c752d3d](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/c752d3d))
* ci(deps): Bump actions/setup-java from 4 to 5 (#6) ([5b60307](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/5b60307)), closes [#6](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/6)
* ci(deps): Bump cycjimmy/semantic-release-action from 4 to 5 ([21b4c88](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/21b4c88))
* docker(deps): Bump eclipse-temurin from 21-jre-alpine to 25-jre-alpine ([82120b3](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/82120b3))
* docker(deps): Bump gradle from 8.5-jdk21 to 9.1-jdk21 ([60bf690](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/60bf690))
* docker(deps): Bump gradle from 8.5-jdk21 to 9.1-jdk21 (#9) ([b5646a1](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/b5646a1)), closes [#9](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/9)
* fix: Correct username formatting in dependabot.yml (#11) ([ae8886b](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/ae8886b)), closes [#11](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/11)
* fix: Dockerfile permission and port issues (#25) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-registry-service/commit/14c3f81)), closes [#25](https://github.com/billionaire-devs/partner-insurers-registry-service/issues/25)


### BREAKING CHANGE

* Move core domain classes to shared-kernel library

- Extract domain models (DomainEvent, AggregateRoot, Model, Result)
- Extract value objects (DomainEntityId, Email, Phone, Address, Url)
- Extract domain exceptions (DomainException, EntityNotFoundException, etc.)
- Extract application patterns (Command, Query, CommandHandler, QueryHandler)
- Extract presentation utilities (ApiResponse, GlobalExceptionHandler)
- Extract infrastructure serializers and event publishing
- Update dependencies to use shared-kernel:0.1.0
- Replace local imports with shared library imports

This creates a reusable foundation for microservices architecture
allowing consistent domain patterns across partner insurer services.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* :recycle: refactor!: complete migration to shared-kernel library imports

- Replace core.domain.* → partnerinsurers.sharedkernel.domain.*
- Replace core.application.* → partnerinsurers.sharedkernel.application.*
- Replace core.presentation.* → partnerinsurers.sharedkernel.presentation.*
- Update project metadata: group and rootProject name
- Clean up unused imports and optimize import statements
- Update all domain events, entities, and value objects references

Completes transition to shared-kernel:0.1.0 for consistent domain patterns.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 chore: configure GitHub Packages repository and CI authentication

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🔧 config: update maven repository configuration and add optimization docs

- Update GitHub Packages URL to shared-kernel repository
- Configure GitHub credentials in gradle.properties

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
* Old deploy workflow replaced with new build-and-publish system

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 🧹 chore(ci): update release pipeline

- Migrate semantic-release config from JSON to JavaScript for dynamic branch handling
- Add @semantic-release/exec plugin for build automation and success/failure logging
- Update GitHub Actions workflow with proper permissions and semantic-release v24.0.8
- Enhanced changelog generation with emoji categorization
- Support for dynamic prerelease channels based on branch names

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* ♻️ refactor(docker): standardize service namin

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>

* 👷 ci(qodana): switch to community linter image

- Removes QODANA_TOKEN/QODANA_ENDPOINT env usage and Ultimate-only flags in
.github/workflows/qodana_code_quality.yml.
- Updates qodana.yaml to use jetbrains/qodana-jvm-community:2025.2, eliminating the license requirement.

Signed-off-by: Mel Sardes <100629918+MelSardes@users.noreply.github.com>
