# Employee Management System

A complete Employee Management System built with Spring Boot 3, Java 21, MySQL, JWT security, and Docker.

## Features
- JWT-based authentication
- Role-based access control
- Employee CRUD management
- Department and job title management
- Leave request workflow
- Audit logging
- OpenAPI documentation
- Dockerized MySQL database

## Prerequisites
- Java 21+
- Maven 3.9+
- Docker & Docker Compose

> **Note:** If local MySQL is already running on port 3306, stop it first:
> `brew services stop mysql`

## Quick Start (Docker MySQL)

### 1. Start MySQL container
```bash
docker compose up -d
```

Wait until MySQL is healthy:
```bash
docker compose ps
```

### 2. Run the application
```bash
mvn spring-boot:run
```

Or build and run the JAR:
```bash
mvn clean package -DskipTests
java -jar target/employee-management-0.0.1-SNAPSHOT.jar
```

### 3. Access the application
- **Web UI:** http://localhost:8080
- **Login:** http://localhost:8080/login
- **API docs:** http://localhost:8080/swagger-ui.html
- **Health:** http://localhost:8080/actuator/health

### Default accounts
| Username | Password   | Role  |
|----------|------------|-------|
| admin    | Admin@123  | Admin |
| hr       | Hr@12345   | HR    |

## Database Configuration

MySQL runs in Docker with these settings (see `docker-compose.yml`):

| Setting      | Value                |
|--------------|----------------------|
| Container    | employee-mysql       |
| Database     | employee_management  |
| Username     | root                 |
| Password     | root123              |
| Port         | 3306                 |

Spring Boot connects via `application.properties`:
```
jdbc:mysql://localhost:3306/employee_management
username: root
password: root123
spring.jpa.hibernate.ddl-auto=update
```

Override with environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`.

## Optional: H2 in-memory (no Docker)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## Stop MySQL
```bash
docker compose down
```

Data persists in the `employee-mysql-data` Docker volume. To remove data:
```bash
docker compose down -v
```

## Deployment
Configure environment variables for `JWT_SECRET`, `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` when deploying to production.
