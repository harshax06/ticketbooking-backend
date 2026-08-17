# Build Log

- [x] Day 1: Initialized Spring Boot project, layered package structure

- [x] Day 2: CRUD APIs for Venue & Event with REST conventions

- [x] Day 3: DTOs, Bean Validation, mapper classes

- [x] Day 4: Global exception handling, custom exceptions, standard error format

- [x] Day 5: Seat entity modeled, Venue-Seat relationship, unique constraint on seat position

- [x] Day 6: Repository layer — derived queries, pagination, sorting; bulk seat creation endpoint

- [x] Day 7: Service layer refactor — constructor injection, @Transactional boundaries, consistent patterns

- [x] Day 8: User & Booking entities, relationships wired, DB-level unique constraint on (event, seat)

- [x] Day 9: Naive booking endpoint built - reproduced double-booking race condition under concurrent requests 

- [x] Day 10: Optimistic locking on Seat, clean conflict handling, automated concurrency test proving no double-booking

- [x] Day 11: Pessimistic locking implemented, tested with concurrency test, documented comparison vs optimistic locking

- [x] Day 12: Dynamic multi-field event search (Specifications), JPQL aggregate query