# syntax=docker/dockerfile:1.7

# Multi-stage build for Spring Boot Kotlin application
FROM gradle:9.1-jdk21 AS build

# Build arguments for GitHub authentication
ARG GITHUB_ACTOR
ARG GITHUB_TOKEN

# Set as environment variables for Gradle
ENV GITHUB_ACTOR=${GITHUB_ACTOR}
ENV GITHUB_TOKEN=${GITHUB_TOKEN}

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
    gradle build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

ARG BUILD_DATE="unknown"
ARG VERSION="0.0.0"
ARG VCS_REF="unknown"
ARG VENDOR=${GITHUB_REPOSITORY_OWNER}
ARG MAINTAINER_NAME=${GITHUB_ACTOR}
ARG MAINTAINER_EMAIL="100629918+MelSardes@users.noreply.github.com"
ARG MAINTAINER_URL="https://github.com/MelSardes"
ARG PROJECT_NAME="partner-insurers-registry-service"
ARG PROJECT_REPO="https://github.com/${GITHUB_REPOSITORY}"

WORKDIR /app

# Create a non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Install curl for health checks and dumb-init for proper signal handling
RUN apk add --no-cache curl dumb-init

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Create a script to run the application
COPY --from=build /app/src/main/resources/docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app
USER appuser

LABEL \
  org.opencontainers.image.created=${BUILD_DATE} \
  org.opencontainers.image.authors="${MAINTAINER_NAME} <${MAINTAINER_EMAIL}>" \
  org.opencontainers.image.vendor="${VENDOR}" \
  org.opencontainers.image.title=${PROJECT_NAME} \
  org.opencontainers.image.description="Master registry service powering Bamboo Assur partner insurers." \
  org.opencontainers.image.version=${VERSION} \
  org.opencontainers.image.revision=${VCS_REF} \
  org.opencontainers.image.licenses="Apache-2.0" \
  org.opencontainers.image.source=${PROJECT_REPO} \
  org.opencontainers.image.url=${PROJECT_REPO} \
  org.opencontainers.image.documentation="${PROJECT_REPO}/blob/main/docs" \
  org.opencontainers.image.base.name="eclipse-temurin:21-jre-alpine"

# Default environment variables with optimized JVM settings for containers
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC" \
    SERVER_PORT=8090

# Expose the port the app runs on
EXPOSE 8090

# Health check using Spring Boot Actuator with correct context path
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8090/api/partner-insurers/registry/actuator/health || exit 1

# Use dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--", "/app/docker-entrypoint.sh"]
