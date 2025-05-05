# Library Borrowing System exemplary project for SPQ subject

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

To run performance tests, use the following command. [JUnitPerf documentation](https://noconnor.github.io/JUnitPerf/docs/junit5.html) should be checked to understand the code in performance directory.  
```sh
mvn -Pperformance integration-test
```

To run tests with more verbose output:
```sh
mvn test -Dspring-boot.run.profiles=test
```
This ensures that your unit tests mock dependencies work properly while integration tests interact with a real embedded server.

To test JaCoCo test coverage report. Try to change coverage threshold from 0.5 to 0.2 to ensure that check is successful. 
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

If you want to also see the performance tests reports you also need to run the following command. Check `target/reports` for the HTML report on performance. Check configuration tables at [JUnitPerf](https://github.com/noconnor/JUnitPerf).
```sh
mvn -Pperformance integration-test
```

Then, open `target/site/index.html`, and the performance report should be available.

## Profiling with VisualVM

In order to allow VisualVM to connect to the application, *sprin-boot-maven-plugin* needs to be configured with the following parameters:
```xml
   <plugin>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-maven-plugin</artifactId>
      <configuration>
         <!-- Required to allow VisualVM profiler connection -->
         <jvmArguments>-Xverify:none</jvmArguments>
      </configuration>
   </plugin>
```

Then run the application as usual with the *mvn spring-boot:run* command and open VisualVM. You should see your application listed in the *Local* tab and be able to connect to it starting, for example, a CPU profiling session inside the 'Profiler' tab.

## Configuring CI/CD with GitHub Actions
GitHub Actions is a continuous integration and continuous delivery (CI/CD) platform that allows you to automate your build, test, and deployment pipeline. You can create workflows that build and test every pull request to your repository, or deploy merged pull requests to production.

GitHub Actions goes beyond just DevOps and lets you run workflows when other events happen in your repository. For example, you can run a workflow to automatically add the appropriate labels whenever someone creates a new issue in your repository.

GitHub provides Linux, Windows, and macOS virtual machines to run your workflows, or you can host your own self-hosted runners in your own data center or cloud infrastructure.

The steps to follow to define a GitHub actions workflow for your project are:
1. In your repository on GitHub, create a workflow file called `maven-site-integration.yml` in the `.github/workflows` directory.
2. Copy the following YAML contents into the `maven-site-integration.yml` file:

```yml
name: Maven Site & Integration Tests

on:
  push:
    branches:
      - '**'
  schedule:
    - cron: '0 18-23/2 * * *'  # 20:00–01:00 CET
    - cron: '0 0-6/2 * * *'    # 02:00–08:00 CET

jobs:
  build-and-test:
    runs-on: ubuntu-latest

    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: libraryapidb
        ports:
          - 3306:3306
        options: >-
          --health-cmd="mysqladmin ping -h 127.0.0.1 -uroot -proot"
          --health-interval=10s
          --health-timeout=5s
          --health-retries=5

    steps:
      - name: Checkout code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Cache Maven dependencies
        uses: actions/cache@v4
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: |
            ${{ runner.os }}-maven-

      - name: Wait for MySQL to be ready
        run: |
          echo "Waiting for MySQL..."
          until mysqladmin ping -h 127.0.0.1 -uroot -proot --silent; do
            sleep 2
          done

      - name: Run DB initialization script
        run: |
          echo "Running DB setup..."
          mysql -h 127.0.0.1 -uroot -proot < src/main/resources/dbsetup.sql

      - name: Run Integration Tests
        run: mvn -Pintegration integration-test

      - name: Generate Maven Site
        run: mvn site
```
3. Click **Commit changes**.
4. In the "Propose changes" dialog, select either the option to commit to the default branch or the option to create a new branch and start a pull request. Then click **Commit changes** or **Propose changes**.

Committing the workflow file to a branch in your repository triggers the push event and runs your workflow.

To view the execution and results of your GitHub action's workflow go to **Actions** top menu of your repository.

Notice that sometimes you need to change access rights of jobs by visiting Settings --> Actions --> General --> Workflow permissions. For this example, it is necessary to set up "read and write permissions".

You may launch a GitHub action through an HTTP POST with the following command (for this example):
```bash
curl -X POST https://api.github.com/repos/dipina/SpringBootLibrary/actions/workflows/maven-site-integration.yml/dispatches \
  -H "Accept: application/vnd.github+json" \
  -H "Authorization: Bearer YOUR_PAT_HERE" \
  -H "X-GitHub-Api-Version: 2022-11-28" \
  -d '{"ref":"main"}'

```

Notice that to generate your our Personal Access Token you need to do the following:
1. Go to GitHub's token creation page: [https://github.com/settings/tokens/new](https://github.com/settings/tokens/new)
2. Fill in the form: "Trigger workflows via API", choose what suits you — 30 days, 90 days, or No expiration (only for trusted use) and select as scope `workflow` (for dispatching GitHub Actions workflows)
3. Click "Generate token"
4. Test it with an API call as the above
5. Use GitHub Actions Secrets: go to your repo → Settings → Secrets → Actions → New repository secret
   - Name: PAT_TOKEN
   - Value: paste your PAT
   
For more information about GitHub actions visit:
- [Overview of GitHub actions](https://docs.github.com/en/actions/about-github-actions/understanding-github-actions)
- [Quickstart for GitHub actions](https://docs.github.com/en/actions/writing-workflows/quickstart)

## Generating Documentation with Doxygen and moving all documentation into docs folder
To generate doxygen reports: `mvn doxygen:report` or `mvn site`

If you want to generate all documentation and move it into `docs` folder of your project, do the following:
1. Run the unit tests and converage tests: `mvn test jacoco:report`
2. Run the performance tests: `mvn -Pperformance integration-test`
3. Ensure that the performance reports are also moved into the `target\site\reports` folder: `mvn -Pperformance resources:copy-resources@copy-perf-report`
4. Move all contents into `docs` folder: `mvn post-site`

Notice that the `docs` is not moved into the repository in GitHub, since this folder is included in `.gitignore` file

Please also notice that some classes have been documented extensively, according to Doxygen documentation style. Check the following classes:
- `BorrowingController.java`
- `BookService.java`
- `Borrowing.java`

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

Further documentation available at [docker_essentials.md](docker_essentials_with_compose.md) document 
