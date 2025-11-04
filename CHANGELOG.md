# [1.0.0-develop.3](https://github.com/billionaire-devs/partner-insurers-core-service/compare/v1.0.0-develop.2...v1.0.0-develop.3) (2025-11-04)


### Bug Fixes

* Dockerfile permission and port issues ([#25](https://github.com/billionaire-devs/partner-insurers-core-service/issues/25)) ([14c3f81](https://github.com/billionaire-devs/partner-insurers-core-service/commit/14c3f8181e816d0be9dfb10bfd96e85a9a5415d1))

# [1.0.0-develop.2](https://github.com/billionaire-devs/partner-insurers-core-service/compare/v1.0.0-develop.1...v1.0.0-develop.2) (2025-11-02)


* 💥 feat!: extract shared kernel library for partner insurers domain ([#17](https://github.com/billionaire-devs/partner-insurers-core-service/issues/17)) ([49c5574](https://github.com/billionaire-devs/partner-insurers-core-service/commit/49c5574b25c081a461b5d43a4cea1424c1a45ff1))


### BREAKING CHANGES

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

# 1.0.0-develop.1 (2025-10-31)


### Bug Fixes

* Correct username formatting in dependabot.yml ([#11](https://github.com/BillionaireDevs/partner-insurers-core-service/issues/11)) ([ae8886b](https://github.com/BillionaireDevs/partner-insurers-core-service/commit/ae8886b0b8e52fe09c66b7c439e3e0783dff7426))
