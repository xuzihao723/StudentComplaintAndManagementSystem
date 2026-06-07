# System Demonstration Guide

## Purpose

This repository contains a working web-application prototype for the **Student Complaint and Feedback Management System**. The demonstration reflects the analysis and design documented in the project report, including the use-case model, class model, layered architecture, activity flow, sequence flow, and ER model.

The demo is intended for the **System Demonstration** part of the semester project. It focuses on functionality, completeness, alignment with documented requirements, user interface usability, demonstration quality, and technical effort.

## Live Demo URL

Open the deployed frontend:

```text
https://student-complaint-frontend.onrender.com/
```

This URL should be used as the main classroom demonstration entry. The local run instructions below are kept as a backup in case the cloud deployment is slow to wake up or unavailable during presentation.

## Prototype Form

- **Demo type:** Website / Web Application
- **Frontend:** Vue 3, Element Plus, Vite
- **Backend:** Spring Boot 3, Spring Security, Spring Data JPA
- **Database:** H2 for quick local demonstration; MySQL supported for deployment mode
- **Authentication:** JWT, BCrypt password hashing, role-based access control
- **Supporting services:** Local file upload storage, notification records, optional SMTP / Resend email delivery

## Demo Accounts

The backend seeds these accounts on first run:

| Role | Username | Password | Demonstration Purpose |
| --- | --- | --- | --- |
| Admin | `admin` | `Admin123!` | Manage users, departments, categories, reports, settings, and audit logs |
| Student Affairs Officer | `officer` | `Officer123!` | Review cases, classify them, assign departments, and close cases |
| Department Staff | `facility_staff` | `Staff123!` | Process facility-related assigned cases |
| Department Staff | `academic_staff` | `Staff123!` | Process academic-related assigned cases |
| Student | `student1` | `Student123!` | Submit and track complaints as an authenticated user |

## Quick Start for Local Demonstration

### 1. Run Backend with H2

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

## Recommended 5-Minute Demonstration Flow

### Flow 1: Student Complaint Submission

1. Log in as `student1`.
2. Open the student dashboard.
3. Submit a complaint with category, title, description, priority, and optional evidence attachment.
4. Show that the system creates a case number.
5. Open the case detail page and show the status and message area.

This demonstrates:

- authenticated user workflow
- complaint submission
- evidence upload support
- case number generation
- student case tracking

### Flow 2: Anonymous Complaint and Tracking

1. Open the public / anonymous submission page.
2. Submit an anonymous complaint under a category that allows anonymous reports.
3. Show the generated case number and one-time tracking code.
4. Use the tracking code to look up the case status.

This demonstrates:

- public access without login
- anonymous reporting policy
- tracking-code workflow
- privacy-aware complaint submission

### Flow 3: Officer Assignment and Department Resolution

1. Log in as `officer`.
2. Open new or pending cases.
3. Review the complaint details.
4. Assign the case to a department.
5. Log in as department staff.
6. Update progress, provide a response, and mark the case as resolved.
7. Return to the officer view and close the case if appropriate.

This demonstrates:

- role-based access control
- officer review and assignment
- department staff workflow
- status transition
- traceable workflow history

### Optional Admin Preview

If time remains, log in as `admin` and briefly show:

- user management
- department and category management
- weekly reports
- system settings
- audit logs

This demonstrates:

- administration support
- reporting and monitoring
- auditability

## Alignment with Project Documentation

| Documented Design Artifact | Demonstration Evidence |
| --- | --- |
| Use-case diagram | Five actors are represented in the prototype: Student, Anonymous User, Student Affairs Officer, Department Staff, and Admin |
| UML class diagram | Complaint cases are central and connect to users, departments, categories, attachments, messages, assignments, and logs |
| Activity diagram | Submission, review, assignment, department update, resolution, and closure are implemented as workflow steps |
| Sequence diagram | Frontend submission calls backend controllers, services, storage, database, and notification logic |
| ER diagram | Case-centered relational data is implemented through JPA entities and repositories |
| Architecture diagram | Vue frontend communicates with Spring Boot backend and H2/MySQL persistence |

## Demonstration Scoring Coverage

### Functionality and Completeness

The prototype includes:

- login and JWT session handling
- role-based dashboards
- student complaint submission
- anonymous complaint submission and tracking
- file evidence upload
- officer review and department assignment
- department progress updates and resolution
- student follow-up review support
- notifications
- weekly reports
- admin management and audit logs

### Alignment with Documented Requirements

The demonstration follows the same actors, workflows, and data structures described in the project report. The live system shows the operational version of the requirements, use cases, class model, architecture, and database design.

### User Interface and Usability

The frontend uses role-specific pages so each user type sees the workflows relevant to their responsibility. Forms, queues, case detail views, and management pages are designed for repeated case handling.

### Demonstration Quality

The recommended demo sequence focuses on three clear workflows:

1. student submission
2. anonymous tracking
3. officer assignment and department resolution

This keeps the presentation concise while still showing the main value of the system.

### Technical Effort and Creativity

The project demonstrates a full-stack implementation with authentication, role-based authorization, workflow logic, persistence, notifications, reporting, audit logging, file handling, and OOAD-driven modeling artifacts.

## Project Deliverables in This Repository

- Live web application: `https://student-complaint-frontend.onrender.com/`
- Project report PDF: `docs/project-documentation/Student_Complaint_and_Feedback_Management_System_Project_Report.pdf`
- Project report LaTeX source: `docs/project-documentation/Student_Complaint_and_Feedback_Management_System_Project_Report.tex`
- Backend prototype: `backend/`
- Frontend prototype: `frontend/`

## Notes and Limitations

- The system is a course-project prototype, not a production deployment guarantee.
- H2 mode is recommended for quick local demonstration.
- MySQL mode is available through Docker Compose for a more deployment-like setup.
- University SSO is modeled as an optional future integration; local prototype accounts are used for the demo.
- Email delivery can be configured through SMTP or Resend. If email is not configured, notification records still demonstrate the workflow.
