# Concurrent Event Ticket Booking System

A backend system for booking event tickets under high-concurrency conditions —
built to demonstrate solutions to real distributed-systems problems: race
conditions on seat booking, idempotent payment retries, distributed locking
across instances, and real-time seat map updates.

## Why this project

Most portfolio projects are CRUD wrappers around a database. This one exists
to answer a specific interview question well: *"tell me about a hard
concurrency problem you've solved."* Every core feature here maps to a real
distributed-systems tradeoff, not just an endpoint.

## Status
🚧 In progress — Day 1 of 30. See [PROGRESS.md](./PROGRESS.md) for the build log.

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
`User`, `Organizer`, `Venue`, `Event`, `Seat`, `Booking`, `Payment`, `Waitlist`, `Notification`

## Running locally
_Instructions added once Docker Compose is in place (Day 8+)._

## Key Engineering Decisions
_A running log of design decisions and tradeoffs — added as they're made._
