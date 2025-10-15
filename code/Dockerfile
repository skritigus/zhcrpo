FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./
RUN chmod +x ./gradlew
COPY src ./src

RUN ./gradlew bootJar --no-daemon --parallel --build-cache

FROM eclipse-temurin:17-jre AS final-stage
WORKDIR /app
COPY --from=build /app/build/libs/*.jar application.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]