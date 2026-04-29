# Docker & Docker Compose

## Quick Start

```bash
# Start application + MySQL (uses cached images)
docker-compose up

# Rebuild after code or Dockerfile changes
docker-compose up --build

# Run in background
docker-compose up -d

# Stop all services
docker-compose down
```

## Dockerfile

The project's `Dockerfile` packages the Spring Boot fat JAR into a lightweight JRE image:

```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## docker-compose.yml

The compose file wires the app container to a MySQL service, passing environment variables for the database connection:

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/libraryapidb
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
    depends_on:
      db:
        condition: service_healthy

  db:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: libraryapidb
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5
```

## VisualVM Profiling

To enable remote profiling with VisualVM, the `spring-boot-maven-plugin` is configured with:

```xml
<jvmArguments>-Xverify:none</jvmArguments>
```

Then run `mvn spring-boot:run` and connect VisualVM to the local JVM from the **Local** tab.
