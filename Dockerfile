# Opción alternativa: compilar localmente primero
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Primero compila localmente: mvn clean package -DskipTests
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]