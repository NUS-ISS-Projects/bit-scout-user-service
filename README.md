# bit-scout-user-service


This project provides REST API endpoints for user account management, including user registration, login, user details retrieval, and updating user details.

## API Endpoints

### Register a new user

**URL**: `/account/register`

**Method**: `POST`

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "John Doe",
  "avatar": "http://example.com/avatar.jpg",
  "introduction": "Hello, I am John."
}



Login
URL: /account/login

Method: POST

Request Body:

json
Copy code
{
  "email": "user@example.com",
  "password": "password123"
}
Responses:

200 OK: Returns the authentication token.
401 UNAUTHORIZED: Invalid email or password.