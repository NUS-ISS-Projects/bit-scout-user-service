FROM openjdk:17-jdk
WORKDIR /app
COPY target/userService-0.0.1-SNAPSHOT.jar /app/userService.jar
ENTRYPOINT ["java", "-jar", "userService.jar"]