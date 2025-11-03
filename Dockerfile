# Multi-stage build for Spring Boot Kotlin application
FROM gradle:9.1-jdk21 AS build

WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/ ./gradle/

# Download dependencies
RUN gradle dependencies --no-daemon

# Copy source code
COPY src/ ./src/

# Build the application
RUN gradle build --no-daemon -x test

# Runtime stage
FROM eclipse-temurin:25-jre-alpine

WORKDIR /app

# Create a non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Install curl for health checks
RUN apk add --no-cache curl

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership to non-root user
RUN chown -R appuser:appgroup /app
USER appuser

# Default environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0 -XX:+UseG1GC" \
    SERVER_PORT=8080

# Expose the port the app runs on
EXPOSE 8080

# Health check using Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/bamboo-assur/partner-insurers/core/api/actuator/health || exit 1

# Create a script to run the application
COPY --from=build /app/src/main/resources/docker-entrypoint.sh /app/
RUN chmod +x /app/docker-entrypoint.sh

ENTRYPOINT ["/app/docker-entrypoint.sh"]
