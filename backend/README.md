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

## REST Endpoints Implemented

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/login` | Authenticate Admin or Officer |
| `GET` | `/api/cases` | List all cases sorted by created_at DESC |
| `POST` | `/api/cases` | Register a new case/FIR |
| `PUT` | `/api/cases/{id}/status` | Update case status (Pending / Solved) |
| `DELETE` | `/api/cases/{id}` | Delete case |
| `GET` | `/api/criminals` | List all criminals sorted by created_at DESC |
| `POST` | `/api/criminals` | Add new criminal record |
| `DELETE` | `/api/criminals/{id}` | Delete criminal record |
| `GET` | `/api/officers` | List all officers sorted by created_at DESC |
| `POST` | `/api/officers` | Add new officer |
| `PUT` | `/api/officers/{id}/status` | Update officer status (Active / Inactive) |
| `GET` | `/api/login-history` | Get recent 50 login attempts |
| `POST` | `/api/login-history` | Log login audit entry |
| `GET` | `/*` | Single-page application serving `index.html` |

---

## Default Credentials
- **Admin**: `admin@crms.gov` / `admin123`
- **Officer**: `raj.sharma@crms.gov` / `officer123`
