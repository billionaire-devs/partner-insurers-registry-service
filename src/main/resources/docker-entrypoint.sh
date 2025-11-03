#!/bin/sh
set -e

# Verify required environment variables
required_vars="DB_HOST DB_PORT DB_NAME DB_USERNAME DB_PASSWORD RABBITMQ_HOST RABBITMQ_PORT RABBITMQ_USERNAME RABBITMQ_PASSWORD"

for var in $required_vars; do
    if [ -z "$(eval echo \$$var)" ]; then
        echo "Error: Required environment variable '$var' is not set"
        exit 1
    fi
done

# Start the application with proper configuration
exec java ${JAVA_TOOL_OPTIONS} -jar /app/app.jar \
    --spring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    --server.port=${SERVER_PORT}
