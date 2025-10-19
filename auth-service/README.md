# JWT Authentication Microservice

A production-ready, secure JWT-based authentication microservice built with Spring Boot 3.3+, Java 21, and MySQL.

## 🚀 Features

- **User Registration & Authentication**
- **JWT Access & Refresh Tokens** with configurable expiration
- **Role-Based Access Control (RBAC)** - USER and ADMIN roles
- **Stateless Session Management**
- **BCrypt Password Hashing**
- **Comprehensive Exception Handling**
- **Input Validation**
- **Clean Architecture** with clear separation of concerns
- **Spring Security 6.2+** integration
- **JJWT 0.12.5** for modern JWT handling

## 📋 Prerequisites

- Java 21 (LTS)
- Maven 3.8+
- MySQL 8.0+
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🛠️ Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd jwt-auth-service
```

### 2. Configure MySQL Database

Create a MySQL database:

```sql
CREATE DATABASE auth_db;
```

Update `src/main/resources/application.properties` with your database credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Generate JWT Secret Key

Generate a secure secret key using OpenSSL:

```bash
openssl rand -base64 64
```

Update `application.properties`:

```properties
jwt.secret=YOUR_GENERATED_SECRET_KEY_HERE
```

**Example output:**
```
VGhpc0lzQVZlcnlTZWN1cmVTZWNyZXRLZXlGb3JKV1RUb2tlbkdlbmVyYXRpb25BbmRWYWxpZGF0aW9uUHVycG9zZXM=
```

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

You should see:
```
Started JwtAuthServiceApplication in X.XXX seconds
```

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

---

## 🔓 Public Endpoints (No Authentication Required)

### 1. Register New User

**Endpoint:** `POST /api/auth/register`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Response (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzA...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

**Validation Rules:**
- Username: 3-50 characters, required, unique
- Email: Valid email format, max 100 characters, required, unique
- Password: 6-100 characters, required

**Error Response (400 Bad Request):**
```json
{
  "username": "Username must be between 3 and 50 characters",
  "email": "Email should be valid",
  "password": "Password is required"
}
```

---

### 2. Login

**Endpoint:** `POST /api/auth/login`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzA...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login",
  "timestamp": 1634567890123
}
```

---

### 3. Refresh Access Token

**Endpoint:** `POST /api/auth/refresh`

**Request:**
```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
  }'
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzA...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 900,
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid refresh token",
  "path": "/api/auth/refresh",
  "timestamp": 1634567890123
}
```

---

## 🔐 Protected Endpoints (Authentication Required)

### 4. Get Current User Profile

**Endpoint:** `GET /api/user/me`

**Authorization:** Bearer Token (USER or ADMIN)

**Request:**
```bash
curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huZG9lIiwiaWF0IjoxNzA..."
```

**Response (200 OK):**
```json
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "USER"
}
```

**Error Response (401 Unauthorized - No Token):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/user/me",
  "timestamp": 1634567890123
}
```

**Error Response (401 Unauthorized - Invalid/Expired Token):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token has expired",
  "path": "/api/user/me",
  "timestamp": 1634567890123
}
```

---

### 5. Admin Test Endpoint

**Endpoint:** `GET /api/admin/test`

**Authorization:** Bearer Token (ADMIN only)

**Request:**
```bash
curl -X GET http://localhost:8080/api/admin/test \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbnVzZXIiLCJpYXQiOjE3MA..."
```

**Response (200 OK):**
```json
{
  "message": "Admin access granted! This endpoint is protected."
}
```

**Error Response (403 Forbidden - User without ADMIN role):**
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to access this resource",
  "path": "/api/admin/test",
  "timestamp": 1634567890123
}
```

---

## 🔒 Security Configuration

### Token Expiration Times

Default configuration in `application.properties`:

```properties
# Access Token: 15 minutes (900,000 milliseconds)
jwt.access-token.expiration=900000

# Refresh Token: 7 days (604,800,000 milliseconds)
jwt.refresh-token.expiration=604800000
```

### Password Security

- Passwords are hashed using **BCrypt** with default strength (10 rounds)
- Never stored in plain text
- Salted automatically by BCrypt

### Session Management

- **Stateless** - No server-side session storage
- JWT tokens contain all necessary authentication info
- Each request is independently authenticated

### CORS Configuration

By default, CORS is not enabled. To enable for frontend applications, add to `SecurityConfiguration.java`:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:4200"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    configuration.setMaxAge(3600L);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## 🗂️ Project Structure

```
src/main/java/com/example/auth/
├── config/
│   ├── JwtAuthenticationFilter.java       # JWT token validation filter
│   ├── SecurityConfiguration.java         # Spring Security configuration
│   └── GlobalExceptionHandler.java        # Global exception handling
├── controller/
│   ├── AuthController.java                # Authentication endpoints
│   ├── UserController.java                # User endpoints
│   └── AdminController.java               # Admin endpoints
├── dto/
│   ├── RegisterRequest.java               # Registration request DTO
│   ├── LoginRequest.java                  # Login request DTO
│   ├── RefreshTokenRequest.java           # Refresh token request DTO
│   ├── AuthResponse.java                  # Authentication response DTO
│   ├── UserDto.java                       # User data DTO
│   ├── MessageResponse.java               # Generic message response
│   └── ErrorResponse.java                 # Error response DTO
├── entity/
│   ├── User.java                          # User entity (implements UserDetails)
│   ├── Role.java                          # Role enum (USER, ADMIN)
│   └── RefreshToken.java                  # Refresh token entity
├── repository/
│   ├── UserRepository.java                # User data access
│   └── RefreshTokenRepository.java        # Refresh token data access
├── service/
│   ├── AuthService.java                   # Authentication business logic
│   ├── UserService.java                   # User management service
│   ├── JwtService.java                    # JWT token operations
│   └── RefreshTokenService.java           # Refresh token management
└── JwtAuthServiceApplication.java         # Main Spring Boot application

src/main/resources/
└── application.properties                 # Application configuration
```

---

## 📊 Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);
```

### Refresh Tokens Table
```sql
CREATE TABLE refresh_tokens (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    token VARCHAR(255) UNIQUE NOT NULL,
    user_id BIGINT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id)
);
```

---

## 🧪 Complete Testing Guide

### Step 1: Register a New User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "password123"
  }'
```

**Save the `accessToken` and `refreshToken` from the response.**

---

### Step 2: Test User Endpoint

```bash
# Replace YOUR_ACCESS_TOKEN with the token from Step 1
curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected:** 200 OK with user profile

---

### Step 3: Test Admin Endpoint (Should Fail)

```bash
curl -X GET http://localhost:8080/api/admin/test \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected:** 403 Forbidden (user doesn't have ADMIN role)

---

### Step 4: Create Admin User

Connect to MySQL and promote user to admin:

```sql
USE auth_db;
UPDATE users SET role = 'ADMIN' WHERE username = 'testuser';
```

---

### Step 5: Login Again as Admin

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'
```

**Save the new `accessToken`** (it will now have ADMIN role)

---

### Step 6: Test Admin Endpoint (Should Succeed)

```bash
curl -X GET http://localhost:8080/api/admin/test \
  -H "Authorization: Bearer NEW_ADMIN_ACCESS_TOKEN"
```

**Expected:** 200 OK with admin success message

---

### Step 7: Test Refresh Token

```bash
curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "YOUR_REFRESH_TOKEN"
  }'
```

**Expected:** 200 OK with new access token

---

### Step 8: Test Invalid Token

```bash
curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer invalid_token_here"
```

**Expected:** 401 Unauthorized

---

### Step 9: Test No Token

```bash
curl -X GET http://localhost:8080/api/user/me
```

**Expected:** 401 Unauthorized

---

## 🔧 Configuration Options

### Adjust Token Expiration

In `application.properties`:

```properties
# 1 hour access token (3600000 milliseconds)
jwt.access-token.expiration=3600000

# 30 days refresh token (2592000000 milliseconds)
jwt.refresh-token.expiration=2592000000
```

### Change BCrypt Strength

In `SecurityConfiguration.java`:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12); // Increase from default 10 to 12
}
```

### Enable SQL Logging

In `application.properties`:

```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

### Change Server Port

In `application.properties`:

```properties
server.port=9090
```

---

## 🚨 Error Response Reference

### 400 Bad Request - Validation Error
```json
{
  "username": "Username is required",
  "email": "Email should be valid",
  "password": "Password must be between 6 and 100 characters"
}
```

### 401 Unauthorized - Invalid Credentials
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login",
  "timestamp": 1634567890123
}
```

### 401 Unauthorized - Expired Token
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "JWT token has expired",
  "path": "/api/user/me",
  "timestamp": 1634567890123
}
```

### 403 Forbidden - Insufficient Permissions
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to access this resource",
  "path": "/api/admin/test",
  "timestamp": 1634567890123
}
```

### 404 Not Found - User Not Found
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found: invaliduser",
  "path": "/api/user/me",
  "timestamp": 1634567890123
}
```

### 500 Internal Server Error
```json
{
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/auth/login",
  "timestamp": 1634567890123
}
```

---

## 🎯 Best Practices Implemented

1. ✅ **Never commit secrets** - JWT secret should be in environment variables
2. ✅ **Password security** - BCrypt with salt
3. ✅ **Stateless authentication** - No server-side session
4. ✅ **Token expiration** - Short-lived access tokens
5. ✅ **Refresh tokens** - Long-lived for better UX
6. ✅ **Role-based authorization** - @PreAuthorize annotations
7. ✅ **Input validation** - Jakarta validation annotations
8. ✅ **Exception handling** - Global exception handler
9. ✅ **Clean architecture** - Separation of concerns
10. ✅ **Security headers** - Handled by Spring Security

---

## 🔐 Production Deployment Checklist

### Security
- [ ] Generate a strong JWT secret (minimum 256 bits)
- [ ] Use environment variables for secrets (never commit to Git)
- [ ] Enable HTTPS (TLS/SSL certificates)
- [ ] Implement rate limiting for auth endpoints
- [ ] Add CORS configuration for your frontend domain
- [ ] Enable security headers (HSTS, X-Frame-Options, etc.)

### Database
- [ ] Use strong database passwords
- [ ] Create separate database user with limited permissions
- [ ] Enable database SSL connections
- [ ] Set up database backups
- [ ] Configure connection pooling

### Application
- [ ] Change `spring.jpa.hibernate.ddl-auto` to `validate` or `none`
- [ ] Disable SQL logging in production
- [ ] Set appropriate logging levels
- [ ] Configure application monitoring
- [ ] Set up health check endpoints

### Performance
- [ ] Enable database indexing
- [ ] Configure connection pool size
- [ ] Set appropriate JVM memory settings
- [ ] Enable caching where appropriate

---

## 📝 Environment Variables Setup

Create a `.env` file (never commit this):

```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/auth_db
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT Configuration
JWT_SECRET=your_generated_base64_secret_key
JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=604800000

# Server Configuration
SERVER_PORT=8080
```

Update `application.properties` to use environment variables:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}
jwt.access-token.expiration=${JWT_ACCESS_EXPIRATION}
jwt.refresh-token.expiration=${JWT_REFRESH_EXPIRATION}

server.port=${SERVER_PORT}
```

---

## 📖 Additional Resources

- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/index.html)
- [JWT.io - Introduction to JWT](https://jwt.io/introduction)
- [JJWT Library Documentation](https://github.com/jwtk/jjwt)
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [BCrypt Explained](https://en.wikipedia.org/wiki/Bcrypt)

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👨‍💻 Support

For issues, questions, or contributions:
- Open an issue on GitHub
- Check existing issues before creating new ones
- Provide detailed information about your environment

---

## 🙏 Acknowledgments

- Spring Framework Team
- JJWT Library Contributors
- Spring Security Team
- The Java Community

---

**Built with ❤️ using Spring Boot 3.3+, Java 21, and Modern Security Practices**

---

## 📞 Quick Reference

| Endpoint | Method | Auth Required | Role Required |
|----------|--------|---------------|---------------|
| `/api/auth/register` | POST | No | - |
| `/api/auth/login` | POST | No | - |
| `/api/auth/refresh` | POST | No | - |
| `/api/user/me` | GET | Yes | USER, ADMIN |
| `/api/admin/test` | GET | Yes | ADMIN |

**Default Token Lifetimes:**
- Access Token: 15 minutes
- Refresh Token: 7 days

**Default Port:** 8080

**Database:** MySQL 8.0+

**Java Version:** 21 (LTS)