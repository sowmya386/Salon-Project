# Build stage
FROM maven:3.9.3-eclipse-temurin-20 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Render will run this to construct the application
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:20-jre-alpine
WORKDIR /app
COPY --from=build /app/target/salon-saas-backend-1-0.0.1-SNAPSHOT.jar app.jar

# Expose the correct port
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
