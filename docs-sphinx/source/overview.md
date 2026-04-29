# Project Overview

## Purpose

**SpringBootLibrary** is an exemplary Spring Boot application developed for the **SPQ (Software Process and Quality)** subject. It demonstrates best practices in:

- RESTful API design with Spring Boot
- Layered architecture (Controller → Service → Repository → Model)
- Automated testing (unit, integration, performance)
- CI/CD pipelines with GitHub Actions
- Code quality tooling (JaCoCo, PMD, Checkstyle, JDepend)
- Technical documentation (Doxygen, Maven Site, Sphinx)

The application models a **library borrowing system** where users can borrow and return books through a shared platform.

## Key Features

| Feature | Technology |
|---|---|
| REST API backend | Spring Boot 3.4, Spring MVC |
| Database ORM | Spring Data JPA + Hibernate |
| Database | MySQL (prod), H2 (test) |
| API docs (interactive) | Springdoc OpenAPI / Swagger UI |
| Unit tests | JUnit 5 + Mockito |
| Integration tests | Spring Boot Test + MySQL |
| Performance tests | JUnitPerf |
| Code coverage | JaCoCo |
| Static analysis | PMD, Checkstyle, JDepend |
| Containerisation | Docker + Docker Compose |
| Technical docs | Doxygen, Maven Site, **Sphinx** |

## Live Documentation Links

| Document | URL |
|---|---|
| This Sphinx portal | `https://<owner>.github.io/SpringBootLibrary/` |
| Javadoc (Maven Site) | `https://<owner>.github.io/SpringBootLibrary/site/apidocs/` |
| JaCoCo Coverage | `https://<owner>.github.io/SpringBootLibrary/site/jacoco/` |
| Surefire Test Report | `https://<owner>.github.io/SpringBootLibrary/site/surefire-report.html` |
| Doxygen | `https://<owner>.github.io/SpringBootLibrary/doxygen/html/` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` (local only) |

> Replace `<owner>` with your GitHub username or organisation name.

## Repository Structure

```
SpringBootLibrary/
├── .github/workflows/         # GitHub Actions CI/CD pipelines
│   ├── maven-site-integration.yml   # Build, test & Maven site
│   └── sphinx-docs.yml              # Sphinx docs → GitHub Pages ← NEW
├── docs-sphinx/               # Sphinx documentation source ← NEW
│   ├── source/
│   │   ├── conf.py
│   │   ├── index.rst
│   │   ├── overview.md
│   │   ├── architecture.md
│   │   ├── getting_started.md
│   │   ├── testing.md
│   │   ├── reports.md
│   │   ├── api_rest.md
│   │   ├── javadoc_link.md
│   │   ├── cicd.md
│   │   ├── docker.md
│   │   └── sphinx_101.md
│   └── requirements.txt
├── docs/                      # Published GitHub Pages root
│   ├── index.html             # Portal landing page ← NEW
│   ├── site/                  # Maven Site output
│   └── doxygen/               # Doxygen output
├── src/
│   ├── main/java/com/example/library/
│   └── test/java/com/example/library/
├── pom.xml
└── README.md
```
