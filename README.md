# 🎓 Smart Job and Placement Management System

A full-stack **Java Spring Boot** application that streamlines college placement activities — managing companies, job drives, student applications, and interview scheduling with **JWT-based role authentication**.

![Status](https://img.shields.io/badge/status-complete-brightgreen)
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

---

## 📌 Overview

The **Smart Job and Placement Management System** is a comprehensive web application that digitizes the campus placement process. It provides two role-based interfaces:

- **👨‍💼 Placement Admin** — Manages companies, creates job drives, reviews applications, updates statuses, and schedules interviews
- **🎓 Student** — Maintains a profile, browses eligible jobs (auto-filtered by CGPA), applies with one click, and tracks application status in real-time

This project demonstrates production-grade patterns: **layered architecture**, **JWT authentication**, **DTO-based API design**, **global exception handling**, **input validation**, and **responsive frontend** built with vanilla HTML/CSS/JS.

---

## ✨ Features

### Authentication & Security
- 🔐 JWT-based stateless authentication
- 🛡️ Role-based access control (ADMIN / STUDENT)
- 🔑 BCrypt password hashing
- ⚙️ Spring Security filter chain
- 📝 Global exception handling with structured error responses
- ✅ Server-side validation using Bean Validation (Jakarta)

### Admin (Placement Officer)
- ➕ Create, update, delete companies
- 💼 Create, update, delete job drives with eligibility criteria
- 📊 View all applications with filters (status, job, student search)
- 🔄 Update application status: `APPLIED → SHORTLISTED → INTERVIEW_SCHEDULED → SELECTED / REJECTED`
- 📅 Schedule, edit, and cancel interviews
- 📈 Dashboard with live stats and quick actions

### Student
- 👤 Create and edit profile (department, CGPA, graduation year, skills)
- 🔍 Browse jobs automatically filtered by CGPA eligibility
- ⚡ One-click apply with duplicate prevention
- 📄 Track application status with visual progress timeline
- 📅 View interview details (date, time, mode, location)

### Developer Experience
- 🧪 Postman collection included (`docs/postman/`)
- 🏗️ Clean layered architecture
- 🧱 DTOs prevent entity leakage
- 🎯 Meaningful exception messages
- 📖 Beginner-friendly code organization

---

## 🛠️ Technology Stack

### Backend
| Technology | Purpose |
|------------|---------|
| **Java 17** | Language |
| **Spring Boot 3.2** | Application framework |
| **Spring Data JPA** | ORM / data access |
| **Hibernate** | JPA implementation |
| **Spring Security** | Auth & authorization |
| **JWT (jjwt)** | Stateless tokens |
| **Hibernate Validator** | Bean validation |
| **Maven** | Build tool |

### Database
| Technology | Purpose |
|------------|---------|
| **MySQL 8.0** | Relational database |

### Frontend
| Technology | Purpose |
|------------|---------|
| **HTML5** | Structure |
| **CSS3** | Styling (custom, no framework) |
| **JavaScript (ES6+)** | Interactivity & API calls |
| **Fetch API** | REST communication |

### Tools
- IntelliJ IDEA
- MySQL Workbench
- Postman
- Git & GitHub

---

## 🏗️ Project Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                       CLIENT (Browser)                      │
│          HTML + CSS + JavaScript (fetch API)                │
└──────────────────────────┬──────────────────────────────────┘
                           │ HTTP (JSON + JWT)
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                  SPRING BOOT APPLICATION                    │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │               Controllers (REST API)                  │ │
│  │  AuthController, StudentController, AdminController,  │ │
│  │  CompanyController, JobDriveController,               │ │
│  │  ApplicationController, InterviewController           │ │
│  └───────────────────────┬───────────────────────────────┘ │
│                          ▼                                  │
│  ┌───────────────────────────────────────────────────────┐ │
│  │                 Service Layer                         │ │
│  │  Business logic + @Transactional boundaries           │ │
│  └───────────────────────┬───────────────────────────────┘ │
│                          ▼                                  │
│  ┌───────────────────────────────────────────────────────┐ │
│  │              Repository Layer (JPA)                   │ │
│  │  Spring Data JPA interfaces                           │ │
│  └───────────────────────┬───────────────────────────────┘ │
│                          ▼                                  │
│  ┌───────────────────────────────────────────────────────┐ │
│  │                  Entity Layer                         │ │
│  │  User, Student, Company, JobDrive,                    │ │
│  │  Application, Interview                               │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │                Security Layer                         │ │
│  │  JwtAuthenticationFilter, JwtTokenProvider,           │ │
│  │  CustomUserDetailsService, SecurityConfig             │ │
│  └───────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
                           │
                           ▼
                 ┌──────────────────┐
                 │    MySQL 8.0     │
                 └──────────────────┘
```

### Layer Responsibilities

| Layer | Role |
|-------|------|
| **Controller** | Receives HTTP requests, validates input, returns responses |
| **Service** | Business logic, transaction boundaries, entity → DTO conversion |
| **Repository** | Database access via Spring Data JPA |
| **Entity** | JPA-mapped database tables |
| **DTO** | Decouples API contracts from entities |
| **Security** | JWT filter chain, authentication, role checks |
| **Exception** | Centralized error handling |

---

## 🗄️ Database Design

### Entity Relationships

```
User (1) ── (1) Student
                     │
                     │ (1)
                     ▼
                Application (*) ──── (1) JobDrive (*) ──── (1) Company
                     │
                     │ (1)
                     ▼
                 Interview (1)
```

### Schema Overview

| Table | Key Columns |
|-------|-------------|
| `users` | id, name, email (unique), password (BCrypt), role |
| `students` | id, user_id (FK), department, cgpa, graduation_year, skills |
| `companies` | id, company_name, location, website, description |
| `job_drives` | id, company_id (FK), job_role, package_amount, required_cgpa, required_skills, deadline, location |
| `applications` | id, student_id (FK), job_drive_id (FK), status, applied_date, unique(student_id, job_drive_id) |
| `interviews` | id, application_id (FK, unique), interview_date, interview_time, mode, location |

**Cascade rules:** Deleting a company cascades to its job drives → applications → interviews.

**Auditing:** All entities auto-populate `created_at` and `updated_at` via `@EnableJpaAuditing`.

---

## 🔌 API Endpoints

### Authentication (`/api/auth`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/register` | Register user | Public |
| POST | `/login` | Login & get JWT | Public |

### Student (`/api/students`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/profile` | Get own profile | STUDENT |
| PUT | `/profile` | Update own profile | STUDENT |
| GET | `/jobs/eligible` | Eligible jobs (filtered by CGPA) | STUDENT |
| POST | `/jobs/{jobId}/apply` | Apply for job | STUDENT |
| GET | `/applications` | My applications | STUDENT |
| GET | `/applications/{id}/status` | Status of specific application | STUDENT |

### Admin (`/api/admin`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/applications` | All applications | ADMIN |
| PUT | `/applications/{id}/status` | Update application status | ADMIN |

### Companies (`/api/companies`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/` | List all | Public |
| GET | `/{id}` | Get one | Public |
| POST | `/` | Create | ADMIN |
| PUT | `/{id}` | Update | ADMIN |
| DELETE | `/{id}` | Delete | ADMIN |

### Job Drives (`/api/jobs`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/` | All jobs | Public |
| GET | `/active` | Active jobs (deadline future) | Public |
| GET | `/{id}` | Get one | Public |
| POST | `/` | Create | ADMIN |
| PUT | `/{id}` | Update | ADMIN |
| DELETE | `/{id}` | Delete | ADMIN |

### Interviews (`/api/interviews`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Schedule | ADMIN |
| PUT | `/{id}` | Edit | ADMIN |
| DELETE | `/{id}` | Cancel (reverts status) | ADMIN |
| GET | `/student/{studentId}` | Student's interviews | Any |

**Example response (200):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "email": "admin@placement.com",
    "name": "Admin User",
    "role": "ADMIN"
  }
}
```

**Example error (400):**
```json
{
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input data",
  "details": ["email: Invalid email format"],
  "path": "uri=/api/auth/register"
}
```

---

## 🚀 Installation & Setup

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Any IDE (IntelliJ recommended)

### 1. Clone the Repository
```bash
git clone https://github.com/YOUR_USERNAME/smart-job-placement-system.git
cd smart-job-placement-system
```

### 2. Create the Database
```sql
CREATE DATABASE placement_db;
```

### 3. Configure `application.properties`

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/placement_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

jwt.secret=YOUR_BASE64_SECRET_AT_LEAST_32_CHARS
jwt.expiration=86400000
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

Or run `SmartJobPlacementSystemApplication.java` in your IDE.

### 5. Access the Application
- **Frontend:** http://localhost:8080/login.html
- **API Root:** http://localhost:8080/api
- **Health Check:** http://localhost:8080/api/test/hello

### 6. Default Users (Register First)

Register at `/register.html`:
| Role | Suggested Email | Suggested Password |
|------|-----------------|-------------------|
| Admin | admin@placement.com | admin123 |
| Student | john@college.edu | student123 |

Then log in at `/login.html`.

---

## 🧪 Testing with Postman

The full Postman collection is included at:
```
docs/postman/SmartPlacement.postman_collection.json
```

**How to use:**
1. Open Postman → **Import** → choose the file above
2. Click **Run** on the collection
3. Tokens are automatically saved between requests
4. All 18 requests should pass ✅

For manual testing checklist, see `docs/postman/TESTING_CHECKLIST.md`.

---
## 📁 Project Structure

```
smart-job-placement-system/
├── src/
│   ├── main/
│   │   ├── java/com/placementsystem/
│   │   │   ├── SmartJobPlacementSystemApplication.java
│   │   │   ├── controller/         # REST endpoints
│   │   │   ├── service/            # Business logic
│   │   │   │   └── impl/
│   │   │   ├── repository/         # Spring Data JPA
│   │   │   ├── entity/             # JPA entities
│   │   │   ├── dto/
│   │   │   │   ├── request/
│   │   │   │   └── response/
│   │   │   ├── security/           # JWT + config
│   │   │   ├── exception/          # Global handlers
│   │   │   ├── config/             # Auditing, CORS
│   │   │   └── util/               # Enums, helpers
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/             # Frontend
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── css/
│   │           ├── js/
│   │           ├── student/
│   │           └── admin/
│   └── test/
├── docs/
│   ├── postman/
│   └── screenshots/
├── pom.xml
└── README.md
```

---

## 🔮 Future Enhancements

- 📧 Email notifications for status changes and interview schedules
- 📎 Resume upload & download (S3/Cloud Storage integration)
- 🗓️ Calendar view for scheduled interviews
- 📊 Analytics dashboard with charts (student placement rate, top recruiters)
- 🔔 Real-time notifications using WebSockets
- 📱 Mobile app (React Native / Flutter)
- 🌍 Multi-college support
- 🧪 Unit + integration tests with JUnit + Mockito
- 🐳 Docker Compose for one-command setup
- ☁️ Cloud deployment (AWS / Azure / Render)

---

## 🤝 Contributing

This is a learning/portfolio project, but feedback is welcome!

1. Fork the repo
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the **MIT License** — see [LICENSE](LICENSE) for details.

---

## 🙏 Acknowledgments

- Spring Boot documentation
- Baeldung for JWT + Spring Security tutorials
- The open-source community

---

⭐ **If this project helped you, please give it a star!** ⭐