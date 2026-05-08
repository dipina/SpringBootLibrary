# Getting Started

## Prerequisites

| Tool | Minimum Version |
|---|---|
| Java (JDK) | 17 |
| Maven | 3.9 |
| MySQL | 8.0 |
| Docker & Docker Compose | 24+ (optional) |

## Database Setup

```bash
mysql -u root -p < src/main/resources/dbsetup.sql
```

Then update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/libraryapidb
spring.datasource.username=<your_user>
spring.datasource.password=<your_password>
```

## Running the Application

```bash
# Build and run (all checks)
mvn clean install
mvn spring-boot:run

# Skip tests for a fast start
mvn -DskipTests spring-boot:run
```

Access points once running:

| Endpoint | URL |
|---|---|
| Frontend UI | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

## Running with Docker

```bash
# Start all services (app + MySQL)
docker-compose up

# Rebuild after Dockerfile changes
docker-compose up --build
```

## Quick Reference: Maven Commands

| Goal | Command |
|---|---|
| Unit tests | `mvn test` |
| Integration tests | `mvn -Pintegration integration-test` |
| Performance tests | `mvn -Pperformance integration-test` |
| JaCoCo coverage report | `mvn clean test jacoco:report` |
| Full Maven site | `mvn clean verify site` |
| Publish docs to `docs/` | `mvn post-site` |
| Build Sphinx docs | `cd docs-sphinx && make html` |
