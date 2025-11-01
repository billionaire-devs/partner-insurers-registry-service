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

# Expose the port the app runs on
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
