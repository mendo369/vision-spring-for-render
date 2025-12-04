FROM openjdk:17-jdk-slim

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8080

# Usa el profile de Docker
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]