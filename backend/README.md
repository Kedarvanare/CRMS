# =======================================================
# CRMS - Crime Report Management System (Spring Boot Backend)
# =======================================================

## Quick Start Guide

### Prerequisites
- Java 17+ (installed on your system)
- MySQL Server running on `localhost:3306` (database `crms` with user `root` and empty password)

### How to Run the Spring Boot Backend

#### Option 1: Using the provided Maven Wrapper (Recommended)
Open a terminal in the `backend` directory:
```bash
cd backend
./mvnw.cmd spring-boot:run
```
*(On Linux/macOS, use `./mvnw spring-boot:run`)*

#### Option 2: Using the bundled Apache Maven
```bash
cd backend
./apache-maven-3.9.6/bin/mvn.cmd spring-boot:run
```

#### Option 3: Package into executable JAR and run
```bash
cd backend
./mvnw.cmd clean package -DskipTests
java -jar target/backend-2.0.0.jar
```

---

## Ports & Configuration
Configured in `backend/src/main/resources/application.properties`:
- **Current Port**: `8080` (can be changed to `3000` whenever you stop the Node.js server)
- **Frontend URL**: `http://localhost:8080/`
- **Database**: MySQL `crms` database (auto-created on start)

---

## REST Endpoints & Security
All `/api/**` endpoints (except `/api/login` and `/api/login-history` audit logging) are secured with **JWT (JSON Web Token)** authentication. Send the token in the `Authorization` HTTP header:
```http
Authorization: Bearer <your_jwt_token>
```

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/login` | Public | Authenticate Admin or Officer & receive JWT token |
| `GET` | `/api/cases` | Authenticated | List all cases sorted by created_at DESC |
| `POST` | `/api/cases` | Authenticated | Register a new case/FIR |
| `PUT` | `/api/cases/{id}/status` | Authenticated | Update case status (Pending / Solved) |
| `DELETE` | `/api/cases/{id}` | Authenticated | Delete case |
| `GET` | `/api/criminals` | Authenticated | List all criminals sorted by created_at DESC |
| `POST` | `/api/criminals` | Authenticated | Add new criminal record |
| `DELETE` | `/api/criminals/{id}` | Authenticated | Delete criminal record |
| `GET` | `/api/officers` | Authenticated | List all officers sorted by created_at DESC |
| `POST` | `/api/officers` | Authenticated | Add new officer |
| `PUT` | `/api/officers/{id}/status` | Authenticated | Update officer status (Active / Inactive) |
| `GET` | `/api/login-history` | Authenticated | Get recent 50 login attempts |
| `POST` | `/api/login-history` | Public | Log login audit entry |
| `GET` | `/*` | Public | Single-page application serving `index.html` |

---

## Default Credentials
- **Admin**: `admin@crms.gov` / `admin123`
- **Officer**: `raj.sharma@crms.gov` / `officer123`
