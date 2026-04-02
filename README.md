Finance Data Processing and Access Control Backend

## Overview

This project is a backend system for a Finance Dashboard that manages financial records, user roles, and summary analytics.

It demonstrates:

* REST API design
* Role-Based Access Control (RBAC)
* JWT Authentication
* Data modeling with JPA
* Validation and error handling

---

## Tech Stack

* Java
* Spring Boot
* Spring Security (JWT)
* Spring Data JPA
* Maven
* H2 / MySQL

---

## Project Structure

com.finance.dashboard
├── controller
├── service
├── repository
├── model
├── security
└── jwt

---

## Authentication (JWT)

### 🔹 Login API

POST /auth/login

Request:
{
"username": "admin"
}

Response:
{
"token": "<JWT_TOKEN>"
}

---

## How to Use Token

Add token in request header:

Authorization: Bearer <JWT_TOKEN>

---

## Roles

* ADMIN → Full access
* ANALYST → Read access
* VIEWER → Limited access

---

## API Endpoints

### Financial Records

POST   /records       (ADMIN)
GET    /records       (ADMIN, ANALYST)
PUT    /records/{id}  (ADMIN)
DELETE /records/{id}  (ADMIN)

---

### Dashboard

GET /dashboard/summary (All users)

---

## Sample Request

POST /records

{
"amount": 5000,
"type": "INCOME",
"category": "Salary",
"date": "2026-04-01",
"note": "Monthly salary"
}

---

## Validation Rules

* Amount > 0
* Category not empty
* Type required
* Date required

---

## Error Codes

* 400 → Bad Request
* 401 → Unauthorized
* 403 → Forbidden

---

## Run Project

1. Clone repo
2. Run:
   mvn spring-boot:run
3. Open:
   [http://localhost:8080](http://localhost:8080)

---

## Testing

1. Login → get token
2. Use token in Bearer Auth
3. Call APIs

---

## Assumptions

* Simplified login (no DB auth)
* Roles hardcoded
* JWT used instead of sessions

---

## Future Improvements

* Connect users with database
* Add refresh tokens
* Add Swagger documentation
* Add pagination

---

## Author

Mohd Sadique
