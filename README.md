# Event Ledger

## Overview

Event Ledger is a distributed microservices application built with **Java 21** and **Spring Boot 3.5.3**.

The solution consists of two independently deployable microservices that process financial transaction events while ensuring:

- Idempotent event processing
- Out-of-order event handling
- Distributed tracing
- Service resiliency
- Observability
- Graceful degradation

The services communicate synchronously using REST and maintain independent H2 databases.

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

- **Gateway Service** exposes public APIs.
- **Account Service** manages account balances and transaction history.
- Each service owns its own H2 database.
- Services communicate only through REST APIs.

---

# Requirement Coverage

✔ Idempotent event processing

✔ Out-of-order event handling

✔ Independent microservices

✔ Separate H2 databases

✔ REST communication between services

✔ Distributed trace propagation

✔ Structured logging

✔ Health endpoints

✔ Micrometer custom metrics

✔ Resilience4j Circuit Breaker

✔ Graceful degradation

✔ Docker Compose support

✔ Swagger/OpenAPI documentation

✔ Automated unit tests

---

# Event Gateway

### Responsibilities

- Accept transaction events
- Validate requests
- Prevent duplicate event processing
- Persist event records
- Invoke Account Service
- Generate and propagate Trace IDs
- Collect metrics
- Protect downstream calls using Circuit Breaker

### APIs

| Method | Endpoint |
|---------|----------|
| POST | `/events` |
| GET | `/events/{id}` |
| GET | `/events?account={accountId}` |
| GET | `/health` |

---

# Account Service

### Responsibilities

- Apply account transactions
- Maintain transaction history
- Calculate balances
- Return account details

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
- Docker Compose
- Maven

---

# Key Features

- Independent microservices
- REST-based communication
- Separate H2 database for each service
- Bean Validation
- Global Exception Handling
- Idempotent event processing
- Chronological event ordering
- Balance calculation
- Distributed trace propagation
- Circuit Breaker
- Graceful degradation
- Structured logging
- Health endpoints
- Custom metrics
- Swagger/OpenAPI documentation

---

# Design Decisions

## Idempotency

Each event is uniquely identified by **eventId**.

Duplicate submissions are detected and ignored, ensuring the same transaction is never processed twice.

---

## Out-of-Order Events

Events are always retrieved in ascending order of **eventTimestamp**, ensuring chronological ordering regardless of arrival sequence.

---

## Balance Calculation

```
Balance = Sum(CREDIT) − Sum(DEBIT)
```

Balances are always calculated from the complete transaction history, guaranteeing correctness even when events arrive out of order.

---

## Resiliency

Communication between the Gateway and Account Service is protected using **Resilience4j Circuit Breaker**.

If the Account Service becomes unavailable:

- The Circuit Breaker prevents repeated failing requests.
- The Gateway returns **HTTP 503 Service Unavailable**.
- Previously stored events remain available because they are served from the Gateway database.

---

## Distributed Tracing

Each incoming request carries an **X-Trace-Id**.

The trace identifier is propagated across services:

```
Client
   │
   ▼
Gateway
   │
   ▼
Account Service
```

This enables end-to-end request correlation through application logs.

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
docker compose up --build
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
http://localhost:8080/health
```

Account Service

```
http://localhost:8081/health
```

---

# Testing

Run all tests:

```bash
mvn test
```

Generate JaCoCo report:

```bash
mvn jacoco:report
```

Current coverage:

| Module | Coverage |
|----------|----------|
| Gateway Service | 98% |
| Account Service | 95% |
| Branch Coverage | 100% |

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
│   ├── filter
│   └── exception
│
└── docker-compose.yml
```

---

# Assumptions

- Event IDs are globally unique.
- Account balances are derived from transaction history.
- Each microservice owns its own database.
- Services communicate synchronously using REST.

---

# Future Improvements

Potential production enhancements include:

- OpenTelemetry with Jaeger/Zipkin
- Prometheus + Grafana dashboards
- Retry with exponential backoff and jitter
- Transactional Outbox Pattern
- Kafka-based asynchronous event processing
- Rate limiting
- Authentication and Authorization
- Distributed caching

---

# Author

**Rakesh Baitha**