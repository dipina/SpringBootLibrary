# REST API Reference

The API is documented interactively via **Springdoc OpenAPI** and is always in sync with the source code.

## Swagger UI (Live)

When the application is running locally:

```
http://localhost:8080/swagger-ui.html
```

The OpenAPI JSON specification can be downloaded from:

```
http://localhost:8080/v3/api-docs
```

## Endpoints Summary

### Books

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/books` | List all books |
| `GET` | `/api/books/{id}` | Get book by ID |
| `POST` | `/api/books` | Add a new book |
| `PUT` | `/api/books/{id}` | Update a book |
| `DELETE` | `/api/books/{id}` | Delete a book |

### Users

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/users` | List all users |
| `GET` | `/api/users/{id}` | Get user by ID |
| `POST` | `/api/users` | Register a new user |
| `DELETE` | `/api/users/{id}` | Delete a user |

### Borrowings

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/borrowings` | List all borrowings |
| `POST` | `/api/borrowings` | Borrow a book |
| `PUT` | `/api/borrowings/{id}/return` | Return a borrowed book |
| `GET` | `/api/borrowings/user/{userId}` | Get borrowings for a user |

## Request / Response Examples

### Borrow a book

**Request:**
```http
POST /api/borrowings
Content-Type: application/json

{
  "userId": 1,
  "bookId": 42
}
```

**Response `201 Created`:**
```json
{
  "id": 7,
  "userId": 1,
  "bookId": 42,
  "borrowDate": "2025-04-20",
  "returnDate": null,
  "status": "BORROWED"
}
```

### Return a book

```http
PUT /api/borrowings/7/return
```

**Response `200 OK`:**
```json
{
  "id": 7,
  "returnDate": "2025-04-22",
  "status": "RETURNED"
}
```
