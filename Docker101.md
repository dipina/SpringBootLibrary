# Docker 101

> **Context:** This guide explains Docker concepts as applied to the [`dipina/SpringBootLibrary`](https://github.com/dipina/SpringBootLibrary) repository, a Spring Boot application used in the **Software Process and Quality** course. Read this document together with the [`Dockerfile`](Dockerfile) and [`docker-compose.yml`](docker-compose.yml) files in this repo.

---

## Table of Contents

1. [What is Docker?](#1-what-is-docker)
2. [Why Docker? The "Works on My Machine" Problem](#2-why-docker-the-works-on-my-machine-problem)
3. [Core Concepts](#3-core-concepts)
   - [Images](#31-images)
   - [Containers](#32-containers)
   - [Dockerfile](#33-dockerfile)
   - [Docker Hub & Registries](#34-docker-hub--registries)
4. [The Dockerfile in this Repository](#4-the-dockerfile-in-this-repository)
5. [Docker Compose](#5-docker-compose)
6. [The docker-compose.yml in this Repository](#6-the-docker-composeyml-in-this-repository)
7. [Key Docker Commands](#7-key-docker-commands)
8. [Docker in the Software Process & Quality Context](#8-docker-in-the-software-process--quality-context)
9. [Summary Diagram](#9-summary-diagram)
10. [Further Reading](#10-further-reading)

---

## 1. What is Docker?

**Docker** is an open-source platform that allows you to **package, ship, and run applications inside lightweight, isolated units called _containers_**.

A container bundles your application together with **everything it needs to run**: the runtime (e.g., the JVM), libraries, configuration files, and OS-level dependencies. This bundle travels as a single artifact and runs identically on any machine that has Docker installed — a developer's laptop, a CI server, or a cloud VM.

> **Analogy:** Think of a Docker container like a **shipping container** in cargo logistics. No matter what is inside (furniture, electronics, food), the external dimensions and interface are standardised. Cranes, ships, and trucks do not need to know the contents — they just handle the box. Docker does the same for software.

Docker was released in 2013 and has become the industry standard for application packaging. It is a foundational tool in modern DevOps, CI/CD pipelines, and microservices architectures.

---

## 2. Why Docker? The "Works on My Machine" Problem

Without Docker, deploying software often involves:

- Installing the correct Java version on every machine.
- Configuring the correct database driver and version.
- Setting environment variables manually.
- Praying that the OS on the server matches your development laptop.

This leads to the classic complaint: **"It works on my machine!"**

Docker eliminates this by making the **environment** part of the deliverable. Instead of shipping only code, you ship the code **plus its environment** as an immutable image.

In the context of this course:

| Without Docker | With Docker |
|---|---|
| Each student installs Java + MySQL manually | `docker compose up` and the whole stack starts |
| Environment differences cause grading inconsistencies | Instructor runs the exact same environment as the student |
| "It crashed on the server" is hard to debug | The container is identical in dev and production |
| Onboarding a new developer takes hours | Clone the repo, run Docker, done |

---

## 3. Core Concepts

### 3.1 Images

A **Docker image** is a **read-only template** — a snapshot of a file system with all the files, dependencies, and configuration needed to run an application.

Images are built in **layers**. Each instruction in a `Dockerfile` creates a new layer on top of the previous one. Layers are cached, so if only your application code changes, Docker only rebuilds the last layer — not the whole image.

```
┌─────────────────────────────┐
│     Your application JAR    │  ← Layer 4: your code
├─────────────────────────────┤
│     Application config      │  ← Layer 3: your config
├─────────────────────────────┤
│     Eclipse Temurin JRE 17  │  ← Layer 2: Java runtime
├─────────────────────────────┤
│         Alpine Linux        │  ← Layer 1: base OS
└─────────────────────────────┘
```

Images are **named** using the pattern `name:tag`, for example:
- `eclipse-temurin:17-jre-alpine` — the official OpenJDK 17 JRE on Alpine Linux
- `mysql:8.0` — the official MySQL 8.0 image
- `springbootlibrary:latest` — the image built from this repo's Dockerfile

### 3.2 Containers

A **container** is a **running instance of an image**. The relationship between an image and a container is like the relationship between a class and an object in object-oriented programming:

- Image = class definition (the blueprint)
- Container = object instance (the running thing)

You can run multiple containers from the same image simultaneously. Each container is **isolated** from others and from the host OS by default, though you can explicitly open ports and share volumes.

Key properties of containers:
- **Isolated** — each has its own filesystem, network interface, and process space.
- **Ephemeral** — when a container stops, any changes to its filesystem are lost (unless you use volumes).
- **Lightweight** — containers share the host OS kernel; they do not emulate hardware like virtual machines do.

### 3.3 Dockerfile

A **Dockerfile** is a plain-text script of instructions that tells Docker how to build an image. Each line is an instruction; common ones are:

| Instruction | Purpose |
|---|---|
| `FROM` | Sets the base image to start from |
| `WORKDIR` | Sets the working directory inside the image |
| `COPY` | Copies files from your host into the image |
| `RUN` | Executes a shell command during the build |
| `EXPOSE` | Documents which port the app listens on |
| `ENTRYPOINT` | Defines the command to run when the container starts |
| `ARG` | Declares a build-time variable |
| `ENV` | Sets an environment variable in the container |

### 3.4 Docker Hub & Registries

A **registry** is a storage and distribution system for Docker images. [Docker Hub](https://hub.docker.com) is the default public registry. When you write `FROM eclipse-temurin:17`, Docker fetches that image from Docker Hub automatically.

You can also use private registries (GitHub Container Registry, AWS ECR, etc.) to store your own images.

---

## 4. The Dockerfile in this Repository

The `Dockerfile` at the root of this project builds the Spring Boot Library application into a Docker image. A typical Dockerfile for this kind of Spring Boot + Maven project uses a **multi-stage build** to keep the final image small:

```dockerfile
# ─── Stage 1: BUILD ───────────────────────────────────────────────────────────
# Use a full JDK image with Maven to compile the project
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and POM first (cached layer — only re-runs if pom.xml changes)
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (separate layer for better caching)
RUN ./mvnw dependency:go-offline

# Copy source code and build the JAR
COPY src ./src
RUN ./mvnw package -DskipTests

# ─── Stage 2: RUNTIME ─────────────────────────────────────────────────────────
# Use a smaller JRE-only image for the final artifact
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy only the built JAR from the build stage — no compiler, no source code
COPY --from=build /app/target/*.jar app.jar

# Document that the container listens on port 8080
EXPOSE 8080

# Start the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Line-by-line explanation

**Stage 1 — Build:**

- `FROM eclipse-temurin:17-jdk-alpine AS build` — starts from a base image that has both the JDK and Alpine Linux. The `AS build` label lets the second stage reference this stage by name.
- `WORKDIR /app` — all subsequent commands run inside `/app`.
- `COPY .mvn/ .mvn` and `COPY mvnw pom.xml ./` — copies the Maven wrapper and dependency manifest. Placing this *before* copying the source code is a **caching optimisation**: Docker only re-downloads dependencies when `pom.xml` changes, not every time a `.java` file changes.
- `RUN ./mvnw dependency:go-offline` — downloads all Maven dependencies into the layer cache.
- `COPY src ./src` — copies the application source code.
- `RUN ./mvnw package -DskipTests` — compiles the code and packages it into a fat JAR. Tests are skipped here (they should run earlier in the CI pipeline).

**Stage 2 — Runtime:**

- `FROM eclipse-temurin:17-jre-alpine` — a much smaller base image (JRE only, no compiler). This is why multi-stage builds matter: the final image does not contain Maven, the JDK, or your source code — only the JAR and its runtime.
- `COPY --from=build /app/target/*.jar app.jar` — copies only the built JAR from Stage 1.
- `EXPOSE 8080` — a documentation hint; actual port mapping happens at `docker run` time or in `docker-compose.yml`.
- `ENTRYPOINT ["java", "-jar", "app.jar"]` — the command that runs every time a container starts from this image.

### Why multi-stage?

| | Single-stage | Multi-stage |
|---|---|---|
| Final image contains | JDK + Maven + source + JAR | JRE + JAR only |
| Typical image size | ~500 MB | ~100–150 MB |
| Security surface | Large (compiler tools present) | Minimal |
| Build cache efficiency | Lower | Higher |

---

## 5. Docker Compose

Running a Spring Boot application alone is rarely enough — it usually needs a database. You could start the database and the application manually with separate `docker run` commands, but that becomes unwieldy quickly.

**Docker Compose** is a tool for **defining and running multi-container applications**. You describe all your services, networks, and volumes in a single YAML file (`docker-compose.yml`), then start everything with one command:

```bash
docker compose up
```

Docker Compose handles:
- Starting containers in the correct order (respecting `depends_on`).
- Creating a shared private network so containers can reach each other by service name.
- Mounting volumes for data persistence.
- Reading environment variables from `.env` files.
- Tearing everything down cleanly with `docker compose down`.

---

## 6. The docker-compose.yml in this Repository

The `docker-compose.yml` at the root of this project defines two services: the Spring Boot application and its MySQL database.

```yaml
version: '3.8'

services:

  # ── Database service ─────────────────────────────────────────────────────────
  db:
    image: mysql:8.0
    container_name: library-db
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: librarydb
      MYSQL_USER: library
      MYSQL_PASSWORD: library
    ports:
      - "3306:3306"
    volumes:
      - db-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 10s
      timeout: 5s
      retries: 5

  # ── Application service ───────────────────────────────────────────────────────
  app:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: library-app
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://db:3306/librarydb
      SPRING_DATASOURCE_USERNAME: library
      SPRING_DATASOURCE_PASSWORD: library
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      db:
        condition: service_healthy

volumes:
  db-data:
```

### Key concepts illustrated here

**`services`** — each entry is a container that Compose will manage.

**`image: mysql:8.0`** — for the `db` service, Docker pulls the official MySQL 8.0 image from Docker Hub. No custom Dockerfile is needed.

**`build: context: .`** — for the `app` service, Docker builds a local image using the `Dockerfile` in the current directory. This is the image described in Section 4.

**`ports: "8080:8080"`** — maps port 8080 on the **host** to port 8080 inside the **container**. Format is always `HOST:CONTAINER`. Without this, the app runs inside Docker but is unreachable from your browser.

**`environment`** — injects environment variables into the container. Spring Boot reads `SPRING_DATASOURCE_URL` and uses it to configure the data source, overriding any values in `application.properties`. Notice how the URL uses `db` as the hostname — Docker Compose creates a shared network and each service is reachable by its service name.

**`depends_on` with `condition: service_healthy`** — tells Compose not to start `app` until the `db` container passes its health check. This prevents the application from crashing because the database is not ready yet.

**`volumes: db-data`** — creates a **named volume** managed by Docker. MySQL stores its data files at `/var/lib/mysql` inside the container; mounting a volume there means the data **persists** even if the container is stopped or removed. Without this, every `docker compose down` would wipe the database.

**`healthcheck`** — periodically runs `mysqladmin ping` inside the `db` container. Docker marks the container as `healthy` only when this succeeds.

### Network communication between services

```
┌──────────────────────────────────────────────────────────┐
│                  Docker internal network                  │
│                                                          │
│   ┌─────────────────┐        ┌──────────────────────┐   │
│   │   library-app   │──────▶│     library-db        │   │
│   │  (Spring Boot)  │ :3306  │     (MySQL 8.0)      │   │
│   │                 │        │                      │   │
│   └────────┬────────┘        └──────────────────────┘   │
│            │ :8080                                        │
└────────────┼─────────────────────────────────────────────┘
             │
     ┌───────▼──────┐
     │  Your browser │  http://localhost:8080
     └──────────────┘
```

The `app` container reaches the database at `jdbc:mysql://db:3306/librarydb` — Docker resolves `db` to the internal IP of the database container automatically.

Your browser reaches the application at `http://localhost:8080` — Docker forwards that to port 8080 of the `app` container.

---

## 7. Key Docker Commands

```bash
# Build the image defined by the Dockerfile in the current directory
docker build -t springbootlibrary:latest .

# Run a standalone container (without Compose)
docker run -p 8080:8080 springbootlibrary:latest

# ── Docker Compose commands (run from the repo root) ──────────────────────────

# Start all services (builds images if needed, attaches to logs)
docker compose up

# Start all services in the background (detached mode)
docker compose up -d

# Force rebuild of images before starting
docker compose up --build

# Stop all running containers (keeps volumes and images)
docker compose stop

# Stop and remove containers AND networks (keeps volumes and images)
docker compose down

# Stop and remove containers, networks, AND volumes (wipes the database!)
docker compose down -v

# View logs of all services
docker compose logs

# Follow logs of a specific service
docker compose logs -f app

# List running containers
docker ps

# Open a shell inside a running container
docker exec -it library-app sh

# List local images
docker images

# Remove a specific image
docker rmi springbootlibrary:latest
```

---

## 8. Docker in the Software Process & Quality Context

Docker is not just a deployment tool — it directly supports several Software Process & Quality principles:

### Reproducibility
Every build of the Docker image from the same `Dockerfile` and source code produces functionally identical software. The build process is **deterministic and auditable**.

### Continuous Integration (CI)
CI pipelines (GitHub Actions, Jenkins, GitLab CI) can:
1. Build the Docker image.
2. Run tests inside the container.
3. Push the image to a registry.

This makes the artifact under test identical to what will be deployed.

### Infrastructure as Code
The `Dockerfile` and `docker-compose.yml` are **code** that lives in the repository. They are version-controlled, reviewed, and evolve alongside the application. There is no undocumented manual server configuration.

### Environment Parity (Dev/Test/Prod)
The [12-Factor App](https://12factor.net/) methodology advocates for dev/prod parity. Docker makes this achievable: the same image runs on a developer's MacBook, in a CI runner, and in production.

### Isolation & Fast Feedback
Containers start in seconds. Running the full application stack locally takes one command, enabling developers to test integration scenarios quickly without needing a shared server.

### Quality Gates
You can add a quality gate to your pipeline: only push the Docker image to the registry if all tests pass. The image then becomes a **quality-certified artifact**.

---

## 9. Summary Diagram

```
Source Code (this repo)
        │
        │  docker compose up --build
        ▼
┌───────────────┐     Dockerfile      ┌─────────────────────┐
│ Maven build   │ ─────────────────▶  │  Docker Image       │
│ (Stage 1 JDK) │                     │  (Stage 2 JRE only) │
└───────────────┘                     └──────────┬──────────┘
                                                  │
                                    docker compose up
                                                  │
                         ┌────────────────────────▼────────────────────────┐
                         │           Docker Compose network                │
                         │                                                 │
                         │  ┌──────────────┐     ┌───────────────────┐    │
                         │  │  app:8080    │────▶│  db:3306 (MySQL)  │    │
                         │  │ Spring Boot  │     │  + named volume   │    │
                         │  └──────┬───────┘     └───────────────────┘    │
                         │         │                                       │
                         └─────────┼───────────────────────────────────────┘
                                   │ port 8080 mapped to host
                                   ▼
                           http://localhost:8080
```

---

## 10. Further Reading

- [Docker official documentation](https://docs.docker.com/get-started/)
- [Docker Compose reference](https://docs.docker.com/compose/compose-file/)
- [Dockerfile best practices](https://docs.docker.com/build/building/best-practices/)
- [Dockerizing a Spring Boot Application — Baeldung](https://www.baeldung.com/dockerizing-spring-boot-application)
- [Spring Boot with Docker — Spring Guides](https://spring.io/guides/gs/spring-boot-docker/)
- [The 12-Factor App](https://12factor.net/)

---

*This document is part of the `dipina/SpringBootLibrary` repository. See the [README](README.md) for setup instructions.*
