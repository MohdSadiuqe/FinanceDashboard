# Finance Dashboard Backend

Spring Boot backend for a finance dashboard: **financial records**, **role-based access control (RBAC)**, **JWT authentication**, **aggregated dashboard APIs**, and **H2 persistence** (in-memory for local development).

This project is structured to match a typical take-home brief: clear layers (controller → service → repository), explicit validation and errors, and documented assumptions.

---

## How this maps to the assignment

| Requirement | Implementation |
|-------------|----------------|
| **User & role management** | `User` entity with `Role` (`VIEWER`, `ANALYST`, `ADMIN`), `isActive`, CRUD-style admin APIs under `/users` |
| **Financial records** | JPA entity with amount, type (`INCOME` / `EXPENSE`), category, date, note; CRUD + query filters + pagination |
| **Dashboard summaries** | `/dashboard/summary`, `/dashboard/recent`, `/dashboard/trends` (monthly or weekly buckets) |
| **Access control** | Spring Security **JWT** filter + `@PreAuthorize` on controllers (method security) |
| **Validation & errors** | Jakarta Validation on DTOs; `GlobalExceptionHandler` returns JSON `ApiError` with appropriate HTTP status codes |
| **Persistence** | H2 in-memory via JPA (`application.properties`) |

---

## Tech stack

- Java 21+ (project parent targets Java 21; CI/local may use newer JDKs)
- Spring Boot 4, Spring Security, Spring Data JPA
- H2, Lombok, JJWT

---

## Run locally

```bash
mvn spring-boot:run
```

- API base URL: `http://localhost:8080`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:financedb`, user `sa`, empty password)

---

## Demo users (seeded on first startup)

| Email | Password | Role |
|-------|----------|------|
| `admin@demo.local` | `Admin123!` | `ADMIN` |
| `analyst@demo.local` | `Analyst123!` | `ANALYST` |
| `viewer@demo.local` | `Viewer123!` | `VIEWER` |

A small set of **sample financial records** is inserted when the database is empty.

---

## Authentication

### `POST /auth/login`

Request body:

```json
{
  "email": "admin@demo.local",
  "password": "Admin123!"
}
```

Response:

```json
{
  "token": "<JWT>",
  "email": "admin@demo.local",
  "role": "ADMIN",
  "expiresInSeconds": 3600
}
```

Send the token on protected routes:

```http
Authorization: Bearer <JWT>
```

JWT claims include the user **email** as the subject and **role** (e.g. `ADMIN`) for Spring Security authorities (`ROLE_ADMIN`).

---

## Roles and enforced behavior

| Role | Allowed |
|------|---------|
| **VIEWER** | Read dashboard endpoints only (`/dashboard/**`) |
| **ANALYST** | Dashboard + **read** financial records (`GET /records`, `GET /records/{id}`) |
| **ADMIN** | Everything above + **create/update/delete** records + **user management** (`/users/**`) |

Inactive users (`isActive = false`) cannot log in.

---

## API reference

### Users (ADMIN only)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/users` | Create user (body: `UserRequest`) |
| `GET` | `/users` | List users (passwords never returned) |
| `GET` | `/users/{id}` | Get user |
| `PATCH` | `/users/{id}` | Partial update: `name`, `role`, `isActive`, `password` (`UserPatchRequest`) |

### Financial records

| Method | Path | Roles | Description |
|--------|------|-------|-------------|
| `POST` | `/records` | ADMIN | Create (`FinancialRecordRequest`, includes `userId` of owning user) |
| `GET` | `/records` | ANALYST, ADMIN | Paginated list; optional filters: `type`, `category`, `dateFrom`, `dateTo`, `createdByUserId` |
| `GET` | `/records/{id}` | ANALYST, ADMIN | Get one |
| `PUT` | `/records/{id}` | ADMIN | Update (`FinancialRecordUpdateRequest`) |
| `DELETE` | `/records/{id}` | ADMIN | Delete (**hard delete**; keeps the model simple on Hibernate 7) |

Pagination: standard Spring Data query params, e.g. `?page=0&size=20&sort=date,desc`.

### Dashboard (VIEWER, ANALYST, ADMIN)

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/dashboard/summary` | Total income, total expense, net balance, category totals split by income vs expense |
| `GET` | `/dashboard/recent?limit=10` | Recent activity (default 10, max 50) with creator metadata |
| `GET` | `/dashboard/trends?granularity=MONTH` | Time-series buckets; `granularity` = `MONTH` (default) or `WEEK` (ISO week, e.g. `2026-W14`) |

---

## Configuration

| Property | Purpose |
|----------|---------|
| `jwt.secret` | HS256 signing secret (use a long random value in production) |
| `jwt.expiration-ms` | Token lifetime in milliseconds |

---

## Assumptions and tradeoffs

- **JWT stateless auth**: No refresh-token flow; suitable for a demo or SPA with short-lived tokens.
- **Email as identity**: Login and JWT subject use **email** (normalized to lowercase when stored).
- **Record ownership**: `createdBy` links a record to a user; admins choose `userId` on create.
- **Dashboard scope**: Aggregates are **global** (all records), not per-user slices—reasonable for a small org dashboard; you could add `WHERE created_by = :currentUser` for multi-tenant style if needed.
- **H2 in-memory**: Data is reset when the process stops unless you switch the JDBC URL to a file-based H2 or another database.

---

## Tests

```bash
mvn verify
```

`spring-boot-starter-test` runs a **context load** test to ensure the application wiring starts.

---

## Author

Mohd Sadique
