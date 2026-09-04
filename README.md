# FinTech Digital Wallet and Transaction Engine API

A production-minded REST API for user registration, wallet management, deposits, authenticated transfers, and transaction history. The service is packaged as a Spring Boot application and runs with PostgreSQL through Docker Compose.

## Architecture Overview

The application follows a layered architecture with explicit boundaries:

```text
HTTP clients
    |
    v
Controllers  ->  request validation and HTTP responses
    |
    v
Services     ->  business rules and transaction boundaries
    |
    v
Repositories ->  Spring Data JPA persistence and locking
    |
    v
PostgreSQL
```

- `controller`: REST endpoints under `/api/v1`.
- `dto`: validated request models and response models, keeping the API contract separate from persistence entities.
- `service`: registration, authentication, wallet, and transfer workflows.
- `repository`: database access, filtered transaction queries, and wallet locking.
- `entity`: JPA domain model for users, wallets, and transactions.
- `security`: JWT creation, parsing, and request authentication.
- `mapper`: conversion between entities and API DTOs.
- `exception`: centralized API error handling.

## Tech Stack

- Java 17
- Spring Boot 4.1
- Spring MVC
- Spring Data JPA and Hibernate
- Spring Security with JWT
- PostgreSQL 16
- Jakarta Bean Validation
- springdoc OpenAPI and Swagger UI
- Maven Wrapper
- Docker and Docker Compose
- JUnit, Spring Boot Test, Mockito, and H2 for tests

## Key Features

- User registration with a securely encoded password and an initial USD wallet.
- JWT-based login and stateless request authentication.
- Authenticated wallet lookup and deposits with ownership checks.
- Wallet-to-wallet transfers with self-transfer and balance validation.
- Pessimistic wallet locking to protect concurrent balance updates.
- Paginated and filtered transaction history by date, type, and status.
- Consistent validation and REST error responses.
- Interactive OpenAPI documentation generated from controller annotations.
- Reproducible multi-stage Docker image with a JDK build stage and a lightweight JRE runtime stage.

## Setup

### Prerequisites

Install Docker Desktop with Docker Compose support. No local Java, Maven, or PostgreSQL installation is required for the containerized workflow.

### Start the application

From the repository root:

```bash
docker compose up --build
```

The application starts on `http://localhost:8080`. PostgreSQL runs inside the Compose network and is available to the application as `postgres:5432`. The database data is retained in the `postgres-data` named volume.

To run the stack in the background:

```bash
docker compose up --build -d
```

To stop the stack:

```bash
docker compose down
```

PostgreSQL is also published on `localhost:5432` so the application can be started directly from IntelliJ using the default datasource settings. Stop any other PostgreSQL service using port `5432` before starting Compose.

### Local Maven workflow

For a local Java 17 or 21 environment, start the database first:

```bash
docker compose up -d postgres
```

Then start the application from IntelliJ or the Maven wrapper. The default local connection is `postgres/secret` on `localhost:5432`:

```bash
./mvnw test
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` in place of `./mvnw`.

Database credentials and JWT settings can be overridden with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`, `JWT_SECRET`, and `JWT_EXPIRATION_MS`.

## API Documentation

Once the application is running, open the interactive Swagger UI:

[Swagger UI](http://localhost:8080/swagger-ui.html)

The generated OpenAPI document is available at:

[OpenAPI JSON](http://localhost:8080/v3/api-docs)

Authentication endpoints are public. To test protected endpoints:

1. Use `POST /api/v1/users` to register a user.
2. Use `POST /api/v1/auth/login` and copy the returned JWT.
3. Select **Authorize** in Swagger UI and enter the token (without the `Bearer ` prefix).
4. Execute the protected operations; Swagger UI adds the bearer header automatically.

The remaining API endpoints require the bearer token returned by `POST /api/v1/auth/login`.

## Core Endpoints

| Method | Endpoint | Purpose | Access |
| --- | --- | --- | --- |
| `POST` | `/api/v1/users` | Register a user and create a wallet | Public |
| `POST` | `/api/v1/auth/login` | Authenticate and issue a JWT | Public |
| `GET` | `/api/v1/wallets/me` | Read the current user's wallet | Authenticated |
| `POST` | `/api/v1/wallets/deposit` | Deposit into the current user's wallet | Authenticated |
| `POST` | `/api/v1/transactions/transfer` | Transfer funds between wallets | Authenticated |
| `GET` | `/api/v1/transactions` | Query the current user's transactions | Authenticated |
| `GET` | `/api/v1/users` | List all users | Admin role |

## Engineering Highlights

### Transaction boundaries

State-changing workflows use `@Transactional` so wallet changes and transaction records commit atomically. Read-only queries use `@Transactional(readOnly = true)` where appropriate. Registration creates the user and initial wallet in one unit of work.

### Security

Spring Security runs in stateless mode. A JWT authentication filter extracts and validates bearer tokens before protected controllers execute. Passwords are stored using BCrypt, and service-level ownership checks prevent a user from depositing to or transferring from another user's wallet.

### Locking and concurrency

Transfer operations load both wallets with a repository method using `@Lock(LockModeType.PESSIMISTIC_WRITE)`. The database locks the rows for the duration of the transaction, preventing concurrent transfers from observing and spending the same balance.

### Validation and API contracts

Request DTOs use Jakarta Bean Validation and controllers apply `@Valid`. OpenAPI annotations describe controller tags, operations, and response outcomes, keeping the interactive documentation close to the behavior it represents.

## Verification

Run the automated test suite with:

```bash
./mvnw test
```

Build the production artifact without tests with:

```bash
./mvnw -DskipTests package
```
