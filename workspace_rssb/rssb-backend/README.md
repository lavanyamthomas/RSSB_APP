# RSSB Backend (Spring Boot)

## Stack

- Spring Boot 3
- Spring Data JPA
- PostgreSQL
- Spring Security + JWT

## Setup

1. Create a PostgreSQL database named `rssb_db`.
2. Configure environment variables (or use defaults):
   - `DB_URL=jdbc:postgresql://localhost:5432/rssb_db`
   - `DB_USERNAME=postgres`
   - `DB_PASSWORD=postgres`
   - `JWT_SECRET=<long-random-secret-at-least-32-chars>`
3. Run the app:

```bash
mvn spring-boot:run
```

Backend URL: http://localhost:8080

## Auth

- `POST /api/auth/login`
- `POST /api/auth/register`

Seeded users:

- admin / Admin@123 (ROLE_ADMIN)
- sewadar / Sewadar@123 (ROLE_USER)

Pass JWT in header:

`Authorization: Bearer <token>`

## APIs

- `GET /api/health` (public)
- `POST /api/registrations` (USER/ADMIN)
- `GET /api/persons/search` (USER/ADMIN)
- `GET /api/records/completed` (USER/ADMIN)
- `POST /api/walkie/transmit` (USER/ADMIN)

### Search API with Pagination/Filter/Sort

`GET /api/persons/search?q=&location=&status=&page=0&size=10&sortBy=createdAt&sortDir=desc`

### Records API with Pagination/Filter/Sort

`GET /api/records/completed?dateFrom=2026-05-01&dateTo=2026-05-31&title=&page=0&size=10&sortBy=completedAt&sortDir=desc`
