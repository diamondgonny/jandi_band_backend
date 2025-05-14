FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app/build/libs/band_backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"] 