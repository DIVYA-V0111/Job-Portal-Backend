# Job-Portal-Application

A Spring Boot REST API for a Job Portal application.

## Tech Stack
- Java 17
- Spring Boot 3
- Spring Security + JWT
- MySQL
- Maven

## Features
- JWT Authentication
- Role based access (CANDIDATE / RECRUITER)
- Job CRUD
- Application management
- Resume upload
- Email notifications
- OTP based forgot password
- Recommended jobs algorithm

## Setup

1. Clone the repository
2. Copy application.properties.example to application.properties
3. Fill in your database and email credentials
4. Run the application

## API Endpoints

### Applications
- POST /api/applications/apply/{jobId}
- GET  /api/applications/my-applications
- GET  /api/applications/job/{jobId}
- PUT  /api/applications/{id}/status
- GET  /api/applications/job/{id}
- DEL  /api/applications/{applicationId}

### Auth
- POST /api/auth/register
- POST /api/auth/login

### Jobs
- POST /api/jobs
- GET  /api/jobs
- GET  /api/jobs/{id}
- PUT  /api/jobs/{id}
- DEL  /api/jobs/{id}
- GET  /api/jobs/search
- GET  /api/jobs/my-jobs

### Otp
- POST /api/auth/forgot-password
- POST /api/auth/verify-otp
- POST /api/auth/reset-password

###RecommendedJobs
- GET  /api/recommended-jobs

### Resume
- POST /api/resume/upload
- GET  /api/resume/download
- DEL  /api/resume/delete

###SavedJobs
- POST  /api/saved-jobs/{jobId}
- DEL 	/api/saved-jobs/{jobId}
- GET 	/api/saved-jobs

###User
- POST  /api/users
- GET	/api/users
- GET   /api/users/{id}
- PUT	/api/users/profile
- GET 	/api/users/profile
- PUT 	/api/users/change-password