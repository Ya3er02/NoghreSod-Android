# 🚫 API Reference - NoghreSod

**Complete API endpoint documentation, request/response models, and integration guide.**

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Endpoints](#endpoints)
4. [Request/Response Models](#requestresponse-models)
5. [Error Handling](#error-handling)
6. [Rate Limiting](#rate-limiting)
7. [Integration Guide](#integration-guide)

---

## Overview

### Base URL

```
Development:  https://api-dev.noghresod.ir/v1
Staging:      https://api-staging.noghresod.ir/v1
Production:   https://api.noghresod.ir/v1
```

### Protocol

- **HTTPS Only** (TLS 1.2+)
- **JSON** Request/Response
- **REST** Architecture

### Timeout

- **Connection:** 30 seconds
- **Read:** 30 seconds
- **Write:** 30 seconds

---

## Authentication

### Bearer Token

```http
GET /products HTTP/1.1
Host: api.noghresod.ir
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Refresh

```http
POST /auth/refresh HTTP/1.1
Host: api.noghresod.ir
Content-Type: application/json

{
  "refresh_token": "refresh_token_here"
}
```

### Session Management

- Token expiry: 24 hours
- Refresh token expiry: 30 days
- Auto-refresh 1 hour before expiry

---

## Endpoints

### Products

#### GET /products

Fetch all products with optional filtering.

**Request:**
```http
GET /products?page=1&per_page=20&weight_min=1&weight_max=100 HTTP/1.1
Authorization: Bearer {token}
```

**Response (200 OK):**
```json
{
  "data": [
    {
      "id": "PROD-001",
      "name": "Silver Ring",
      "price": 250000,
      "weight": 5.2,
      "hallmark": "925",
      "gem_type": "diamond",
      "plating_type": "gold",
      "image_url": "https://..."
    }
  ],
  "pagination": {
    "page": 1,
    "per_page": 20,
    "total": 150,
    "total_pages": 8
  }
}
```

#### GET /products/{id}

Fetch single product details.

**Response (200 OK):**
```json
{
  "id": "PROD-001",
  "name": "Silver Ring",
  "description": "Beautiful 925 silver ring...",
  "price": 250000,
  "weight": 5.2,
  "hallmark": "925",
  "gem_type": "diamond",
  "plating_type": "gold",
  "images": ["url1", "url2"],
  "in_stock": true,
  "stock_count": 5
}
```

### Cart

#### GET /cart

Fetch user's shopping cart.

**Response (200 OK):**
```json
{
  "id": "CART-123",
  "items": [
    {
      "product_id": "PROD-001",
      "quantity": 2,
      "price": 250000
    }
  ],
  "subtotal": 500000,
  "tax": 50000,
  "shipping": 10000,
  "total": 560000
}
```

#### POST /cart/items

Add item to cart.

**Request:**
```json
{
  "product_id": "PROD-001",
  "quantity": 2
}
```

**Response (201 Created):**
```json
{
  "cart_id": "CART-123",
  "item_id": "ITEM-456",
  "quantity": 2,
  "total": 560000
}
```

#### DELETE /cart/items/{item_id}

Remove item from cart.

**Response (204 No Content)**

### Checkout

#### POST /checkout

Initiate checkout process.

**Request:**
```json
{
  "cart_id": "CART-123",
  "shipping_address": {
    "street": "...",
    "city": "...",
    "postal_code": "..."
  },
  "shipping_method": "standard",
  "discount_code": "SAVE10"
}
```

**Response (200 OK):**
```json
{
  "order_id": "ORD-789",
  "total": 490000,
  "discount_applied": 70000,
  "status": "pending_payment",
  "payment_gateway_url": "https://zarinpal.com/..."
}
```

### Payments

#### POST /payments/zarinpal/initialize

Initialize Zarinpal payment.

**Request:**
```json
{
  "amount": 490000,
  "order_id": "ORD-789",
  "description": "Order for silver jewelry",
  "callback_url": "https://app.noghresod.ir/payment/callback"
}
```

**Response (200 OK):**
```json
{
  "authority": "000000001234567890AB",
  "payment_url": "https://www.zarinpal.com/pg/StartPay/000000001234567890AB",
  "expires_at": "2025-01-28T15:50:00Z"
}
```

#### GET /payments/zarinpal/verify

Verify Zarinpal payment.

**Request:**
```http
GET /payments/zarinpal/verify?authority=000000001234567890AB&status=OK
```

**Response (200 OK):**
```json
{
  "ref_id": "1234567890",
  "card_hash": "...",
  "card_pan": "627648****7481",
  "status": "success",
  "order_id": "ORD-789"
}
```

### User Profile

#### GET /users/me

Fetch authenticated user profile.

**Response (200 OK):**
```json
{
  "id": "USR-001",
  "email": "user@example.com",
  "name": "John Doe",
  "phone": "+98901234567",
  "created_at": "2024-01-01T10:00:00Z"
}
```

#### PATCH /users/me

Update user profile.

**Request:**
```json
{
  "name": "Jane Doe",
  "phone": "+98909876543"
}
```

---

## Request/Response Models

### Product Model

```kotlin
data class ProductDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Double,
    @Json(name = "weight") val weight: Double,
    @Json(name = "hallmark") val hallmark: String,
    @Json(name = "gem_type") val gemType: String?,
    @Json(name = "plating_type") val platingType: String?,
    @Json(name = "image_url") val imageUrl: String
)
```

### Cart Item Model

```kotlin
data class CartItemDto(
    @Json(name = "item_id") val itemId: String,
    @Json(name = "product_id") val productId: String,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "price") val price: Double
)
```

### Order Model

```kotlin
data class OrderDto(
    @Json(name = "id") val id: String,
    @Json(name = "user_id") val userId: String,
    @Json(name = "items") val items: List<CartItemDto>,
    @Json(name = "subtotal") val subtotal: Double,
    @Json(name = "tax") val tax: Double,
    @Json(name = "shipping") val shipping: Double,
    @Json(name = "total") val total: Double,
    @Json(name = "status") val status: OrderStatus,
    @Json(name = "created_at") val createdAt: String
)

enum class OrderStatus {
    @Json(name = "pending_payment") PENDING_PAYMENT,
    @Json(name = "paid") PAID,
    @Json(name = "shipped") SHIPPED,
    @Json(name = "delivered") DELIVERED,
    @Json(name = "cancelled") CANCELLED
}
```

---

## Error Handling

### Error Response

```json
{
  "error": {
    "code": "INVALID_REQUEST",
    "message": "Product not found",
    "details": {
      "product_id": "Invalid product ID"
    }
  }
}
```

### Error Codes

| Code | HTTP | Description | Retry |
|------|------|-------------|-------|
| INVALID_REQUEST | 400 | Bad request | No |
| UNAUTHORIZED | 401 | Not authenticated | No |
| FORBIDDEN | 403 | Not authorized | No |
| NOT_FOUND | 404 | Resource not found | No |
| CONFLICT | 409 | Resource conflict | No |
| RATE_LIMITED | 429 | Too many requests | Yes |
| SERVER_ERROR | 500 | Internal error | Yes |
| UNAVAILABLE | 503 | Service unavailable | Yes |

---

## Rate Limiting

### Limits

- **Per second:** 100 requests
- **Per minute:** 1000 requests
- **Per hour:** 10000 requests

### Headers

```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1704067800
```

---

## Integration Guide

### Retrofit Service

```kotlin
interface ApiService {
    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 20
    ): ApiResponse<List<ProductDto>>
    
    @GET("products/{id}")
    suspend fun getProduct(
        @Path("id") id: String
    ): ApiResponse<ProductDto>
    
    @POST("cart/items")
    suspend fun addToCart(
        @Body request: AddToCartRequest
    ): ApiResponse<CartItemDto>
}
```

### Usage

```kotlin
class ProductsRepository(
    private val apiService: ApiService
) {
    suspend fun getProducts(): List<ProductEntity> {
        val response = apiService.getProducts()
        return response.data.map { it.toEntity() }
    }
}
```

---

## Related Documentation

- [DEVELOPMENT.md](../DEVELOPMENT.md) - Setup guide
- [DEPLOYMENT.md](../DEPLOYMENT.md) - Release process
- [ARCHITECTURE.md](../ARCHITECTURE.md) - Architecture details

---

**Last Updated:** December 28, 2025  
**API Version:** v1  
**Status:** ✅ Production
