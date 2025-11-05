# Use an official OpenJDK image as base
FROM eclipse-temurin:21-jdk-jammy
# Set working directory inside container
WORKDIR /app

# Copy the JAR file from target directory to the container
COPY target/userservice-0.0.1-SNAPSHOT.jar app.jar

# Expose the port Eureka runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
