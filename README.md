# Student Complaint and Feedback Management System

<a id="readme-top"></a>

<div align="center">

English | [简体中文](README.zh-CN.md)

A full-stack web application for campus complaints and feedback · OOAD course project

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Vue](https://img.shields.io/badge/Vue-3.5-4FC08D?style=flat-square&logo=vuedotjs&logoColor=white)
![Vite](https://img.shields.io/badge/Vite-5.4-646CFF?style=flat-square&logo=vite&logoColor=white)
[![Stars](https://img.shields.io/github/stars/xuzihao723/StudentComplaintAndManagementSystem?style=flat-square)](https://github.com/xuzihao723/StudentComplaintAndManagementSystem/stargazers)

[Live Demo](https://student-complaint-frontend.onrender.com/) · [Demo Guide](docs/SYSTEM_DEMONSTRATION.md) · [Project Report](docs/project-documentation/Student_Complaint_and_Feedback_Management_System_Project_Report.pdf) · [Report an Issue](https://github.com/xuzihao723/StudentComplaintAndManagementSystem/issues)

</div>

## Table of Contents

- [About the Project](#about)
- [Features and Roles](#features)
- [Live Demo](#demo)
- [Technology Stack](#stack)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Project Structure and Architecture](#structure)
- [Main API Endpoints](#api)
- [Testing and Building](#testing)
- [Cloud Deployment](#deployment)
- [Documentation](#documentation)
- [Limitations and Improvement Opportunities](#limitations)
- [Contributing](#contributing)
- [License](#license)
- [Maintainer and Acknowledgments](#acknowledgments)

<a id="about"></a>

## About the Project

This project provides a shared platform for students, student affairs officers, department staff, and administrators to manage campus complaints and feedback. It connects submission, assignment, progress updates, resolution, and closure in a traceable workflow. Anonymous users can submit complaints in categories that permit anonymous reporting and use a tracking code to check progress.

The application uses a separate Vue frontend and Spring Boot backend, with a relational database, attachment storage, notifications, and audit records. Developed as an Object-Oriented Analysis and Design (OOAD) course project, the repository also includes a project report, UML and database diagrams, and a classroom demonstration guide.

<a id="features"></a>

## Features and Roles

| User | Role identifier | Main capabilities |
| --- | --- | --- |
| Student | `STUDENT` | Register and log in, submit complaints, upload evidence, view cases and messages, provide follow-up feedback and satisfaction ratings |
| Anonymous user | No login required | Submit complaints in eligible categories, track cases, and add messages using a tracking code |
| Student Affairs Officer | `OFFICER` | Review new cases, request additional information, assign departments, close cases, and view weekly reports |
| Department Staff | `DEPARTMENT_STAFF` | View cases assigned to their department, update progress, reply, and mark cases as resolved |
| Administrator | `ADMIN` | Manage users, departments, and categories; generate and export reports; configure email; and inspect audit logs |

Additional capabilities include:

- **Authentication and authorization:** JWT sessions, BCrypt password hashing, and access control based on roles and case ownership.
- **Case collaboration:** Case numbers, status history, messages, internal notes, reminders, and overdue monitoring.
- **Attachments:** PNG, JPEG, GIF, PDF, DOC, and DOCX support, with a 10 MB limit per file and a 30 MB limit per upload request.
- **Notifications and reports:** In-app notifications, optional SMTP / Resend email delivery, and weekly report generation and export.
- **Runtime options:** A file-based H2 database for quick local demonstrations and MySQL for a separate database service.

<a id="demo"></a>

## Live Demo

**Open the application: [student-complaint-frontend.onrender.com](https://student-complaint-frontend.onrender.com/)**

### Demo Accounts

The backend creates the following initial accounts on startup. Existing accounts with the same username are retained. Accounts and data in the online environment may change during demonstrations.

| Role | Username | Initial password |
| --- | --- | --- |
| Administrator | `admin` | `Admin123!` |
| Student Affairs Officer | `officer` | `Officer123!` |
| Campus Facilities Staff | `facility_staff` | `Staff123!` |
| Academic Affairs Staff | `academic_staff` | `Staff123!` |
| Student | `student1` | `Student123!` |

These accounts are intended for course demonstrations. Change the initial passwords and default JWT secret before a public deployment.

### Suggested Demonstration Flow

1. Log in as `student1`, submit a complaint, and inspect its case number, status, and messages.
2. Log out, submit an anonymous complaint in an eligible category, save the tracking code, and use it to track the case.
3. Log in as `officer` and assign the case to the appropriate department.
4. Log in as department staff, update progress, and mark the case as resolved.
5. Return to the officer account to close the case, then demonstrate the administrator's user management, reports, and audit logs.

See the [System Demonstration Guide](docs/SYSTEM_DEMONSTRATION.md) for detailed steps.

<a id="stack"></a>

## Technology Stack

| Layer | Technologies | Purpose |
| --- | --- | --- |
| Frontend | Vue 3.5, Element Plus 2.8, Vite 5.4 | Role-specific interfaces, forms, and frontend builds |
| HTTP client | Axios | API requests and JWT headers |
| Backend | Java 17, Spring Boot 3.3.5 | REST APIs and business logic |
| Security | Spring Security, JWT, BCrypt | Authentication, authorization, and password storage |
| Data access | Spring Data JPA, Hibernate | Entity mapping and persistence |
| Database | H2 / MySQL 8.4 (Docker Compose) | Local demonstrations / separate database service |
| Email | Spring Mail, Resend HTTP API | Optional email notifications |
| Testing | JUnit, Spring Boot Test, Vitest | Backend logic and frontend module tests |

Versions are based on [backend/pom.xml](backend/pom.xml) and [frontend/package.json](frontend/package.json). Resolved frontend dependency versions are locked in `package-lock.json`.

<a id="getting-started"></a>

## Getting Started

### Prerequisites

- Git
- JDK 17 or later, with `JAVA_HOME` configured
- Maven 3.9.x
- Node.js 20 or later and npm
- Docker and Docker Compose (only required for MySQL mode)

### 1. Clone the Repository

```bash
git clone https://github.com/xuzihao723/StudentComplaintAndManagementSystem.git
cd StudentComplaintAndManagementSystem
```

### 2. Start the Backend with H2

Open a terminal in the repository root:

```bash
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=dev"
```

This command also works in Windows PowerShell. The `dev` profile uses a file-based H2 database stored in `backend/data/`, so no separate database installation is required.

### 3. Start the Frontend

Open another terminal in the repository root:

```bash
cd frontend
npm ci
npm run dev
```

| Entry point | Address / setting |
| --- | --- |
| Frontend application | [http://localhost:5173](http://localhost:5173) |
| Backend API base URL | `http://localhost:8080/api` |
| H2 console (`dev` profile only) | [http://localhost:8080/h2-console](http://localhost:8080/h2-console) |
| H2 JDBC URL | `jdbc:h2:file:./data/scfs-dev;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE` |
| H2 username / password | `sa` / leave the password blank |

The frontend development server proxies `/api` requests to `http://127.0.0.1:8080` by default. Use the demo accounts above to explore the workflow.

### 4. Optional: Use MySQL

From the repository root, run:

```bash
docker compose up -d mysql
```

Wait for MySQL to finish initializing, then start the backend **without the `dev` profile**:

```bash
cd backend
mvn spring-boot:run
```

Compose creates the `scfs` database and `scfs` user with the local demo password `scfs_password`, and exposes port `3306`. These values match the default backend configuration. Start the frontend as described above. The Docker named volume `scfs_mysql_data` stores database data.

<a id="configuration"></a>

## Configuration

Backend settings are defined in [application.yml](backend/src/main/resources/application.yml) and [application-dev.yml](backend/src/main/resources/application-dev.yml).

| Environment variable | Default | Description |
| --- | --- | --- |
| `PORT` | `8080` | Backend listening port |
| `DB_URL` | Local MySQL `scfs` database | JDBC connection URL; the `dev` profile uses its own H2 URL |
| `DB_USERNAME` / `DB_PASSWORD` | `scfs` / `scfs_password` | MySQL credentials; the `dev` profile uses H2 credentials |
| `JWT_SECRET` | Built-in demo secret | Replace with a random secret for public deployments |
| `JWT_EXPIRATION_MINUTES` | `480` | Login token lifetime in minutes |
| `UPLOAD_DIR` | `uploads` | Backend attachment directory; the `dev` profile uses `uploads` directly |
| `FRONTEND_URL` | `http://localhost:5173` | Frontend URL used by features such as email links |
| `CORS_ALLOWED_ORIGIN_PATTERNS` | `http://localhost:*,http://127.0.0.1:*` | Allowed cross-origin patterns, separated by commas |
| `VITE_API_PROXY` | `http://127.0.0.1:8080` | Frontend development proxy target for `/api` |
| `VITE_API_BASE_URL` | `/api` | Frontend API base URL; include `/api` when using a separately deployed backend |

`VITE_API_BASE_URL` is read during the frontend build. Rebuild the frontend after changing it, and restart the backend after changing backend environment variables.

### Optional Email Notifications

In-app notifications and email delivery are recorded separately. Without a configured email service, in-app notifications are still saved and the email status records the delivery failure.

Configure either provider:

- **Resend:** Set `EMAIL_API_PROVIDER=resend`, `RESEND_API_KEY`, and `RESEND_FROM_ADDRESS`.
- **SMTP:** Set `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, and `MAIL_PASSWORD`; review the sender address, authentication, and TLS settings in the administrator's email settings page.

After configuration, log in as an administrator, enable email delivery in the settings page, and use the test feature to verify delivery. The email service selects Resend first when it is configured.

PowerShell example (run in the same terminal used to start the backend):

```powershell
$env:EMAIL_API_PROVIDER = "resend"
$env:RESEND_API_KEY = "your-resend-api-key"
$env:RESEND_FROM_ADDRESS = "Complaint System <no-reply@example.com>"
```

Bash example:

```bash
export EMAIL_API_PROVIDER="resend"
export RESEND_API_KEY="your-resend-api-key"
export RESEND_FROM_ADDRESS="Complaint System <no-reply@example.com>"
```

<a id="structure"></a>

## Project Structure and Architecture

```text
StudentComplaintAndManagementSystem/
├── backend/
│   ├── src/main/java/edu/demo/scfs/
│   │   ├── config/          # Initial data and configuration
│   │   ├── domain/          # Domain entities and enums
│   │   ├── repository/      # JPA data access
│   │   ├── security/        # JWT and access control
│   │   ├── service/         # Case, notification, and report logic
│   │   └── web/             # REST controllers and DTOs
│   ├── src/main/resources/  # MySQL / H2 configuration
│   ├── src/test/            # Backend tests
│   ├── Dockerfile
│   └── pom.xml
├── frontend/
│   ├── src/                # Vue UI, API client, and module tests
│   ├── public/             # Static assets
│   ├── package.json
│   └── package-lock.json
├── docs/
│   ├── SYSTEM_DEMONSTRATION.md
│   └── project-documentation/
│       ├── figures/        # Architecture, UML, ER, and workflow diagrams
│       └── ...             # Project report and LaTeX source
├── docker-compose.yml      # Local MySQL service
├── DEPLOYMENT_RENDER_AIVEN.md
├── PRODUCT.md
├── DESIGN.md
├── README.md               # English repository homepage
└── README.zh-CN.md         # Chinese documentation
```

The frontend API client calls Spring Boot controllers. Business services persist data through JPA and coordinate attachment storage, notifications, and audit records.

<details>
<summary>View the system architecture diagram from the project report</summary>

![System architecture: role-based UI, API, security, business services, and data storage](docs/project-documentation/figures/figure1_system_architecture.png)

University SSO is shown as an optional design extension. The current implementation authenticates local accounts.

</details>

<a id="api"></a>

## Main API Endpoints

The table below summarizes common endpoints. See the [backend controllers and DTOs](backend/src/main/java/edu/demo/scfs/web) for complete routes and request fields. Protected endpoints require `Authorization: Bearer <token>`. Case submission with evidence uses `multipart/form-data`.

| Area | Method and path | Purpose |
| --- | --- | --- |
| Authentication | `POST /api/auth/register/student`, `POST /api/auth/login`, `GET /api/auth/me` | Student registration, login, and current user details |
| Reference data | `GET /api/reference/departments`, `GET /api/reference/categories` | List departments and categories |
| Anonymous cases | `POST /api/public/cases`, `POST /api/public/cases/track`, `POST /api/public/cases/track/messages` | Submit, track, and add messages |
| Student cases | `POST /api/student/cases`, `GET /api/student/cases` | Submit and list the student's cases |
| Student feedback | `POST /api/student/cases/{id}/messages`, `POST /api/student/cases/{id}/follow-up` | Send messages and follow-up feedback |
| Officer processing | `GET /api/officer/cases/new`, `POST /api/officer/cases/{id}/assign` | Review new cases and assign departments |
| Information and closure | `POST /api/officer/cases/{id}/request-info`, `POST /api/officer/cases/{id}/close` | Request more information and close cases |
| Department processing | `GET /api/department/cases`, `POST /api/department/cases/{id}/progress`, `POST /api/department/cases/{id}/resolve` | View assignments, update progress, and mark resolution |
| User management | `GET /api/admin/users`, `POST /api/admin/users` | List and create users |
| Weekly reports | `GET /api/admin/reports/weekly`, `POST /api/admin/reports/weekly/generate` | View and generate reports |
| Notifications and audit | `GET /api/notifications`, `GET /api/admin/audit-logs` | Current user's notifications and administrator audit records |

<a id="testing"></a>

## Testing and Building

Run backend tests from the repository root:

```bash
mvn -f backend/pom.xml test
```

Run frontend tests and build from the repository root:

```bash
npm --prefix frontend ci
npm --prefix frontend test
npm --prefix frontend run build
```

Backend tests cover case numbers, tracking codes, category policies, SLA policies, notification flows, email settings, and profile logic. Frontend tests cover the API client and presentation modules. The frontend build output is stored in `frontend/dist/`.

To create an executable backend JAR:

```bash
mvn -f backend/pom.xml package
```

The output is stored in `backend/target/`. The existing unit tests should be supplemented with browser workflow checks using the demonstration guide.

<a id="deployment"></a>

## Cloud Deployment

See the [Render + Aiven deployment guide](DEPLOYMENT_RENDER_AIVEN.md):

| Component | Deployment method | Key settings |
| --- | --- | --- |
| Backend | Render Web Service with Docker | Root directory `backend`; use the existing `Dockerfile` and configure the database, JWT, and CORS |
| Frontend | Render Static Site | Root directory `frontend`; build command `npm ci && npm run build`; publish directory `dist` |
| Database | Aiven MySQL | Configure the connection URL and credentials in the backend environment |

Set `VITE_API_BASE_URL` to the actual backend address during the frontend build, for example `https://your-backend.onrender.com/api`. Set the backend's `FRONTEND_URL` and allowed CORS origins to match the actual frontend domain.

The guide includes a free-tier demonstration setup. Check the providers' current plans, quotas, and inactivity policies when deploying. Attachments are stored in the container's local directory, so configure persistent storage for deployments. The online application runs on these services, while GitHub hosts the repository and documentation.

<a id="documentation"></a>

## Documentation

| Document | Contents |
| --- | --- |
| [System Demonstration Guide](docs/SYSTEM_DEMONSTRATION.md) | Demo accounts, workflows, and course presentation requirements |
| [Project Report PDF](docs/project-documentation/Student_Complaint_and_Feedback_Management_System_Project_Report.pdf) | Requirements analysis, object-oriented design, and system description |
| [Project Report LaTeX Source](docs/project-documentation/Student_Complaint_and_Feedback_Management_System_Project_Report.tex) | Editable report source |
| [Project Title and Scope Statement](Project_Title_and_Scope_Statement.pdf) | Project topic and scope |
| [Product Specification](PRODUCT.md) | Feature scope and product requirements |
| [Design Specification](DESIGN.md) | Technical design and implementation conventions |
| [Design Diagrams](docs/project-documentation/figures) | Architecture, use-case, class, activity, sequence, ER, and role navigation diagrams |
| [Cloud Deployment Guide](DEPLOYMENT_RENDER_AIVEN.md) | Render frontend/backend and Aiven MySQL deployment steps |

<a id="limitations"></a>

## Limitations and Improvement Opportunities

- **Project scope:** The current version is intended for course demonstrations and prototype validation. Public operation requires configuration and operational practices suited to the deployment environment.
- **Identity integration:** University SSO remains a design extension and has not been integrated.
- **Attachment storage:** Files are stored locally on the backend and may be lost when containers are recreated. Persistent volumes or object storage are possible improvements.
- **Email delivery:** Delivery depends on valid service credentials and administrator settings. Check the email status in notification records for delivery results.
- **Attachment scanning:** File storage currently validates size and MIME type. A real antivirus scanning service has not been integrated.

<a id="contributing"></a>

## Contributing

Use [Issues](https://github.com/xuzihao723/StudentComplaintAndManagementSystem/issues) to report problems or suggest improvements. Include your environment, reproduction steps, expected behavior, and actual results in a bug report.

Suggested contribution workflow:

1. Fork the repository and create a feature branch.
2. Make your changes and update the relevant documentation.
3. Run tests relevant to your changes; also check the build for frontend changes.
4. Open a pull request describing the changes and validation results.

Do not commit real database passwords, email credentials, JWT secrets, or personal complaint data. Keep both README language versions aligned when updating shared documentation.

<a id="license"></a>

## License

The repository does not currently include a `LICENSE` file. Licensing terms are awaiting clarification from the maintainer.

<a id="acknowledgments"></a>

## Maintainer and Acknowledgments

Maintainer: [xuzihao723](https://github.com/xuzihao723) · Repository: [StudentComplaintAndManagementSystem](https://github.com/xuzihao723/StudentComplaintAndManagementSystem)

The README structure and presentation are inspired by:

- [Awesome README](https://github.com/matiassingers/awesome-readme): Clear introductions, technology badges, navigation, and visual documentation.
- [Best README Template](https://github.com/othneildrew/Best-README-Template): Sections for project descriptions, installation, usage, contributions, licensing, and acknowledgments.

Thanks to the open-source projects behind Vue, Element Plus, Spring Boot, and the other dependencies.

[Back to Top](#readme-top)
