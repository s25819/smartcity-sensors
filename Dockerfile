FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

COPY target/*.jar target/app.jar

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

