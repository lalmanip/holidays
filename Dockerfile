# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -q clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/holidays-api-*.jar app.jar

EXPOSE 8095
ENV SERVER_PORT=8095

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-jar", "app.jar"]
