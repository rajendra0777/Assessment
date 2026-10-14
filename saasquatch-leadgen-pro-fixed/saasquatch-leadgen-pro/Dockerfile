# Multi-stage Dockerfile for Caprae Capital SaaSquatch LeadGen Pro
# Stage 1: Build & Package
FROM maven:3.9.6-eclipse-temurin-17-alpine AS builder
WORKDIR /workspace
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
# Tests are run as a separate gate in the Jenkins pipeline (stage 3) before this image is built.
RUN mvn -B clean package -DskipTests

# Stage 2: Production JRE Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Security: Create non-root user
RUN addgroup -S capraegroup && adduser -S capraeuser -G capraegroup
USER capraeuser:capraegroup

# Copy compiled artifact from builder
COPY --from=builder --chown=capraeuser:capraegroup /workspace/target/*.jar app.jar

# Configuration
ENV SPRING_PROFILES_ACTIVE=postgres \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
