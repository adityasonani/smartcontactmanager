# ===== Build stage =====
FROM maven:3.8.2-eclipse-temurin-8 AS build
WORKDIR /app

# Copy only pom.xml to cache dependencies efficiently
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the application
COPY . .
RUN mvn clean package -DskipTests

# ===== Runtime stage =====
FROM eclipse-temurin:8-jre
WORKDIR /app

# Copy the built JAR file
COPY --from=build /app/target/contactmanager-0.0.1-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Set JVM options for better container performance
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
