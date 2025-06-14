# Medical Profile Service

A microservice built with **Spring Boot 3.5.0**, **Java 21 (Oracle JDK)**, and **PostgreSQL**. This service is part of a larger **MediCore - Medical Profile Management System** project. It is container-ready and will be integrated with **Kafka**, **AWS**, and **Docker** for cloud deployment and communication.

---

## Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Dependencies](#dependencies)
- [Project Setup](#project-setup)
- [Development Configuration](#development-configuration)
- [Run Locally Using H2 DB](#run-locally-using-h2-db)
- [Access H2 Console](#access-h2-console)
- [Docker Setup](#docker-setup)
  - [PostgreSQL Container](#postgresql-container)
  - [Dockerfile For Application](#dockerfile-for-application)
  - [Docker Run Configuration](#docker-run-configuration)
- [PostgreSQL Configuration in `application.properties`](#postgresql-configuration-in-applicationproperties)
- [IntelliJ DB Integration](#intellij-db-integration)
- [API Testing in Dockerized Setup](#api-testing-in-dockerized-setup)
- [Global Error Handling](#global-error-handling)
- [OpenAPI Documentation](#openapi-documentation)
- [Development Notes / Change Log](#development-notes--change-log)

---

## Features

- **REST API**: Provides endpoints to retrieve, create, update, and delete medical profiles.
- **Layered Architecture**: Separates concerns into controller, service, and repository layers (along with model, DTO, and mapper).
- **Validation**: Uses annotations to enforce data integrity.
- **DTO and Mapper**: Keeps API responses clean and decoupled from database structure. Mapper now supports entity-to-DTO and DTO-to-entity conversion.
- **Grouped Validation Logic**: Uses a validation group interface (`CreateMedicalProfileValidationGroup`) to conditionally apply validation rules like `time of registration` only during create operations.
- **Dev-friendly Setup**: Uses H2 in-memory DB for rapid development and testing.
- **OpenAPI Documentation**: Integrated using SpringDoc with `@Tag` and `@Operation` annotations to generate Swagger-compatible docs.
- **Global Error Handling**: Centralized exception handling for clean and user-friendly error responses.
- **Dockerized Microservice**: Fully containerized Spring Boot app using a multi-stage Dockerfile and PostgreSQL container for persistent storage.
- **Production-Ready PostgreSQL**: Switched from in-memory H2 to PostgreSQL running in a Docker container.

---

## Tech Stack

| Category   | Technology           | Description                                        |
| ---------- |----------------------| -------------------------------------------------- |
| Backend    | Spring Boot 3.5.0    | Framework for building RESTful microservices       |
| Language   | Java 21 (Oracle JDK) | Long-Term Support version for enterprise stability |
| Database   | PostgreSQL, H2 (dev) | PostgreSQL for production, H2 for dev/testing      |
| Validation | Hibernate Validator  | Annotation-based request and entity validation     |
| Docs       | SpringDoc OpenAPI    | Auto-generates Swagger UI from code annotations    |
| Container  | Docker               | Containerization using multi-stage build           |
| Messaging  | Kafka (planned)      | For event-driven communication between services    |
| Cloud      | AWS (planned)        | For deploying microservices in the cloud           |

---

## Dependencies

- `spring-boot-starter-web`: For creating REST APIs
- `spring-boot-starter-data-jpa`: For interacting with databases using JPA
- `spring-boot-devtools`: Enables hot reload during development
- `spring-boot-starter-validation`: Supports bean validation using annotations
- `postgresql`: JDBC driver to connect to PostgreSQL
- `com.h2database:h2`: In-memory database for development and testing
- `springdoc-openapi-starter-webmvc-ui`: To generate OpenAPI docs with Swagger UI

---

## Project Setup

### Prerequisites

- Java 21 installed
- Maven or Gradle (depending on your build tool)
- IDE (e.g., IntelliJ IDEA)
- Docker

## Development Configuration

- `application.properties`: Configured to use the H2 database for ease of development
- `server.port=8081`: Port changed from default `8080` to avoid conflicts
- `data.sql`: Auto-loaded by Spring Boot to insert dummy data at startup

---

### Run Locally using H2 DB

1. Clone the repository
2. Navigate to `medical-profile-service`
3. Run the application using your IDE or command line:
   ```bash
   ./mvnw spring-boot:run
   # or
   ./gradlew bootRun
   ```
4. Access the API at: `http://localhost:8081/medical-profiles`

---

### Access H2 Console

Spring Boot makes it easy to view and interact with the H2 database via a browser:

- URL: `http://localhost:8081/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `profile`
- Password: `profile` *(unless you changed it in application.properties)*

Make Sure to uncomment H2-configuration in your `application.properties` file.
Make sure this is present in your `application.properties`:

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

![img_1.png](assets/img_1.png)

---

## Docker Setup

### PostgreSQL Container

A PostgreSQL container was created using the latest PostgreSQL image with the following configuration:

```bash
docker run --name medical-profile-service-db \
  -e POSTGRES_USER=profile \
  -e POSTGRES_PASSWORD=profile \
  -e POSTGRES_DB=db \
  -p 5000:5432 \
  -v medical-profile-db-data:/var/lib/postgresql/data \
  --network internal \
  -d postgres:latest
```

- Port mapped: `5000:5432`
- Persistent storage: Named volume `medical-profile-db-data`
- Network: Internal Docker network named `internal`

![img.png](assets/imgH.png)

---

### Dockerfile for Application

A multi-stage Dockerfile was created in the `medical-profile-service` directory:

### Docker Run Configuration

In IntelliJ IDEA:

- Image name: `medical-profile-service:latest`
- Container name: `medical-profile-service`
- Dockerfile path: `medical-profile-service/Dockerfile`
- Environment Variables:
   - `SPRING_DATASOURCE_URL=jdbc:postgresql://medical-profile-service-db:5432/db`
   - `SPRING_DATASOURCE_USERNAME=profile`
   - `SPRING_DATASOURCE_PASSWORD=profile`
   - `SPRING_JPA_HIBERNATE_DDL_AUTO=update`
   - `SPRING_SQL_INIT_MODE=always`
- Port Binding: `8081:8081`
- Run Option: `--network internal`

### PostgreSQL Configuration in `application.properties`

Commented out H2-related settings and retained only essential production config:

![img.png](assets/imgI.png)

---

### IntelliJ DB Integration

- Connected to the running PostgreSQL container from IntelliJ using:
   - Name: `medical-profile-service-db`
   - JDBC URL: `jdbc:postgresql://localhost:5000/db`
   - Username: `profile`, Password: `profile`
- Verified: Tables created and dummy data from `data.sql` available in database

![img.png](assets/imgJ.png)

---

### API Testing in Dockerized Setup

Tested all `.http` request files (`GET`, `POST`, `PUT`, `DELETE`) against the Dockerized application connected to PostgreSQL. All endpoints worked as expected.

![img.png](assets/imgK.png)

---

## API Endpoints

```
GET    /medical-profiles          # Fetch all profiles
POST   /medical-profiles          # Create new profile
PUT    /medical-profiles/{id}     # Update profile by ID
DELETE /medical-profiles/{id}     # Delete profile by ID
```

---
![img.png](assets/img.png)



### Example Response


```json
[
  {
    "id": "e4b5f...",
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Health St",
    "dateOfBirth": "1990-05-01"
  },
  "..."
]
```

---

## OpenAPI Documentation

- Local API Docs: [http://localhost:8081/v3/api-docs](http://localhost:8081/v3/api-docs)

![img.png](assets/imgB.png)

- Swagger UI (auto-generated): [http://localhost:8081/swagger-ui/index.html](http://localhost:8081/swagger-ui/index.html)

![img.png](assets/imgA.png)

These are generated using annotations like `@Tag`, `@Operation`, etc., in controller classes.

You can copy the raw OpenAPI JSON from `/v3/api-docs` and paste it into [Swagger Editor](https://editor.swagger.io/) for interactive documentation.

---

## API Testing with HTTP Files

This project uses `.http` files located under `api-request/medical-profile-service/` to test API endpoints.

Examples:

- `get-medical-profile.http` – Tests `GET /medical-profiles`
![img.png](assets/imgE.png)

- `create-medical-profile.http` – Tests `POST /medical-profiles`
![img.png](assets/imgD.png)

- `update-medical-profile.http` – Tests `PUT /medical-profiles/{id}`
![img.png](assets/imgF.png)

- `delete-medical-profile.http` – Tests `DELETE /medical-profiles/{id}`
![img.png](assets/imgC.png)

You can use IntelliJ IDEA or VS Code REST Client extension to run these files.

---

## Global Error Handling

A centralized exception handling mechanism is in place using `@ControllerAdvice`. It catches and formats errors like:

- Duplicate email constraint violations
- Entity not found
- Invalid request body

![img.png](assets/imgG.png)

This ensures consistent error responses across the API.

---

## Development Notes / Change Log

- Added DTOs for request and response
- Developed create, update, get, delete logic in service and controller
- Implemented grouped validation
- Added custom repository methods to prevent duplicate emails
- Introduced global error handler to handle exceptions gracefully
- Verified all endpoints using `.http` request files
- Integrated SpringDoc for OpenAPI documentation
- Dockerized the application with a multi-stage Dockerfile
- Created and configured PostgreSQL container with internal Docker networking
- Connected Dockerized Spring Boot app to PostgreSQL using environment variables
- Verified DB connection via IntelliJ and tested all API endpoints in the Dockerized setup

---

