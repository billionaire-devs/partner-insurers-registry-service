# Changelog

All notable changes to this project will be documented in this file.

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
