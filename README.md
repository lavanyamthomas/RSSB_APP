# RSSB Backend (Spring Boot)

Stack: Spring Boot 3, Spring Data JPA, PostgreSQL, Spring Security + JWT.

## Setup

1. Create a PostgreSQL database named `rssb_db`.
2. Optionally set `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (at least 32 chars).
3. Run `mvn spring-boot:run` (http://localhost:8080).

## Auth

`POST /api/auth/login`, `POST /api/auth/register`. Seeded users: `admin` (ROLE_ADMIN) and `sewadar` (ROLE_USER).
Send the JWT in the `Authorization` header using the `Bearer` scheme.

## APIs

- `GET /api/health` (public)
- `POST /api/registrations`
- `GET /api/persons/search?q=&location=&status=&page=0&size=10&sortBy=createdAt&sortDir=desc`
- `GET /api/records/completed?dateFrom=2026-05-01&dateTo=2026-05-31&title=&page=0&size=10&sortBy=completedAt&sortDir=desc`
- `POST /api/walkie/transmit`
