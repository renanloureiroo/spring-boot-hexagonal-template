# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn --batch-mode dependency:go-offline
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn --batch-mode clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app --no-create-home app
COPY --from=build /app/target/step-by-step-*.jar app.jar
USER app
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC -Xss512k -Duser.timezone=UTC"
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
