# Build Log

## Day 1 — Project setup
- Initialized Spring Boot project, layered package structure

## Day 2 - CRUD API
- CRUD APIs for Venue & Event with REST conventions

## Day 3 
- DTOs, Bean Validation, mapper classes

## Day 4
- Global exception handling, custom exceptions, standard error format

## Day 5
- Seat entity modeled, Venue-Seat relationship, unique constraint on seat position

## Day 6
- Repository layer — derived queries, pagination, sorting; bulk seat creation endpoint

## Day 7
- Service layer refactor — constructor injection, @Transactional boundaries, consistent patterns

## Day 8
- User & Booking entities, relationships wired, DB-level unique constraint on (event, seat)

## Day 9
- Naive booking endpoint built - reproduced double-booking race condition under concurrent requests 

## Day 10
- Optimistic locking on Seat, clean conflict handling, automated concurrency test proving no double-booking