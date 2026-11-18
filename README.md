# Dispatch Load Balancer

A Spring Boot REST API that assigns delivery orders to vehicles by priority, capacity, and the shortest Haversine distance.

## Tech Stack

- Java 21
- Spring Boot
- Spring Validation
- Maven
- JUnit and Spring Boot Test
- Docker
- Jenkins

## APIs

```http
POST /api/dispatch/orders
POST /api/dispatch/vehicles
GET  /api/dispatch/plan
```

## Run Locally

Prerequisites: JDK 21.

```bash
./mvnw clean package
./mvnw spring-boot:run
```

The application runs at `http://localhost:8080`.

## Run with Docker

```bash
docker build -t dispatch-load-balancer .
docker run --rm -p 8080:8080 dispatch-load-balancer
```

## Testing

```bash
./mvnw test
```

Tests cover order validation, vehicle capacity, priority assignment, distance calculation, unassignable orders, and controller behavior.

## Jenkins CI/CD Pipeline

The root `Jenkinsfile` displays the CI/CD flow in Jenkins:

1. Check out the source code.
2. Compile the application.
3. Run the test suite.
4. Package the executable JAR.
5. Build a Docker image.
6. Push the image to a Docker registry for `main` builds.

Configure these Jenkins environment variables or credentials:

- `DOCKER_IMAGE`: registry image name, for example `your-user/dispatch-load-balancer`
- `DOCKER_CREDENTIALS_ID`: Jenkins Docker registry credentials ID
