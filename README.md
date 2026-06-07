# Student Complaint and Feedback Management System

Full-stack OOAD course-project prototype for managing student complaints and feedback. The system provides structured complaint submission, anonymous tracking, role-based case processing, department resolution, reports, and audit records.

## Live Demo

Open the deployed web application:

<https://student-complaint-frontend.onrender.com/>

This is the main demonstration entry for the project. The repository focuses on the implemented system itself: backend API, frontend application, database workflow, authentication, role-based pages, and the demonstration guide.

## Project Demonstration

This repository is prepared for the **System Demonstration** requirement of the semester project. The working prototype is a **Website / Web Application** and reflects the analysis and design documented in the project report.

Detailed demonstration guide:

- [System Demonstration Guide](docs/SYSTEM_DEMONSTRATION.md)

Recommended 5-minute demo flow:

1. Student submits a complaint.
2. Anonymous user submits and tracks a complaint.
3. Student Affairs Officer assigns the case.
4. Department Staff updates progress and marks resolution.
5. Admin pages are briefly shown for management, reports, settings, and audit logs.

## Deliverables

| Deliverable | Location |
| --- | --- |
| Live web application | `https://student-complaint-frontend.onrender.com/` |
| Project report PDF | `docs/project-documentation/Student_Complaint_and_Feedback_Management_System_Project_Report.pdf` |
| Project report LaTeX source | `docs/project-documentation/Student_Complaint_and_Feedback_Management_System_Project_Report.tex` |
| System demonstration guide | `docs/SYSTEM_DEMONSTRATION.md` |
| Backend prototype | `backend/` |
| Frontend prototype | `frontend/` |

## Technology Stack

- **Frontend:** Vue 3, Element Plus, Vite
- **Backend:** Spring Boot 3, Spring Security, Spring Data JPA
- **Database:** H2 for quick local demo, MySQL for deployment-like mode
- **Authentication:** JWT, BCrypt, role-based access control
- **Supporting features:** local file upload, notification records, optional SMTP / Resend email delivery

## Implemented Scope

- Student self-registration and login
- JWT authentication
- BCrypt password storage
- Role-based access control
- Roles: `STUDENT`, `OFFICER`, `DEPARTMENT_STAFF`, `ADMIN`
- Role-based frontend dashboards
- Complaint and feedback submission
- Anonymous complaint policy and tracking-code workflow
- File evidence upload to local storage
- Case review, assignment, department progress update, resolution, and closure
- Student follow-up review
- Notification workflow
- Weekly reports
- Admin user, department, category, settings, and audit-log management
- H2 local demo profile and MySQL deployment mode

## Demo Accounts

The backend seeds these accounts on first run:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `Admin123!` |
| Student Affairs Officer | `officer` | `Officer123!` |
| Department Staff | `facility_staff` | `Staff123!` |
| Department Staff | `academic_staff` | `Staff123!` |
| Student | `student1` | `Student123!` |

## Quick Start: H2 Local Demo

Use this mode for the fastest classroom demonstration.

### 1. Run Backend

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend API:

```text
http://localhost:8080
```

H2 console:

```text
http://localhost:8080/h2-console
```

### 2. Run Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend application:

```text
http://localhost:5173
```

## MySQL Mode

For a deployment-like local run:

```bash
docker compose up -d mysql
cd backend
mvn spring-boot:run
```

The API listens on:

```text
http://localhost:8080
```

Optional email variables:

```bash
set MAIL_HOST=smtp.example.com
set MAIL_PORT=587
set MAIL_USERNAME=your@email.com
set MAIL_PASSWORD=your-password
```

Resend API variables can also be configured for HTTPS-based email delivery:

```bash
set RESEND_API_KEY=your-resend-key
set RESEND_FROM_ADDRESS=no-reply@example.com
```

## Main API Areas

- `POST /api/auth/register/student`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/reference/departments`
- `GET /api/reference/categories`
- `POST /api/student/cases`
- `GET /api/student/cases`
- `POST /api/student/cases/{id}/messages`
- `POST /api/student/cases/{id}/follow-up`
- `GET /api/officer/cases/new`
- `POST /api/officer/cases/{id}/assign`
- `POST /api/officer/cases/{id}/request-info`
- `POST /api/officer/cases/{id}/close`
- `GET /api/department/cases`
- `POST /api/department/cases/{id}/progress`
- `POST /api/department/cases/{id}/resolve`
- `GET /api/admin/users`
- `POST /api/admin/users`
- `GET /api/admin/reports/weekly`
- `POST /api/admin/reports/weekly/generate`

## Testing

Backend:

```bash
cd backend
mvn test
```

Frontend:

```bash
cd frontend
npm test
```

## Notes and Limitations

- This is a complete course-project prototype, not a production deployment guarantee.
- H2 mode is recommended for quick local demonstration.
- MySQL mode is available through Docker Compose.
- University SSO is planned as a future integration. The prototype uses local accounts for demonstration.
- Evidence files are stored locally under `uploads/`.
- If SMTP or Resend is not configured, notification records are still created so the workflow remains demonstrable.

## Free Cloud Demo Deployment

For a one-month public demo, use Render Free for frontend/backend and Aiven Free MySQL for the database. See [DEPLOYMENT_RENDER_AIVEN.md](DEPLOYMENT_RENDER_AIVEN.md).
