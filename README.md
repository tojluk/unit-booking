# Booking Service

A reactive Spring Boot application for managing bookings with Redis caching and PostgreSQL database.

## Tech Stack

- Java 21
- Spring Boot 3.4.4
- Spring WebFlux
- R2DBC with PostgreSQL
- Redis for caching
- Liquibase for database migrations
- OpenAPI/Swagger
- Gradle 8.x
- Docker & Docker Compose

## Prerequisites

- JDK 21
- Docker/Podman
- Gradle

## Quick Start

1. Start the infrastructure (PostgreSQL + Redis):
```bash
./env.sh
```
2. Build & Run:
```bash
./gradlew bootRun
```
3. Access the application:
   - Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
   - Localhost: [http://localhost:8080](http://localhost:8080)
   
## Infrastructure Details
1. PostgreSQL
```
Port: 5432
Database: booking_db
Username: tst
Password: tst
```

2. Redis
```
Port: 6379
Persistence enabled
```
