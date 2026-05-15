# Student Complaint and Feedback Management System

Full-stack course project based on the SRS: Spring Boot 3 backend, Vue 3 frontend, MySQL database, local file uploads, role-based access control, case workflow, weekly reports, and SMTP email notifications.

## Current Scope

Implemented:

- Spring Boot project structure
- Vue 3 project structure
- MySQL Docker configuration
- Local database accounts
- Student self-registration
- JWT login
- BCrypt password storage
- Four roles: `STUDENT`, `OFFICER`, `DEPARTMENT_STAFF`, `ADMIN`
- Admin staff account management
- Seeded departments and complaint categories
- Role-based frontend dashboards
- Complaint and feedback submission
- Anonymous category policy
- File evidence upload to local storage
- Case assignment and department processing
- Student follow-up review
- Email notification workflow
- Weekly reports

## Demo Accounts

The backend seeds these accounts on first run:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `Admin123!` |
| Student Affairs Officer | `officer` | `Officer123!` |
| Department Staff | `facility_staff` | `Staff123!` |
| Department Staff | `academic_staff` | `Staff123!` |
| Student | `student1` | `Student123!` |

## Run MySQL

```bash
docker compose up -d mysql
```

## Run Backend

For quick local demo without MySQL, use the H2 dev profile:

```bash
cd backend
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The H2 database is stored under `backend/data/`, and the H2 console is available at `http://localhost:8080/h2-console`.

For MySQL mode, start MySQL first and run without the dev profile. SMTP variables can be set when real email sending is required:

```bash
set MAIL_HOST=smtp.example.com
set MAIL_PORT=587
set MAIL_USERNAME=your@email.com
set MAIL_PASSWORD=your-password
```

Then run:

```bash
cd backend
mvn spring-boot:run
```

The API listens on `http://localhost:8080`.

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

The web app listens on `http://localhost:5173`.

## API Summary

- `POST /api/auth/register/student`
- `POST /api/auth/login`
- `GET /api/auth/me`
- `GET /api/reference/departments`
- `GET /api/reference/categories`
- `GET /api/notifications`
- `POST /api/student/cases`
- `GET /api/student/cases`
- `GET /api/student/cases/{id}`
- `POST /api/student/cases/{id}/messages`
- `POST /api/student/cases/{id}/follow-up`
- `GET /api/officer/cases/new`
- `GET /api/officer/cases/{id}`
- `POST /api/officer/cases/{id}/assign`
- `POST /api/officer/cases/{id}/request-info`
- `POST /api/officer/cases/{id}/close`
- `GET /api/department/cases`
- `GET /api/department/cases/{id}`
- `POST /api/department/cases/{id}/progress`
- `POST /api/department/cases/{id}/resolve`
- `GET /api/admin/users`
- `POST /api/admin/users`
- `PUT /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`
- `POST /api/admin/reference/departments`
- `PUT /api/admin/reference/departments/{id}`
- `POST /api/admin/reference/categories`
- `PUT /api/admin/reference/categories/{id}`
- `GET /api/admin/reports/weekly`
- `POST /api/admin/reports/weekly/generate`

## Notes

- Student self-registration is implemented with local database accounts because university SSO is unavailable.
- Staff and admin accounts are created by administrators.
- Evidence files are stored under `uploads/`.
- When SMTP is not configured, notification records are still created and email status becomes `FAILED`, which keeps the demo workflow usable.
- This is a complete course-project prototype, not a production deployment guarantee for 1,000 concurrent users.
