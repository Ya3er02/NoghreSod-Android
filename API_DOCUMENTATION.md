# 📄 **API Documentation**

**Status:** Complete API Integration Guide  
**Date:** December 28, 2025

---

## 🕐 **Base Configuration**

```kotlin
// Production
const val BASE_URL = "https://api.noghresod.com/v1/"

// Development
const val DEV_BASE_URL = "https://dev-api.noghresod.com/v1/"

// Timeout Configuration
const val CONNECT_TIMEOUT = 10L  // seconds
const val READ_TIMEOUT = 10L     // seconds
const val WRITE_TIMEOUT = 10L    // seconds
```

---

## 📄 **API Endpoints**

### Products

```
✅ GET     /products                    - Get all products (paginated)
✅ GET     /products/{id}               - Get product by ID
✅ GET     /products/search             - Search products
✅ POST    /products/{id}/favorite      - Add to favorites
✅ DELETE  /products/{id}/favorite      - Remove from favorites
```

### Cart

```
✅ GET     /cart                        - Get cart items
✅ POST    /cart/items                  - Add item to cart
✅ PUT     /cart/items/{id}             - Update cart item
✅ DELETE  /cart/items/{id}             - Remove from cart
✅ DELETE  /cart                        - Clear cart
```

### Orders

```
✅ GET     /orders                      - Get user's orders
✅ GET     /orders/{id}                 - Get order details
✅ POST    /orders                      - Create order
✅ PUT     /orders/{id}/cancel          - Cancel order
✅ PUT     /orders/{id}/status          - Update order status
```

### Auth

```
✅ POST    /auth/login                  - User login
✅ POST    /auth/register               - User registration
✅ POST    /auth/logout                 - User logout
✅ POST    /auth/refresh                - Refresh token
✅ GET     /auth/profile                - Get current user
```

### User

```
✅ GET     /user/profile                - Get profile
✅ PUT     /user/profile                - Update profile
✅ PUT     /user/password               - Change password
✅ DELETE  /user/account                - Delete account
```

### Categories

```
✅ GET     /categories                  - Get all categories
✅ GET     /categories/{id}             - Get category
✅ GET     /categories/{id}/products    - Get category products
```

### Search

```
✅ GET     /search/history              - Get search history
✅ DELETE  /search/history              - Clear search history
```

---

## 📊 **Request/Response Examples**

### Get Products

**Request:**
```
GET /products?page=1&limit=20
Authorization: Bearer {token}
```

**Response (200):**
```json
{
  "products": [
    {
      "id": "p1",
      "name": "Silver Ring",
      "price": 150.00,
      "description": "Beautiful silver ring",
      "image": "ring.jpg",
      "isFavorite": false
    }
  ],
  "page": 1,
  "total": 100
}
```

### Create Order

**Request:**
```json
POST /orders
Authorization: Bearer {token}

{
  "shippingAddress": "123 Main St",
  "paymentMethod": "card",
  "items": [
    {"productId": "p1", "quantity": 2}
  ]
}
```

**Response (201):**
```json
{
  "id": "order_123",
  "totalPrice": 300.00,
  "status": "pending",
  "createdAt": "2025-12-28T12:30:00Z"
}
```

### User Login

**Request:**
```json
POST /auth/login

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200):**
```json
{
  "token": "eyJhbGc...",
  "user": {
    "id": "user_123",
    "name": "John Doe",
    "email": "user@example.com",
    "avatar": "avatar.jpg"
  }
}
```

---

## ⚠️ **Error Responses**

### 400 Bad Request
```json
{
  "error": "VALIDATION_ERROR",
  "message": "Invalid email format",
  "details": {
    "field": "email",
    "reason": "Invalid format"
  }
}
```

### 401 Unauthorized
```json
{
  "error": "UNAUTHORIZED",
  "message": "Invalid or expired token"
}
```

### 404 Not Found
```json
{
  "error": "NOT_FOUND",
  "message": "Product not found"
}
```

### 500 Server Error
```json
{
  "error": "SERVER_ERROR",
  "message": "Internal server error",
  "requestId": "req_123"
}
```

---

## 🔐 **Authentication**

### Token Format

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Refresh

```kotlin
// Automatic token refresh on 401
interceptor.addInterceptor { chain ->
    val response = chain.proceed(request)
    
    if (response.code == 401) {
        // Refresh token
        val newToken = authApi.refreshToken()
        
        // Retry request with new token
        val newRequest = request.newBuilder()
            .header("Authorization", "Bearer $newToken")
            .build()
        
        return@addInterceptor chain.proceed(newRequest)
    }
    
    response
}
```

---

## 📄 **Data Models**

### Product

```kotlin
data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val image: String,
    val isFavorite: Boolean = false
)
```

### Order

```kotlin
data class Order(
    val id: String,
    val userId: String,
    val totalPrice: Double,
    val status: String,
    val date: String,
    val items: List<OrderItem>
)
```

### User

```kotlin
data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val avatar: String? = null
)
```

---

## 📄 **Rate Limiting**

```
100 requests per minute
Header: X-RateLimit-Remaining: 99
```

If exceeded:
```json
{
  "error": "RATE_LIMIT_EXCEEDED",
  "retryAfter": 60
}
```

---

## 🚀 **Implementation Status**

- ✅ Product API integrated
- ✅ Order API integrated
- ✅ Cart API integrated
- ✅ Auth API integrated
- ✅ User API integrated
- ✅ Error handling implemented
- ✅ Token refresh implemented
- ✅ Rate limiting handled

**Status:** Ready for production
