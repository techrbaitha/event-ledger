# Event Ledger

A distributed Event Ledger system built using **Java 21** and **Spring Boot 3.5.3**. The application consists of two independent microservices that process financial transaction events while ensuring idempotency, resiliency, observability, and traceability.

---

# Architecture

```
                        +---------------------------+
                        |      Client / Browser     |
                        +------------+--------------+
                                     |
                                     | REST
                                     |
                        +------------v--------------+
                        |     Event Gateway API     |
                        |   (Public Facing Service) |
                        +------------+--------------+
                                     |
                         OpenFeign + REST
                                     |
                        +------------v--------------+
                        |      Account Service      |
                        |    (Internal Service)     |
                        +---------------------------+
```

## Event Gateway

Responsible for:

- Accepting transaction events
- Request validation
- Idempotency validation
- Persisting event records
- Calling Account Service
- Distributed tracing
- Circuit breaker protection
- Metrics collection

### APIs

| Method | Endpoint |
|---------|----------|
| POST | `/events` |
| GET | `/events/{id}` |
| GET | `/events?account={accountId}` |
| GET | `/health` |

---

## Account Service

Responsible for:

- Applying account transactions
- Maintaining transaction history
- Computing account balances
- Returning account details

### APIs

| Method | Endpoint |
|---------|----------|
| POST | `/accounts/{accountId}/transactions` |
| GET | `/accounts/{accountId}/balance` |
| GET | `/accounts/{accountId}` |
| GET | `/health` |

---

# Technology Stack

- Java 21
- Spring Boot 3.5.3
- Spring Data JPA
- Spring Validation
- OpenFeign
- Resilience4j
- Micrometer
- Spring Boot Actuator
- H2 Database
- Lombok
- OpenAPI / Swagger
- Docker
- Maven

---

# Features

- Independent microservices
- REST based communication
- H2 database for each service
- Bean Validation
- Global Exception Handling
- Idempotent event processing
- Event ordering by timestamp
- Balance calculation
- Distributed trace propagation
- Circuit Breaker
- Health endpoints
- Custom metrics
- Docker Compose support

---

# Design Decisions

## Idempotency

Each event is uniquely identified by **eventId**.

Duplicate submissions are detected and prevented to ensure the same event is never processed twice.

---

## Out-of-order Events

Events are always retrieved in ascending order of **eventTimestamp**, ensuring chronological ordering regardless of arrival order.

---

## Balance Calculation

Current balance is computed as:

```
Balance = Sum(CREDIT) - Sum(DEBIT)
```

---

## Resiliency

The Gateway uses **Resilience4j Circuit Breaker** while communicating with the Account Service.

When the Account Service becomes unavailable:

- Circuit Breaker prevents repeated failing requests.
- Gateway returns **503 Service Unavailable**.
- Event retrieval endpoints continue to function using locally stored data.

---

## Distributed Tracing

Each incoming request generates a unique **X-Trace-Id**.

The trace identifier is propagated from:

```
Client
   ↓
Gateway
   ↓
Account Service
```

allowing complete request tracing across services.

---

## Observability

Implemented using:

- Structured logging
- Trace IDs
- Spring Boot Actuator
- Micrometer custom metrics
- Health endpoints

---

# Running the Project

## Prerequisites

- Java 21
- Maven 3.9+
- Docker (optional)

---

## Build

From the project root:

```bash
mvn clean package
```

---

## Run Manually

### Start Account Service

```bash
cd account-service
mvn spring-boot:run
```

Runs on:

```
http://localhost:8081
```

---

### Start Gateway Service

```bash
cd gateway-service
mvn spring-boot:run
```

Runs on:

```
http://localhost:8080
```

---

# Run Using Docker

```bash
docker-compose up --build
```

---

# Swagger

Gateway

```
http://localhost:8080/swagger-ui.html
```

Account Service

```
http://localhost:8081/swagger-ui.html
```

---

# Health Endpoints

Gateway

```
GET /health
```

Account Service

```
GET /health
```

---

# Testing

Run all tests:

```bash
mvn test
```

---

# Project Structure

```
event-ledger
│
├── gateway-service
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   ├── client
│   ├── filter
│   ├── config
│   └── exception
│
├── account-service
│   ├── controller
│   ├── service
│   ├── repository
│   ├── entity
│   ├── dto
│   └── exception
│
└── docker-compose.yml
```

---

# Assumptions

- Event IDs are globally unique.
- Account balances are derived from transaction history.
- Gateway and Account Service maintain separate databases.
- Communication between services is synchronous using REST.

---

# Future Improvements

The current implementation satisfies the assessment requirements.

Potential production enhancements include:

- Transactional Outbox Pattern
- Kafka/Event Streaming
- OpenTelemetry with Jaeger
- Prometheus + Grafana
- Retry with exponential backoff
- Distributed caching
- Rate limiting
- Authentication and Authorization

---

# Author

Rakesh Baitha