# Stage 1: Use maven to build the application
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
#COPY pom.xml .
#RUN mvn dependency:go-offline
#COPY src ./src
COPY . ./wms
WORKDIR /app/wms
RUN mvn clean package -DskipTests

# Stage 2: Use openjdk to run the application
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/wms/target/*.jar wms.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "wms.jar"]
