# API Integration Guide

## Overview

The NoghreSod Android app communicates with a backend API using Retrofit and Kotlinx Serialization.

## Base URL Configuration

Edit `app/src/main/kotlin/com/noghre/sod/di/NetworkModule.kt`:

```kotlin
.baseUrl("https://your-api-domain.com/")
```

## Authentication

### Login Endpoint

**Endpoint**: `POST /api/v1/auth/login`

**Request**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response**:
```json
{
  "token": "jwt-token-here",
  "user": {
    "id": "user123",
    "name": "John Doe",
    "email": "user@example.com",
    "phone": "1234567890",
    "avatar": "https://...jpg"
  }
}
```

### Register Endpoint

**Endpoint**: `POST /api/v1/auth/register`

**Request**:
```json
{
  "email": "newuser@example.com",
  "password": "password123"
}
```

**Response**: Same as login

### Logout Endpoint

**Endpoint**: `POST /api/v1/auth/logout`

**Headers**: `Authorization: Bearer {token}`

## Products

### List Products

**Endpoint**: `GET /api/v1/products?page=1&limit=20&category=electronics`

**Response**:
```json
[
  {
    "id": "prod123",
    "title": "Product Name",
    "description": "Product description",
    "price": 99.99,
    "image": "https://...jpg",
    "category": "electronics",
    "rating": 4.5,
    "reviews": 128
  }
]
```

### Get Product Details

**Endpoint**: `GET /api/v1/products/{id}`

**Response**: Single product object (same structure as above)

### Search Products

**Endpoint**: `GET /api/v1/products/search?q=laptop`

**Response**: Array of matching products

## User Profile

### Get Profile

**Endpoint**: `GET /api/v1/users/profile`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
{
  "id": "user123",
  "name": "John Doe",
  "email": "user@example.com",
  "phone": "1234567890",
  "avatar": "https://...jpg"
}
```

### Update Profile

**Endpoint**: `PUT /api/v1/users/profile`

**Headers**: `Authorization: Bearer {token}`

**Request**: User object with updated fields

## Orders

### Get Orders

**Endpoint**: `GET /api/v1/orders`

**Headers**: `Authorization: Bearer {token}`

**Response**:
```json
[
  {
    "id": "order123",
    "userId": "user123",
    "items": [
      {
        "productId": "prod123",
        "quantity": 2,
        "price": 99.99
      }
    ],
    "total": 199.98,
    "status": "confirmed",
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

### Create Order

**Endpoint**: `POST /api/v1/orders`

**Headers**: `Authorization: Bearer {token}`

**Request**:
```json
{
  "items": [
    {
      "productId": "prod123",
      "quantity": 2,
      "price": 99.99
    }
  ],
  "shippingAddress": "123 Main St, City, State 12345"
}
```

### Get Order Details

**Endpoint**: `GET /api/v1/orders/{id}`

**Headers**: `Authorization: Bearer {token}`

**Response**: Single order object

## Error Handling

### Error Response Format

```json
{
  "error": "error_code",
  "message": "Human readable message",
  "details": {}
}
```

### Common Status Codes

- `200` - Success
- `201` - Created
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `500` - Server Error

## Headers

### Required Headers

```
Content-Type: application/json
Authorization: Bearer {jwt-token}
User-Agent: NoghreSod-Android/1.0.0
```

### Rate Limiting

The API implements rate limiting:
- 100 requests per minute for authenticated users
- 20 requests per minute for unauthenticated users

## Data Models

### Product
```kotlin
@Serializable
data class ProductDto(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val image: String,
    val category: String,
    val rating: Double,
    val reviews: Int
)
```

### User
```kotlin
@Serializable
data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val avatar: String?
)
```

### Order
```kotlin
@Serializable
data class OrderDto(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val total: Double,
    val status: String,
    val createdAt: String
)
```

## Implementation Guide

### Making API Calls

```kotlin
// In your ViewModel
viewModelScope.launch {
    val result = authRepository.login(email, password)
    when (result) {
        is Result.Success -> {
            // Handle success
        }
        is Result.Error -> {
            // Handle error
        }
    }
}
```

### Error Handling

```kotlin
// In Repository
suspend fun login(email: String, password: String): Result<AuthResponse> {
    return try {
        val response = apiService.login(AuthRequest(email, password))
        Result.Success(response)
    } catch (e: HttpException) {
        Result.Error("HTTP Error: ${e.code()}")
    } catch (e: IOException) {
        Result.Error("Network Error: ${e.message}")
    }
}
```

### Testing APIs

Use Postman or similar tool to test endpoints before implementation:

1. Set base URL
2. Add headers
3. Create request
4. Copy response
5. Update DTOs if needed

## Pagination

The API supports offset-based pagination:

```kotlin
val response = apiService.getProducts(
    page = 1,
    limit = 20,
    category = null
)
```

## Caching Strategy

- Products cached for 5 minutes
- User profile cached until logout
- Orders cached for 10 minutes
- Images cached indefinitely (until app clear)

## Future Enhancements

- [ ] GraphQL API support
- [ ] WebSocket for real-time updates
- [ ] JWT Token Refresh
- [ ] Offline Sync
- [ ] Request Retry Logic
- [ ] Rate Limiting Handling
