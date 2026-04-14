# syntax=docker/dockerfile:1
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copy wrapper and basic config to cache the Gradle download and dependencies
COPY gradlew ./
COPY gradle ./gradle
COPY build.gradle ./
COPY settings.gradle ./

# Fix line endings and make executable (important for Windows users)
RUN sed -i 's/\r$//' gradlew && chmod +x ./gradlew

# Pre-download dependencies - this layer will be cached unless build.gradle changes
RUN --mount=type=cache,target=/root/.gradle ./gradlew dependencies --no-daemon

# Copy source code and build
COPY src ./src
RUN --mount=type=cache,target=/root/.gradle ./gradlew bootJar --no-daemon --parallel --build-cache

FROM eclipse-temurin:17-jre AS final-stage
WORKDIR /app
COPY --from=build /app/build/libs/*.jar application.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"]