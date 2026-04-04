# Finance Dashboard Backend

Spring Boot backend for a finance dashboard: **financial records**, **role-based access control (RBAC)**, **JWT authentication**, **aggregated dashboard APIs**, and **MySQL** persistence. **H2** is used only for automated tests so `mvn verify` does not need a running MySQL instance.

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
| **Persistence** | **MySQL** via JPA (`application.properties`); **H2** in tests only |

---

## Tech stack

- Java 21+ (project parent targets Java 21; CI/local may use newer JDKs)
- Spring Boot 4, Spring Security, Spring Data JPA
- MySQL (runtime), H2 (tests only), Lombok, JJWT

---

## Database (SQL DDL)

- **[`database/schema.sql`](database/schema.sql)** — portable DDL (`record_date` column maps to `FinancialRecord.date`).
- **[`database/schema-mysql.sql`](database/schema-mysql.sql)** — **MySQL 8+** variant with `AUTO_INCREMENT` and `utf8mb4` (optional if you create tables manually).

JPA **`spring.jpa.hibernate.ddl-auto=update`** creates or updates tables against MySQL when the app starts. Demo users and sample rows are inserted by **`DataInitializer`** (BCrypt passwords), not by these scripts.

---

## MySQL setup

1. Install and start **MySQL 8+**.
2. Create a database user and grant access (example matches default `application.properties`):

```sql
CREATE DATABASE IF NOT EXISTS financedb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'finance'@'localhost' IDENTIFIED BY 'finance';
GRANT ALL PRIVILEGES ON financedb.* TO 'finance'@'localhost';
FLUSH PRIVILEGES;
```

3. Default connection settings live in **`src/main/resources/application.properties`**:

| Property | Default |
|----------|---------|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/financedb?createDatabaseIfNotExist=true&...` |
| `spring.datasource.username` | `finance` |
| `spring.datasource.password` | `finance` |

Override locally by editing that file or using environment variables such as `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and `SPRING_DATASOURCE_PASSWORD`.

---

## Run locally

```bash
mvn spring-boot:run
```

- API base URL: `http://localhost:8080`
- Ensure **MySQL** is running and credentials match `application.properties` before starting the app.

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
- **MySQL**: Application data persists in your MySQL instance; use migrations or `ddl-auto=validate`/`none` in production instead of `update`.
- **Tests**: `src/test/resources/application.properties` uses **H2 in-memory** so tests do not require MySQL.

---

## Tests

```bash
mvn verify
```

`spring-boot-starter-test` runs a **context load** test against **H2** (`src/test/resources/application.properties`) so the suite does not depend on MySQL.

---

## Author

Mohd Sadique
