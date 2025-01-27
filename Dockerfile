# Stage 1: Build the application
FROM maven:3.9.9-sapmachine-21 AS builder

# Set the working directory
WORKDIR /app

# Copy the Maven configuration file and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the application source code
COPY src/ ./src/

# Build the application package, skipping tests for faster build
RUN mvn clean package -DskipTests=true

# Stage 2: Production image
FROM sapmachine:21-jdk-headless-ubuntu-20.04 AS prod

# Set the working directory
WORKDIR /app

# Copy the application JAR file from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Environment variables for the Spring application
ENV SPRING_SERVER_PORT=8081 \
    SPRING_APP_NAME=MicroService

# Expose the application port
EXPOSE 8081

# Entry point for the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]