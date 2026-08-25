# Concurrent Event Ticket Booking System

A backend system for booking event tickets under high-concurrency conditions —
built to demonstrate solutions to real distributed-systems problems: race
conditions on seat booking, idempotent payment retries, distributed locking
across instances, and real-time seat map updates.

## Tech Stack
- Java 21, Spring Boot 3.3
- PostgreSQL, Redis (distributed locks, caching)
- Spring Data JPA / Hibernate
- WebSocket (STOMP) for live seat updates
- Docker Compose for local infra
- (Testing stack, CI, etc. — filled in as added)

## Architecture
_Diagram + explanation added Week 1._

## Core Domain
`User`, `Organizer`, `Venue`, `Event`, `Seat`, `BookingMapper`, `Payment`, `Waitlist`, `Notification`

## Running locally
_Instructions added once Docker Compose is in place (Day 8+)._

## Key Engineering Decisions
_A running log of design decisions and tradeoffs — added as they're made._

## Optimistic vs Pessimistic Locking — Tested Comparison

Both strategies were implemented and verified with a 10-thread concurrent
booking test targeting a single seat. Both correctly allow exactly one
booking to succeed and reject the other nine.

| | Optimistic (@Version) | Pessimistic (SELECT FOR UPDATE) |
|---|---|---|
| **How it works** | Read freely, check a version number at write time | Lock the row on read, block other readers until commit |
| **Conflict handling** | Losing threads fail fast and can retry | Losing threads wait in a queue, then proceed safely |
| **Throughput under low contention** | High — no blocking overhead | Slightly lower — locking has inherent overhead even with no real contention |
| **Throughput under high contention** | Degrades — many failed retries | More predictable — requests queue and process serially |
| **Failure mode** | Exception on write; caller must retry or fail | No exception from contention itself — just waits (can time out) |
| **Best fit** | Low-conflict-probability operations (most of a normal day) | High-conflict-probability moments (a popular event's on-sale minute) |

**My takeaway:** For this project, optimistic locking is the better default
— most seats, most of the time, aren't being raced for. But for a viral
on-sale event where hundreds of users hit the same few seats in the same
second, pessimistic locking's queueing behavior gives more predictable,
fair outcomes than a storm of failed optimistic retries. A production
system might reasonably use optimistic locking generally, with a
pessimistic fallback specifically for detected high-demand events.


## N+1 Query Fix

Before: GET /api/v1/events (10 results) generated 11 SQL queries
(1 for events + 1 per event's venue lookup, due to lazy loading).

After: Same endpoint now generates exactly 1 query using
@EntityGraph(attributePaths = {"venue"}), which performs the join
at the database level in a single round trip.

Verified using hibernate.generate_statistics and manual query counting
in logs before/after the fix.
