# Use an official OpenJDK 21 runtime as a parent image
FROM openjdk:17

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the host machine to the container
COPY /target/stage1-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the application runs on
EXPOSE 8089

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
