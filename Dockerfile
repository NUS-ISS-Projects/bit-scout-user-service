# Use a base image with JDK
FROM openjdk:17-jdk-slim

# Add the jar file
COPY target/your-app.jar /app.jar

# Set the entry point
ENTRYPOINT ["java", "-jar", "/app.jar"]