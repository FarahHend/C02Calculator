# Use a base image with JDK installed
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/CompanyService-0.0.1-SNAPSHOT.jar app.jar

# Expose the port on which the Company Microservice will run
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
