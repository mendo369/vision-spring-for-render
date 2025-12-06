# Usa la imagen oficial recomendada para OpenJDK 17
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copia el pom.xml y los archivos fuente
COPY pom.xml .
COPY src ./src

# Instala Maven y compila el proyecto
RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

# Expone el puerto en el que corre la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "target/ia-platform-backend-0.0.1-SNAPSHOT.jar"]