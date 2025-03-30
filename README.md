# Booking Service

A reactive Spring Boot application for managing bookings with Redis caching and PostgreSQL database.

## Tech Stack

- Java 21
- Spring Boot 3.4.4
- Spring WebFlux
- R2DBC with PostgreSQL
- Redis for caching and locking
- Liquibase for database migrations
- OpenAPI/Swagger
- Gradle 8.x
- Docker & Docker Compose

## Prerequisites

- JDK 21
- Docker/Podman
- Gradle
- Lombok

### Lombok Configuration
1. Install Lombok:
   - IntelliJ IDEA: Install Lombok plugin via Settings -> Plugins
   - Eclipse: Download lombok.jar from [projectlombok.org](https://projectlombok.org/download)
      - Run `java -jar lombok.jar`
      - Select your Eclipse installation
      - Click "Install/Update"
      - Restart Eclipse

2. Enable annotation processing:
   - IntelliJ IDEA: Settings -> Build -> Compiler -> Annotation Processors
   - Eclipse: No additional steps needed after Lombok installation
   
## Quick Start

1. Start the infrastructure (PostgreSQL + Redis):
```bash
   # Requires Docker/Podman installed and running
   # Docker Desktop on Windows/Mac or Docker Engine on Linux
   
   ./env.sh  # This script starts PostgreSQL and Redis containers
```
2. Build & Run:
```bash
   # Set JAVA_HOME (if not set)
   # Windows
   set JAVA_HOME=path\to\jdk-21

   # Linux/MacOS
   export JAVA_HOME=/path/to/jdk-21

   # Build and run
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
