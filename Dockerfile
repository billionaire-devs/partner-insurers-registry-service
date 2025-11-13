# syntax=docker/dockerfile:1.7

# Multi-stage build for Spring Boot Kotlin application
FROM gradle:9.1-jdk21 AS build

# Build arguments for GitHub authentication (map to expected Gradle properties)
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN
ARG GITHUB_REPOSITORY

# Set as environment variables for Gradle (using the names Gradle expects)
ENV GPR_USER=${GITHUB_ACTOR}
ENV GPR_KEY=${GITHUB_TOKEN}
ENV GITHUB_REPOSITORY=${GITHUB_REPOSITORY}

WORKDIR /app

# Enable BuildKit cache for Gradle dependencies
RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    --mount=type=cache,target=/home/gradle/.gradle/wrapper \
    gradle --version > /dev/null

# Copy gradle files first for better caching
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/ ./gradle/

# Download dependencies
RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    --mount=type=cache,target=/home/gradle/.gradle/wrapper \
    gradle dependencies --no-daemon

# Copy source code
COPY src/ ./src/

# Build the application
RUN --mount=type=cache,target=/home/gradle/.gradle/caches \
    --mount=type=cache,target=/home/gradle/.gradle/wrapper \
    gradle bootJar --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

# Build arguments that match what GitHub Actions provides
# Version is managed by semantic-release via gradle.properties
ARG BUILD_DATE="unknown"
ARG VERSION="0.0.0"
ARG VCS_REF="unknown"
ARG VENDOR="unknown"
ARG MAINTAINER_NAME="unknown"
ARG MAINTAINER_EMAIL="unknown"
ARG MAINTAINER_URL="unknown"
ARG PROJECT_NAME="partner-insurers-registry-service"
ARG PROJECT_REPO="unknown"

WORKDIR /app

# Create a non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Install curl for health checks and dumb-init for proper signal handling
RUN apk add --no-cache curl dumb-init

# Copy the built JAR from the build stage (use default Spring Boot JAR naming)
COPY --from=build /app/build/libs/*.jar app.jar

# Copy the entrypoint script directly from host context
COPY docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app
USER appuser

# Labels (these will be overridden by the workflow labels, but provide defaults)
LABEL \
  org.opencontainers.image.created=${BUILD_DATE} \
  org.opencontainers.image.authors="${MAINTAINER_NAME} <${MAINTAINER_EMAIL}>" \
  org.opencontainers.image.vendor="${VENDOR}" \
  org.opencontainers.image.title="${PROJECT_NAME}" \
  org.opencontainers.image.description="Master registry service powering Bamboo Assur partner insurers." \
  org.opencontainers.image.version="${VERSION}" \
  org.opencontainers.image.revision="${VCS_REF}" \
  org.opencontainers.image.licenses="Apache-2.0" \
  org.opencontainers.image.source="${PROJECT_REPO}" \
  org.opencontainers.image.url="${PROJECT_REPO}" \
  org.opencontainers.image.documentation="${PROJECT_REPO}/blob/main/docs" \
  org.opencontainers.image.base.name="eclipse-temurin:21-jre-alpine"

# Default environment variables with optimized JVM settings for containers
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC -XX:+UseStringDeduplication"
ENV SERVER_PORT=8090
ENV DB_HOST=localhost
ENV DB_PORT=5432
ENV DB_NAME=partner_insurers_registry_service_db
ENV DB_USERNAME=postgres
ENV DB_PASSWORD=postgres
ENV RABBITMQ_HOST=localhost
ENV RABBITMQ_PORT=5672
ENV RABBITMQ_USERNAME=guest
ENV RABBITMQ_PASSWORD=guest

# Expose the port the app runs on
EXPOSE 8090

# Health check using Spring Boot Actuator with correct context path
HEALTHCHECK --interval=30s --timeout=3s --start-period=45s --retries=3 \
    CMD curl -f http://localhost:8090/api/partner-insurers/registry/actuator/health || exit 1

# Use dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--", "/app/docker-entrypoint.sh"]
