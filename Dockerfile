# ====== Build Stage ======
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper & settings first for better caching
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline

# Copy project source
COPY src ./src

# Build the JAR
RUN ./mvnw clean package -DskipTests

# ====== Runtime Stage ======
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built jar from builder
COPY --from=build /app/target/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# JVM tuning for containers
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
