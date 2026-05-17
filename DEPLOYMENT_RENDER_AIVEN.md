# Render Free Backend + Aiven Free MySQL Deployment

This guide deploys the course prototype for about one month with free tiers:

- Frontend: Render Static Site
- Backend: Render Free Web Service
- Database: Aiven MySQL Free Tier

The backend sleeps on Render Free after inactivity. The first request after sleep can take about a minute.

## 1. Create Aiven MySQL

1. Open Aiven and create a free MySQL service.
2. Choose the free tier and wait until the service is running.
3. In the Aiven connection page, copy:
   - Host
   - Port
   - User, usually `avnadmin`
   - Password
   - Database, usually `defaultdb`
4. Build the Spring JDBC URL:

```text
jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED&serverTimezone=UTC
```

Keep this value private.

## 2. Deploy Backend on Render

1. Open Render.
2. Create a new Web Service from this GitHub repository.
3. Select the `backend` directory as the root directory.
4. Use Docker as the runtime. Render will use `backend/Dockerfile`.
5. Select the Free plan.
6. Add these environment variables:

```text
DB_URL=jdbc:mysql://HOST:PORT/defaultdb?sslMode=REQUIRED&serverTimezone=UTC
DB_USERNAME=avnadmin
DB_PASSWORD=your-aiven-password
JWT_SECRET=replace-with-a-long-random-string
CORS_ALLOWED_ORIGIN_PATTERNS=https://*.onrender.com,http://localhost:*,http://127.0.0.1:*
FRONTEND_URL=https://your-frontend-name.onrender.com
UPLOAD_DIR=/app/uploads
```

Optional SMTP variables:

```text
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-gmail@gmail.com
MAIL_PASSWORD=your-gmail-app-password
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS=true
```

7. Deploy the backend.
8. After deployment, copy the backend URL, for example:

```text
https://student-complaint-backend.onrender.com
```

## 3. Deploy Frontend on Render

1. Create a new Static Site from the same GitHub repository.
2. Set root directory:

```text
frontend
```

3. Set build command:

```text
npm ci && npm run build
```

4. Set publish directory:

```text
dist
```

5. Add this environment variable, using your real backend URL:

```text
VITE_API_BASE_URL=https://student-complaint-backend.onrender.com/api
```

6. Deploy the frontend.

## 4. Test After Deployment

Open the frontend URL and use the seeded demo accounts:

| Role | Username | Password |
| --- | --- | --- |
| Admin | `admin` | `Admin123!` |
| Officer | `officer` | `Officer123!` |
| Department Staff | `facility_staff` | `Staff123!` |
| Department Staff | `academic_staff` | `Staff123!` |
| Student | `student1` | `Student123!` |

Manual checks:

1. Log in as `admin`.
2. Open Settings and confirm departments/categories are loaded.
3. Log in as `student1`.
4. Submit a case.
5. Log in as `officer`.
6. Confirm the new case and notification are visible.

## 5. Important Limits

- Render Free services sleep after inactivity.
- Uploaded files are stored in the container path `/app/uploads`; they are not durable on free Render services.
- Aiven Free MySQL storage is limited, so keep this for demo/course use.
- Do not commit real database passwords, SMTP passwords, or JWT secrets to GitHub.
