# Runtime-only Dockerfile for prebuilt Spring Boot JAR
# Java version determined from build.gradle toolchain: 17
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the already-built jar from host build output
# Ensure you have run: ./gradlew clean bootJar -x test --no-daemon
COPY build/libs/*.jar app.jar

# Expose Spring Boot default port
EXPOSE 8080

# Start the application (no Gradle build inside the image)
ENTRYPOINT ["java", "-jar", "app.jar"]
