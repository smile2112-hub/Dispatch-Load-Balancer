# 🚚 Dispatch Load Balancer

A Spring Boot REST API that assigns delivery orders to vehicles based on **priority, vehicle capacity, and minimum travel distance** using the **Haversine Formula**.

## 🛠️ Tech Stack

* Java
* Spring Boot
* Spring Data JPA / Hibernate
* MySQL
* Maven
* JUnit & Mockito
* Postman

## ✨ Features

* Add delivery orders
* Add fleet/vehicle details
* Priority-based order assignment (**HIGH → MEDIUM → LOW**)
* Vehicle capacity validation
* Haversine-based distance calculation
* Optimized dispatch plan
* Input validation and exception handling
* Unit and service-level testing

## 🔌 APIs

### Add Orders

```http
POST /api/dispatch/orders
```

### Add Vehicles

```http
POST /api/dispatch/vehicles
```

### Get Dispatch Plan

```http
GET /api/dispatch/plan
```

## ▶️ Run Locally

### 1. Clone the repository

```bash
git clone <YOUR_REPOSITORY_URL>
cd dispatch-load-balancer
```

### 2. Configure MySQL

Update `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/dispatch_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
```

### 3. Run the application

```bash
mvn clean install
mvn spring-boot:run
```

Application:

```text
http://localhost:8080
```

## 🧪 Testing

APIs can be tested using **Postman**.

Tests cover:

* Order validation
* Vehicle capacity
* Priority assignment
* Distance calculation
* Unassignable orders
* Edge cases

## 📂 Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
MySQL
```

## 👨‍💻 Author

**Mahesh G. Kajale**

PG-DAC | Java Full-Stack Developer
