---
theme: touying
title: Student Complaint and Feedback Management System
author: Zile Ye, Zixuan Feng, Zihao Xu, Yansong Wen
aspectRatio: 16/9
canvasWidth: 1200
transition: slide-left
lineNumbers: false
drawings:
  persist: false
fonts:
  sans: Arial
  serif: Times New Roman
  mono: Fira Code
touying:
  preset: university
---

# Student Complaint and Feedback Management System

<div class="subtitle">OOAD Project Presentation</div>

<div class="cover-meta">
  <div><b>Course</b><span>CPS 3962</span></div>
  <div><b>Instructor</b><span>Waqar Ali</span></div>
  <div><b>University</b><span>Wenzhou-Kean University</span></div>
  <div><b>Department</b><span>Computer Science and Technology</span></div>
</div>

<div class="team-line">
  Zile Ye · Zixuan Feng · Zihao Xu · Yansong Wen
</div>

<div class="tech-line">
  Vue 3 · Spring Boot · H2 / MySQL · OOAD Modeling
</div>

<div class="speaker">Presenter: Zile Ye</div>

<!--
Ye:
Good morning everyone. Our project is the Student Complaint and Feedback Management System, a full-stack course-project prototype for CPS 3962.
Today we will briefly introduce the problem, requirements analysis, OOAD design, architecture, prototype demonstration, testing results, and conclusion.
-->

---

## 1. Problem and Objectives

<div class="focus-grid two">
  <div>
    <span class="tag">Problem</span>
    <h3>Complaint handling is hard to trace</h3>
    <ul>
      <li>Scattered channels: email, paper forms, messages</li>
      <li>Unclear case status and department responsibility</li>
      <li>Limited evidence history, audit records, and reports</li>
    </ul>
  </div>
  <div>
    <span class="tag">Aim</span>
    <h3>Build a structured workflow</h3>
    <ul>
      <li>Submit, review, assign, update, resolve, and close cases</li>
      <li>Support authenticated prototype users and anonymous users</li>
      <li>Provide tracking, reports, and audit records</li>
    </ul>
  </div>
</div>

<div class="bottom-message">Goal: make student complaints traceable, role-based, and easier to manage.</div>

<div class="speaker">Presenter: Zile Ye</div>

<!--
Ye:
The problem we want to solve is that student complaints are often handled through emails, paper forms, or informal messages.
In that situation, case status, evidence, department responsibility, and final resolution can become difficult to track.
Our aim is to provide one structured platform where complaints can be submitted, reviewed, assigned, updated, and closed with proper records.
The system also supports anonymous complaints, because some students may prefer to report sensitive issues without revealing their identity.
The main objectives are role-based access, complaint submission, anonymous tracking, officer review, department processing, admin management, reports, and audit records.
-->

---

## 2. Requirements and Use Cases

<div class="actor-grid">
  <div><b>Student</b><span>Submit and track complaints</span></div>
  <div><b>Anonymous User</b><span>Submit anonymously and track with code</span></div>
  <div><b>Student Affairs Officer</b><span>Review, classify, assign, close</span></div>
  <div><b>Department Staff</b><span>Update progress and resolve cases</span></div>
  <div><b>Admin</b><span>Manage users, reports, settings, audit logs</span></div>
</div>

<div class="focus-grid three">
  <div>
    <span class="tag">Functional</span>
    <p>Submission, tracking, review, assignment, response, closure, reports.</p>
  </div>
  <div>
    <span class="tag">Security</span>
    <p>JWT login, role-based access, anonymous tracking code.</p>
  </div>
  <div>
    <span class="tag">Quality</span>
    <p>Usable interface, maintainable layers, prototype-level performance.</p>
  </div>
</div>

<div class="speaker">Presenter: Zixuan Feng</div>

<!--
Feng:
In requirements analysis, we identified five actors: Student, Anonymous User, Student Affairs Officer, Department Staff, and Admin.
Students can log in, submit complaints, upload evidence, track cases, provide additional information, and request follow-up review.
Anonymous users can submit complaints and track progress using a case number and tracking code.
Student Affairs Officers review, classify, request more information, assign departments, and close cases.
Department Staff update assigned cases and provide responses.
Admins manage users, departments, categories, reports, settings, and audit logs.
The use-case model shows that every actor has a clear responsibility, and it separates user goals from internal system actions such as validation, case number generation, and tracking code generation.
-->

---

## 3. OOAD Design and Architecture

<div class="focus-grid two">
  <div>
    <span class="tag">OOAD Model</span>
    <h3>ComplaintCase is the core object</h3>
    <ul>
      <li>User, Category, and Department define identity and responsibility</li>
      <li>Attachments, messages, status logs, and assignments belong to cases</li>
      <li>Notification and AuditLog support traceability</li>
    </ul>
  </div>
  <div>
    <span class="tag">Layered Design</span>
    <h3>Frontend → Backend → Data</h3>
    <ul>
      <li>Vue frontend provides role/public pages and case views</li>
      <li>Spring Boot handles security, controllers, services, repositories</li>
      <li>H2/MySQL stores cases, users, workflow records, reports, settings</li>
    </ul>
  </div>
</div>

<div class="bottom-message">Design idea: one central case object moves through a layered technical workflow.</div>

<div class="speaker">Presenter: Zihao Xu</div>

<!--
Xu:
For OOAD design, ComplaintCase is the central class.
Every complaint becomes a case with status, priority, category, assignment, messages, attachments, status logs, and resolution information.
In the class diagram, User represents identity and role information. Category controls classification and anonymous policy. Department represents responsibility after assignment.
Attachments, messages, status logs, and assignments are owned by ComplaintCase, so they are modeled as case-related records.
The architecture follows a layered design. Users interact with the Vue frontend. The frontend sends REST requests to the Spring Boot backend.
The backend uses Spring Security, controllers, services, repositories, and audit logic.
Repositories connect to H2 for development and MySQL for deployment. The ER diagram follows the same idea, with cases as the central table.
-->

---

## 4. Prototype Demonstration

<div class="demo-grid">
  <div>
    <span>1</span>
    <h3>Student Submission</h3>
    <p>Create a complaint with category, title, description, priority, and optional attachment.</p>
  </div>
  <div>
    <span>2</span>
    <h3>Anonymous Tracking</h3>
    <p>Generate a tracking code and use it later to check case progress.</p>
  </div>
  <div>
    <span>3</span>
    <h3>Staff Workflow</h3>
    <p>Officer assigns the case; Department Staff update progress and mark resolution.</p>
  </div>
</div>

<div class="bottom-message">Admin pages are shown briefly for users, departments, categories, reports, settings, and audit logs.</div>

<div class="speaker">Presenter: Yansong Wen</div>

<!--
Wen:
Now we will demonstrate the main workflow.
First, we show a student submitting a complaint with category, title, description, priority, and optional attachment. The system creates a case number.
Second, we show anonymous submission. For anonymous cases, the system generates a tracking code and displays it once. This allows the anonymous user to check progress later.
Third, we show the staff workflow. The Student Affairs Officer reviews the case and assigns it to a department. Then Department Staff update progress, provide a response, and mark the case as resolved.
Finally, the admin side provides management functions such as users, departments, categories, reports, settings, and audit logs.
-->

---

## 5. Testing, Outcome, and Future Work

<div class="focus-grid two">
  <div>
    <span class="tag">Evaluation</span>
    <div class="score-line"><b>12 / 12</b><span>Backend tests passed</span></div>
    <div class="score-line"><b>20 / 20</b><span>Frontend tests passed</span></div>
    <ul>
      <li>Validated submission, tracking, workflow, reports, and role access</li>
      <li>Prototype aligns with requirements and OOAD diagrams</li>
    </ul>
  </div>
  <div>
    <span class="tag">Conclusion</span>
    <h3>Main objectives achieved</h3>
    <ul>
      <li>Structured complaint workflow</li>
      <li>Anonymous tracking and role-based processing</li>
      <li>Audit logs, reports, and management support</li>
    </ul>
  </div>
</div>

<div class="bottom-message">Future work: university SSO, object storage, stronger anonymous safeguards, analytics, and escalation rules. Thank you.</div>

<div class="speaker">Presenter: Yansong Wen</div>

<!--
Wen:
For testing, our backend tests passed 12 out of 12, and frontend tests passed 20 out of 20.
The prototype supports the main requirements, including complaint submission, anonymous tracking, officer workflow, department processing, admin management, reports, and audit logs.
In conclusion, this project achieves the objective as a course-project prototype.
Future improvements include university SSO integration, object storage for attachments, stronger safeguards for anonymous reporting, advanced analytics, and escalation rules.
Thank you. We are ready to answer questions.
-->
