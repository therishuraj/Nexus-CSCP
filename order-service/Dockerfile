# Builder
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /build
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*
COPY . .
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 3007
ENTRYPOINT ["java", "-jar", "app.jar"]
