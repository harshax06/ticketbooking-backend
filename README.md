
# 🎟️ Concurrent Ticket Booking System

A Spring Boot backend that provably prevents double-booking under real concurrent load — a production-style ticketing platform built to demonstrate real backend engineering: concurrency control, security, caching, real-time updates, and deployment, rather than another CRUD tutorial clone.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red)](https://redis.io/)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](./LICENSE)

🔗 **Live demo:** https://ticketbooking-backend-x63o.onrender.com
📘 **API docs (Swagger):** https://ticketbooking-backend-x63o.onrender.com/swagger-ui.html
📮 **Postman collection:** [`/postman/ticketbooking.postman_collection.json`](./postman/ticketbooking.postman_collection.json)

> **Note:** the live demo runs on Render's free tier, which spins down after inactivity. The first request after idle time may take 30–60 seconds to respond while the instance wakes up.

---

## Why this project exists

Most backend portfolio projects are e-commerce CRUD clones — every reviewer has seen dozens of them. This is a **ticket booking system** instead, chosen specifically because it forces real engineering decisions a CRUD app never surfaces:

- **Race conditions** — two users trying to book the same seat in the same instant
- **Concurrency control** — optimistic vs. pessimistic locking, implemented and tested side by side
- **Abuse prevention** — rate limiting the exact endpoint most vulnerable to bots (a concert's on-sale moment)
- **Real-time state** — pushing live seat availability to every connected client via WebSocket
- **Cache correctness** — Redis caching with explicit invalidation, not just "add a cache and hope"

Every one of these is backed by real, working code — and for the concurrency claims specifically, by an automated multi-threaded test that proves it.

---

## Table of contents

- [Architecture](#architecture)
- [Tech stack](#tech-stack)
- [The concurrency story](#the-concurrency-story)
- [Security](#security)
- [Caching](#caching)
- [Real-time seat updates](#real-time-seat-updates)
- [Getting started](#getting-started)
- [API overview](#api-overview)
- [Testing](#testing)
- [Deployment](#deployment)
- [Project structure](#project-structure)
- [What I'd do differently](#what-id-do-differently)
- [License](#license)
- [Author](#author)

---

## Architecture

```
┌─────────────┐      ┌──────────────────┐      ┌─────────────┐
│   Client    │─────▶│   Spring Boot    │─────▶│  PostgreSQL │
│ (Browser /  │◀─────│   REST + WS API  │◀─────│  (Render)   │
│  Postman)   │      │                  │      └─────────────┘
└─────────────┘      │  ┌────────────┐  │      ┌─────────────┐
                      │  │   Redis    │◀─┼─────▶│   Cache     │
                      │  └────────────┘  │      │  (Render)   │
                      └──────────────────┘      └─────────────┘
        Deployed as a Docker container on Render (Web Service)
```

**Domain model:**
```
Venue (1) ──< Seat (many)
Venue (1) ──< Event (many)
Event (1) ──< Booking (many)
Seat  (1) ──< Booking (many)
User  (1) ──< Booking (many)
User  (1) ──< Event (many, as organizer)

Constraint: (event_id, seat_id) is UNIQUE in bookings
            → a seat can only be booked once per event
```

**Key design decision:** booking status lives on the `Booking` table, not as a flag on `Seat`. A seat isn't permanently "booked" — it's booked *for one specific event*. The same physical seat is free again for a different event at the same venue. This normalization is what makes the concurrency-safety work below possible to reason about cleanly.

---

## Tech stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 (Web, Data JPA, Security, Validation, WebSocket, Cache, Actuator) |
| Database | PostgreSQL 16 |
| Caching | Redis 7 |
| Auth | JWT (access + refresh tokens), BCrypt password hashing |
| Real-time | WebSocket (STOMP over SockJS) |
| Rate limiting | Bucket4j |
| API docs | springdoc-openapi (Swagger UI) |
| Testing | JUnit 5, Mockito, MockMvc, multi-threaded concurrency tests, JaCoCo coverage |
| Containerization | Docker (multi-stage build), Docker Compose |
| CI | GitHub Actions — tests run against a real ephemeral Postgres + Redis |
| Deployment | Render (Web Service + managed Postgres + managed Redis) |

---

## The concurrency story

This is the centerpiece of the project. Two locking strategies are implemented, each verified with a real multi-threaded test that fires 10 concurrent requests at the same seat.

### Optimistic locking (`@Version`)
Assumes conflicts are rare. Reads freely; at write time, Hibernate checks whether the row's version still matches what was read. If another transaction beat it to the write, the update fails and the caller can retry.

### Pessimistic locking (`SELECT ... FOR UPDATE`)
Assumes conflicts are likely. Locks the row the instant it's read — every other transaction trying to touch that row blocks until the lock holder commits or rolls back.

### Tested comparison

| | Optimistic | Pessimistic |
|---|---|---|
| Conflict handling | Losing threads fail fast, must retry | Losing threads wait in a queue, then proceed safely |
| Throughput, low contention | High — no blocking overhead | Slightly lower — locking has inherent overhead |
| Throughput, high contention | Degrades — many failed retries | More predictable — requests queue and process serially |
| Best fit | Most traffic, most of the time | A popular event's on-sale minute |

**Verified with:** `BookingConcurrencyTest` and `BookingPessimisticConcurrencyTest` — both fire 10 threads at a single seat simultaneously, using a `CountDownLatch` pair that releases every thread at once to force a genuine race rather than letting requests trickle in sequentially. Both consistently produce exactly **1 successful booking and 9 clean rejections**, with the database left in a provably consistent state — run repeatedly, not just once.

Beyond the locking layer itself, a database-level `UNIQUE(event_id, seat_id)` constraint is the final backstop: even if application logic were bypassed entirely, Postgres itself refuses a duplicate booking row.

---

## Security

- **JWT auth** — short-lived access tokens (15 min) + revocable, database-backed refresh tokens (7 days)
- **BCrypt** password hashing — deliberately slow with per-password salting, unlike a fast hash such as SHA-256
- **Role-based + ownership-based authorization** — `@PreAuthorize` with a custom permission evaluator, so an organizer can edit their own events but not someone else's
- **Rate limiting** on the booking endpoint specifically (Bucket4j) — the direct answer to "how would you stop bots scalping tickets the second an event goes live"
- **CORS** configured explicitly and understood as a browser-enforced mechanism, not a substitute for authentication
- **No user enumeration** — login and password-reset return identical responses whether an account exists or not
- **Soft delete** for events via `@SQLDelete`/`@SQLRestriction` — cancelled events are hidden from normal queries but retained for refund/audit history, with an admin-only endpoint to view them
- Every error path (400, 401, 403, 409, 500) returns a consistent, non-leaking JSON shape — no stack traces or internal details ever reach the client

---

## Caching

`GET /api/v1/events` is high-read, low-write — a natural fit for caching. Redis caches the query result; every write to `Event` (`create`, `update`, `delete`) explicitly evicts the cache so stale data is never served. This was deliberately built with correctness first: a cache that's never invalidated is a bug waiting to happen, not a performance win.

---

## Real-time seat updates

Booking a seat broadcasts a message over a STOMP WebSocket topic (`/topic/events/{eventId}/seats`) the instant the booking commits. Every browser tab watching that event's seat map updates live, with no polling and no refresh delay — the correct architecture for this problem, not just a more impressive-sounding one.

---

## Getting started

### Prerequisites
- Docker + Docker Compose
- (For local dev without Docker) Java 21, Maven, PostgreSQL 16, Redis 7

### Run everything with one command

```bash
git clone https://github.com/harshax06/ticketbooking-backend.git
cd ticketbooking-backend
```

Create a `.env` file in the project root (not committed — see `.gitignore`):

```
JWT_SECRET=<generate-your-own-32-byte-base64-value>
```

```bash
docker-compose up --build
```

This builds the app image, starts Postgres and Redis, waits for Postgres to be healthy, then starts the app.

The API is available at `http://localhost:8080`. Swagger UI at `http://localhost:8080/swagger-ui.html`.

### Run locally without Docker (dev profile)

```bash
docker run -d --name ticketbooking-db -p 5433:5432 \
  -e POSTGRES_DB=ticketbooking -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres \
  postgres:16
docker run -d --name ticketbooking-redis -p 6379:6379 redis:7-alpine

# Run with the dev profile active (IntelliJ: set in Run Configuration)
```

### Run the test suite

```bash
./mvnw clean test
```

Includes unit tests (Mockito), DTO validation tests, security integration tests (MockMvc), and two multi-threaded concurrency tests — run against a real PostgreSQL instance, both locally and in CI.

---

## API overview

Full interactive documentation is available via Swagger UI. Highlights:

| Method | Endpoint | Auth | Notes |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Returns access + refresh token |
| `POST` | `/api/v1/auth/login` | Public | |
| `POST` | `/api/v1/auth/refresh` | Public (valid refresh token) | Issues new access token |
| `POST` | `/api/v1/auth/forgot-password` | Public | Always 200, regardless of whether email exists |
| `GET` | `/api/v1/events` | Public | Cached, paginated event listing |
| `GET` | `/api/v1/events/search` | Public | Dynamic filtering via Specifications (city, category, date range, keyword) |
| `GET` | `/api/v1/events/deleted` | Admin | Soft-deleted events, native-query bypass of the delete filter |
| `POST` | `/api/v1/venues/{id}/seats` | Organizer/Admin | Bulk seat creation |
| `POST` | `/api/v1/bookings` | User | Optimistic-locking booking path, rate-limited |
| `POST` | `/api/v1/bookings/pessimistic` | User | Pessimistic-locking booking path (comparison) |
| `WS` | `/ws` → `/topic/events/{id}/seats` | Public | Live seat availability broadcast |

All error responses follow a consistent shape:

```json
{
  "timestamp": "2026-09-01T10:00:00",
  "status": 409,
  "error": "SEAT_UNAVAILABLE",
  "message": "Seat A1 is already booked for this event",
  "path": "/api/v1/bookings",
  "details": null
}
```

---

## Testing

The test suite is layered deliberately:

- **Unit tests** (Mockito) — service-layer business logic in isolation, milliseconds to run
- **Validation tests** — Bean Validation constraints (e.g. `@Future` on event start time)
- **Security integration tests** (MockMvc) — exercise the real `SecurityFilterChain`, not a mocked stand-in
- **Concurrency tests** — real multi-threaded races against a real PostgreSQL instance, proving correctness under load

```bash
./mvnw test                                       # everything
./mvnw test -Dtest=BookingConcurrencyTest          # optimistic-locking race test
./mvnw test -Dtest=BookingPessimisticConcurrencyTest
./mvnw clean test jacoco:report                    # generate coverage report
```

Coverage sits around 53% overall, concentrated deliberately: the booking/concurrency logic and auth flows carry the heaviest test weight, since correctness there matters most. Controllers and query-building code are thinner (they're largely pass-through/declarative layers, verified extensively through manual and Postman testing during development).

---

## Deployment

Deployed on **Render** as a Docker Web Service, with managed Postgres and managed Redis (Key-Value) instances in the same region for low-latency internal networking. Configuration is environment-variable driven — no secrets are committed to source control:

- `application-dev.yml` — safe placeholder JWT fallback for local convenience
- `application-docker.yml` — no fallback; a real secret **must** be supplied via environment variable, or the app refuses to start
- CI runs against a genuine ephemeral Postgres + Redis via GitHub Actions, so tests behave identically locally and in CI

---

## Project structure

```
src/main/java/com/harsha/ticketbooking/
├── config/            # Security, CORS, caching, WebSocket, OpenAPI, JPA auditing config
├── controller/        # REST controllers
├── service/           # Business logic, transaction boundaries
├── repository/        # Spring Data JPA repositories + Specifications
├── entity/            # JPA entities
├── dto/
│   ├── request/
│   └── response/
├── exception/         # Custom exceptions + global handler
├── mapper/            # Entity <-> DTO mapping
└── security/          # JWT service, filters, permission evaluators
```

---

## What I'd do differently

Honest notes, because a project without any is less credible than one with a few:

- **Database migrations** — this project relies on Hibernate's `ddl-auto` rather than a dedicated migration tool. A production version would use Flyway or Liquibase from day one, especially given a couple of `NOT NULL`-column migration snags hit during development.
- **Idempotency keys** — a dedicated idempotency-key store for the booking/payment endpoint (to safely handle retried requests) is a natural next iteration beyond the current locking-based safety.
- **Environment-driven config across three targets** (local dev, Docker Compose, Render) surfaced real friction — JDBC URL formats, credential placement, and health-check timing all needed separate handling. Worth standardizing earlier next time, rather than discovering each mismatch one deploy at a time.

---

## License

Distributed under the MIT License. See [`LICENSE`](./LICENSE) for details.

---

## Author

Built by **Harsha** as a full-cycle backend engineering project — from initial design through production deployment.

🐙 [GitHub](https://github.com/harshax06)