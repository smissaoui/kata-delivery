# ============================
# 1. Build stage
# ============================
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /workspace

# Copy only the pom files first (better Docker cache)
COPY pom.xml .
COPY domain/pom.xml domain/pom.xml
COPY application/pom.xml application/pom.xml
COPY infrastructure/pom.xml infrastructure/pom.xml

RUN mvn -q -e -DskipTests=true dependency:go-offline

# Now copy the full source code
COPY . .

# Build the infrastructure module only (and its dependencies)
RUN mvn -q -e -DskipTests=true -pl infrastructure -am package


# ============================
# 2. Runtime stage
# ============================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the previous stage
COPY --from=builder /workspace/infrastructure/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
