# Build Stage
FROM --platform=linux/amd64 eclipse-temurin:21

#
ARG JAR_FILE=target/camera-onboarding-0.0.1-SNAPSHOT.jar

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file
COPY ${JAR_FILE} app.jar

# Expose the application port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]