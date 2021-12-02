FROM maven:3.8.3-openjdk-17-slim as build
WORKDIR /app
COPY . .
RUN mvn clean package

FROM openjdk:17-jdk-slim as runtime
WORKDIR /app
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar .
EXPOSE 8080
CMD ["java", "-jar", "backend-0.0.1-SNAPSHOT.jar"]
