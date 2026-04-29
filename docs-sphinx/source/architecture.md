# Architecture

## MVC Pattern

SpringBootLibrary follows the classic **Model-View-Controller (MVC)** pattern, adapted for a REST API:

```
HTTP Client (Browser / Postman)
        │
        ▼
┌───────────────────┐
│    Controller     │  ← Handles HTTP requests & responses
│  (REST endpoints) │    com.example.library.controller
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│     Service       │  ← Business logic
│                   │    com.example.library.service
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│   Repository      │  ← Spring Data JPA interfaces
│                   │    com.example.library.repository
└────────┬──────────┘
         │
         ▼
┌───────────────────┐
│  Database (MySQL) │  ← Entities via Hibernate / JPA
│  H2 (tests)       │    com.example.library.model
└───────────────────┘
```

## Package Structure

| Package | Responsibility |
|---|---|
| `controller/` | REST endpoints — `BookController`, `BorrowingController`, `UserController` |
| `service/` | Business logic — `BookService`, `BorrowingService`, `UserService` |
| `repository/` | JPA repositories — CRUD + custom queries |
| `model/` | JPA entities — `Book`, `Borrowing`, `User` |

## Key Design Decisions

**Spring Data JPA** is used for the persistence layer. Repository interfaces extend `JpaRepository`, providing standard CRUD operations without boilerplate code.

**H2 in-memory database** is activated automatically in the `test` Spring profile, so unit and integration tests run without requiring a real MySQL instance (except when using `@SpringBootTest` with the `integration` Maven profile, which spins up a MySQL Docker container via GitHub Actions).

**Springdoc OpenAPI** auto-generates an interactive Swagger UI from the controller annotations at runtime, always reflecting the current API surface.

## Static Resources

The frontend is a plain HTML + JavaScript + CSS single-page application located at:

```
src/main/resources/static/
├── index.html
├── app.js
└── style.css
```

It communicates with the backend over REST, keeping the view layer decoupled from the server.
