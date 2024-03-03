
# Stage 1: Build the application
FROM adoptopenjdk/openjdk16 AS build
WORKDIR /app
COPY . .
RUN ./gradlew build

# Stage 2: Run the application
FROM adoptopenjdk/openjdk16
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
