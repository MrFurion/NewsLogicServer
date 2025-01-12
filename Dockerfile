FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle clean build --no-daemon -x test


FROM openjdk:21
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]