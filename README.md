# Library Borrowing System

## Purpose
This application allows users to borrow and return books seamlessly through a shared platform. Users can manage book availability, track borrowed books, and return them, facilitating an efficient book-sharing system.

## Architecture Overview
The application is built using **Spring Boot**, following the **MVC (Model-View-Controller) pattern**:
- **Model**: Defines the entities and database interaction via JPA.
- **View**: A frontend built with **HTML, JavaScript** (and **Thymeleaf** if needed, not done in this example).
- **Controller**: Handles HTTP requests and business logic.

### Key Components
- `src/main/java/com/example/library/` (Main Java source folder)
  - `controller/` – RESTful controllers handling requests.
  - `service/` – Business logic layer.
  - `repository/` – Interfaces for database interactions.
  - `model/` – Entity definitions using JPA.
- `src/main/resources/static/` – Contains HTML, JavaScript, and CSS for the UI.
- `src/main/resources/application.properties` – Configuration file for database and server settings.
- **Swagger**: API documentation is generated automatically using **Springdoc OpenAPI**.

For a detailed breakdown, refer to [HOWTO_SPRINGBOOT.md](HOWTO_SPRINGBOOT.md).

## Running the Application
1. Start MySQL and create the required database:
   ```sh
   mysql -u root -p < src/main/resources/dbsetup.sql
   ```
2. Configure `application.properties` with your database credentials.
3. Run the application using Maven:
  - Optionally firt clean and install dependencies explicitly:
    ```sh
    mvn clean install
    ```
   ```sh
   mvn spring-boot:run
   mvn -DskipTests spring-boot:run
   ```
4. Access the application:
   - **Frontend UI**: `http://localhost:8080`
   - **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
   - **Swagger API Docs**: `http://localhost:8080/swagger-ui.html`

## Testing the Application
To run only JUnit tests. This will execute all tests cases in the src/test/java/ directory, excluding folder *integration* and *performance*:

```sh
mvn test
```
To run only some specific tests:
```sh
mvn -Dtest=UserServiceTest,BookServiceTest test
```

**Important** Integration tests will only work if the MySQL database is previously running.

To run integration tests:
```sh
mvn -Pintegration integration-test
```

To run performance tests:
```sh
mvn -Pperformance integration-test
```

To run tests with more verbose output:
```sh
mvn test -Dspring-boot.run.profiles=test
```
This ensures that your unit tests mock dependencies work properly while integration tests interact with a real embedded server.

To test JaCoCo test coverage report
```sh
mvn verify
mvn clean test jacoco:report # creates the report
mvn clean verify jacoco:report # if the check fails jacoco stops
```
Then, open `target/site/jacoco/index.html`, and the JaCoCo report should be available.

To generate a test report (JaCoCo, PMD, Checkstyle):
```sh
mvn site
mvn clean verify site
```
Then, open `target/site/index.html`, and the JaCoCo report should be available.

## Launching the app with Docker
You may launch the app in one single command.

If you just need to start your containers without rebuilding them.
```sh
docker-compose up	
```
If you've changed the Dockerfile, dependencies, or environment configurations.
```sh
docker-compose up --build	
```

