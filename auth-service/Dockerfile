# ----------- Builder Stage -----------
FROM maven:3.9.9-eclipse-temurin-21 AS builder
# Use a specific Maven version for building the application.

WORKDIR /app
# Set the working directory inside the container

COPY pom.xml .
# Copy the Maven POM file to the working directory

RUN mvn dependency:go-offline -B
#mvn - install all packages, dependency- cache dependencies unless change has been detected, B- batch mode.

COPY src ./src
# Copy the source code to the working directory

RUN mvn clean package -DskipTests
# Build the application, this will create a JAR file in the target directory

# ----------- Runner Stage -----------
FROM openjdk:21-jdk AS runner
# Use a specific JDK version for running the application. This stage is lighter and does not include Maven.

WORKDIR /app

COPY --from=builder ./app/target/auth-service-0.0.1-SNAPSHOT.jar ./app.jar
# Copy the built JAR file from the builder stage to the runner stage. It means the JAR file will be named app.jar in the /app directory

EXPOSE 8085
# Expose port 8085 for the application

ENTRYPOINT ["java", "-jar", "app.jar"]
# Set the entry point to run the (jar)application using Java
