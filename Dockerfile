# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace/app

COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
COPY src src

# application.properties 복사 추가
COPY application.properties ./src/main/resources/

RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/app/build/libs/band_backend-0.0.1-SNAPSHOT.jar app.jar

# application.properties를 별도로 복사 (외부 설정 가능하도록)
COPY application.properties ./

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.location=file:./application.properties"]
