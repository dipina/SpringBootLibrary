
# 🐳 Docker Essentials

## 📦 What is Docker?

Docker is a platform that allows developers to package applications into containers—standardized executable components combining application source code with the operating system (OS) libraries and dependencies required to run that code in any environment.

---

## 📚 Core Concepts

### 🔹 Docker Image

An **image** is a lightweight, standalone, executable package that includes everything needed to run a piece of software: code, runtime, libraries, environment variables, and config files. Images are the *blueprint* for containers.

### 🔹 Docker Container

A **container** is a runtime instance of a Docker image. It includes the application and all its dependencies but shares the kernel with other containers. Containers are isolated, secure, and portable.

### 🔹 Dockerfile

A **Dockerfile** is a script containing instructions to build a Docker image. Typical instructions include:

- `FROM`: base image
- `COPY` or `ADD`: copy files
- `RUN`: run commands
- `CMD`: specify the default command

### 🔹 Docker Volumes

Volumes are used to **persist data** beyond the container lifecycle. For example, storing database files or logs.

### 🔹 Docker Networks

Docker supports isolated networks for containers to communicate securely and efficiently.

### 🔹 Docker Registry

A **registry** stores Docker images. Docker Hub is the default public registry.

---

## ⚙️ Common Docker Commands

### 🔍 Image Management

```bash
docker images
docker pull <image>
docker build -t <name> .
docker rmi <image>
```

### 🚀 Container Management

```bash
docker run <image>
docker run -it ubuntu /bin/bash
docker run -d -p 8080:80 nginx
docker ps
docker ps -a
docker stop <id>
docker rm <id>
```

### 🛠 Volume Management

```bash
docker volume create myvol
docker run -v myvol:/app/data <image>
```

### 🌐 Network Management

```bash
docker network create mynet
docker network ls
docker network connect mynet <container>
```

### 🧼 Cleanup

```bash
docker system prune
docker volume prune
docker rm $(docker ps -aq) -f
docker rmi $(docker images -q) -f
```

---

## 🧱 Docker Compose

### 🔸 What is Docker Compose?

**Docker Compose** is a tool for defining and running multi-container Docker applications using a simple YAML file. Instead of running each container with long `docker run` commands, Compose lets you configure and launch entire stacks of services in one command.

It is ideal when your application consists of multiple services (e.g., a web app + database).

### 🔸 Benefits

- Define services, networks, and volumes in a single file
- Easily bring up/down environments (`up`/`down`)
- Scales well for development and testing
- Reduces manual CLI steps

---

## 📁 Docker Compose Example

### 📄 `docker-compose.yml`

```yaml
version: '3'
services:
  web:
    build: .
    ports:
      - "5000:5000"
    volumes:
      - .:/app
    depends_on:
      - db
    networks:
      - appnet

  db:
    image: postgres:14
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: mydatabase
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - appnet

volumes:
  pgdata:

networks:
  appnet:
```

### 🚀 Commands

```bash
docker-compose up --build     # Build and start all services
docker-compose down           # Stop and remove all containers, networks, volumes
docker-compose ps             # List running containers
docker-compose logs           # View logs from services
```

---

## 🛠 Dockerfile Example

```Dockerfile
FROM node:18
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
EXPOSE 3000
CMD ["npm", "start"]
```

---

## ✅ Summary

Docker simplifies development and deployment by packaging applications into containers. Compose enhances this by orchestrating multiple services using a single configuration file.

Use Docker Compose when:
- You have more than one container.
- You want reproducible environments.
- You need easier local development or CI/CD pipelines.

---

Happy Dockering! 🐳
