# 📘 Book Management System - Microservices Architecture

## 📝 Overview
This project is a **Microservices-based Book Management System** with authentication and authorization using **Spring Security (JWT)**. It includes the following services:

- **Service Registry** (Eureka Server)
- **API Gateway** (Spring Cloud Gateway)
- **Security Service** (User Authentication & Authorization with JWT)
- **Book Service** (CRUD operations for books, secured with JWT)

Each service is designed to be independently deployable and scalable.

---

## 🏗 Architecture
The system follows a **microservices architecture** with the following components:

- **Service Registry** (Eureka Server)
    - Manages service discovery.
    - All microservices must register with the service registry before communication.
    - **Must be started first** before other services.

- **API Gateway**
    - Routes incoming requests to appropriate microservices.
    - Implements JWT-based authentication and authorization.
    - Uses **Resilience4j Circuit Breaker** for fault tolerance.

- **Security Service**
    - Manages user authentication & authorization.
    - Generates JWT tokens for authenticated users.
    - Stores user details in a **PostgreSQL** database.

- **Book Service**
    - Provides book management features (Create, Read, Update, Delete).
    - Secured with JWT authentication.
    - Stores book data in a **PostgreSQL** database.

---

## 🚀 Technologies Used
- **Spring Boot** (Microservices framework)
- **Spring Security & JWT** (Authentication & Authorization)
- **Spring Cloud Gateway** (API Gateway)
- **Eureka Server** (Service Registry)
- **PostgreSQL** (Database for user & book storage)
- **H2 Database** (Optional in-memory DB for development)
- **Resilience4j** (Circuit Breaker for fault tolerance)

---

## ⚙️ Setup & Installation

Ensure you have the two databases setup in postgres and do change the password to yours in the yml files
- mtech_books_db
- mtech_users_db


### 1️⃣ Start the **Service Registry** (Eureka Server)
> This **must be started first** to allow other services to register.

```bash
cd service-registry
mvn spring-boot:run
```

### 2️⃣ Start the API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

### 3️⃣ Start the Security Service (User Authentication)
```bash
cd security-service
mvn spring-boot:run
```

### 4️⃣ Start the Book Service

```bash
cd book-service
mvn spring-boot:run
```

## 🔑 Authentication & Authorization
User Registration (POST http://localhost:9090/api/v1/auth/register)
```json
{
  "firstName": "Jipheens",
  "lastName": "Wahome",
  "email": "wahome@example.com",
  "password": "password123",
  "role": "USER"
}

```

User Login (POST http://localhost:9090/api/v1/auth/authenticate)
```json
{
  "email": "wahome@example.com",
  "password": "password123"
}
```


###  Example Protected API Call 
(GET http://localhost:9090/api/v1/books)
```shell
GET /api/v1/books
Authorization: Bearer <JWT_TOKEN>
```