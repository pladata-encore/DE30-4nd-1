# Use an official Maven image to build the application
FROM maven:3-amazoncorretto-17 AS build

# Set the working directory in the container
WORKDIR /portfolio

# Copy the pom.xml and download the dependencies
COPY pom.xml ./
RUN mvn dependency:go-offline

# Copy the source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests

# Use an official OpenJDK image to run the application
FROM amazoncorretto:17.0.11

# Set the working directory in the container
WORKDIR /portfolio

# Copy the built application from the build stage
COPY --from=build /portfolio/target/portfolio-0.0.1-SNAPSHOT.jar /portfolio/portfolio-0.0.1-SNAPSHOT.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "portfolio-0.0.1-SNAPSHOT.jar"]