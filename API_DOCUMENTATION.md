# 🌐 NoghreSod API Documentation

## Overview

**Base URL (Production):** `https://api.noghresod.ir/v1/`  
**Base URL (Staging):** `https://staging-api.noghresod.ir/v1/`  
**API Version:** v1  
**Last Updated:** 2025-12-25  
**Protocol:** HTTPS only (TLS 1.2+)  
**Content-Type:** `application/json`  
**Character Encoding:** UTF-8

---

## 🔐 Authentication

All authenticated endpoints require a valid **JWT Bearer Token** in the `Authorization` header.

### Headers

```
Authorization: Bearer <access_token>
Content-Type: application/json
Accept: application/json
X-Device-ID: <unique_device_id>
X-App-Version: 1.0.0
X-Idempotency-Key: <unique_request_id>
```

### Token Lifecycle

| Token Type | Lifetime | Use Case |
|------------|----------|----------|
| Access Token | 15 minutes | API requests |
| Refresh Token | 30 days | Renew access token |

### Token Refresh

When access token expires (401 response), use refresh token:

```http
POST /auth/refresh
Content-Type: application/json

{
  "refresh_token": "eyJhbGc..."
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "access_token": "eyJhbGc...",
    "refresh_token": "eyJhbGc...",
    "expires_in": 900,
    "token_type": "Bearer"
  }
}
```

---

## 📦 Products API

### 1. List Products (Paginated)

Retrieve a paginated list of products with optional filtering.

**Endpoint:** `GET /products`  
**Authentication:** Optional  
**Rate Limit:** 100 req/hour (unauthenticated), 1000 req/hour (authenticated)

**Query Parameters:**

| Parameter | Type | Required | Default | Description |
|-----------|------|----------|---------|-------------|
| `page` | integer | No | 1 | Page number (min: 1) |
| `per_page` | integer | No | 20 | Items per page (min: 1, max: 100) |
| `category_id` | string | No | - | Filter by category UUID |
| `search` | string | No | - | Search in name, description |
| `min_price` | integer | No | - | Minimum price (IRR) |
| `max_price` | integer | No | - | Maximum price (IRR) |
| `sort_by` | enum | No | `created_at` | `created_at`, `price`, `name`, `rating` |
| `order` | enum | No | `desc` | `asc`, `desc` |
| `tags` | string | No | - | Comma-separated tags |
| `in_stock` | boolean | No | - | Filter in-stock products |

**Example Request:**

```http
GET /products?page=1&per_page=20&category_id=cat-123&sort_by=price&order=asc
Authorization: Bearer eyJhbGc...
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "products": [
      {
        "id": "prod-uuid-123",
        "name": "زنجیری نقره گردنبند",
        "slug": "silver-chain-necklace",
        "description": "گردنبند نقره عیار 925 با طرح زنجیری ظریف",
        "price": {
          "amount": 2500000,
          "currency": "IRR",
          "formatted": "۲,۵۰۰,۰۰۰ تومان"
        },
        "discount": {
          "percentage": 10,
          "amount": 250000,
          "final_price": 2250000
        },
        "images": [
          {
            "url": "https://cdn.noghresod.ir/products/prod-123-main.jpg",
            "thumbnail": "https://cdn.noghresod.ir/products/prod-123-thumb.jpg",
            "alt": "زنجیری نقره گردنبند",
            "order": 1
          }
        ],
        "category": {
          "id": "cat-123",
          "name": "گردنبند",
          "slug": "necklaces"
        },
        "tags": ["زنانه", "زنجیری", "نقره"],
        "rating": {
          "average": 4.5,
          "count": 128
        },
        "stock": {
          "available": 15,
          "status": "in_stock"
        },
        "specifications": {
          "material": "925 نقره",
          "weight": "12 گرم",
          "length": "45 سانتی متر"
        },
        "is_favorite": false,
        "created_at": "2025-12-20T10:30:00Z",
        "updated_at": "2025-12-25T15:45:00Z"
      }
    ],
    "pagination": {
      "current_page": 1,
      "per_page": 20,
      "total_items": 156,
      "total_pages": 8,
      "has_next": true,
      "has_previous": false
    },
    "filters": {
      "categories": [
        {"id": "cat-1", "name": "گردنبند", "count": 45},
        {"id": "cat-2", "name": "انگشتر", "count": 67}
      ],
      "price_range": {
        "min": 500000,
        "max": 15000000
      }
    }
  },
  "meta": {
    "timestamp": "2025-12-25T19:30:00Z",
    "request_id": "req-abc123"
  }
}
```

**Error Responses:**

**400 Bad Request** - Invalid parameters
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PARAMETERS",
    "message": "پارامترهای ورودی نامعتبر است",
    "details": {
      "page": ["Must be a positive integer"],
      "per_page": ["Cannot exceed 100"]
    }
  },
  "meta": {
    "timestamp": "2025-12-25T19:30:00Z",
    "request_id": "req-abc123"
  }
}
```

**429 Too Many Requests** - Rate limit exceeded
```json
{
  "success": false,
  "error": {
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "تعداد درخواست‌ها از حد مجاز گذشته است",
    "retry_after": 60
  }
}
```

---

### 2. Get Product Detail

**Endpoint:** `GET /products/{id}`  
**Authentication:** Optional  
**Path Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `id` | string | Yes | Product UUID or slug |

**Example Request:**

```http
GET /products/prod-uuid-123
Authorization: Bearer eyJhbGc...
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "id": "prod-uuid-123",
    "name": "زنجیری نقره گردنبند",
    "description": "گردنبند نقره عیار 925 با طرح زنجیری ظریف و قابل تنظیم",
    "long_description": "این گردنبند از نقره خالص عیار 925...",
    "price": {
      "amount": 2500000,
      "currency": "IRR"
    },
    "images": [...],
    "related_products": [
      {
        "id": "prod-456",
        "name": "دستبند نقره",
        "price": 1800000,
        "image": "..."
      }
    ],
    "reviews": {
      "average_rating": 4.5,
      "total_count": 128,
      "rating_distribution": {
        "5": 80,
        "4": 30,
        "3": 12,
        "2": 4,
        "1": 2
      },
      "recent_reviews": [
        {
          "id": "rev-123",
          "user": {
            "name": "علی رضایی",
            "avatar": "..."
          },
          "rating": 5,
          "comment": "کیفیت عالی و ارسال سریع",
          "created_at": "2025-12-20T10:00:00Z",
          "helpful_count": 12
        }
      ]
    }
  }
}
```

**Error Responses:**

**404 Not Found** - Product not found
```json
{
  "success": false,
  "error": {
    "code": "PRODUCT_NOT_FOUND",
    "message": "محصول مورد نظر یافت نشد",
    "product_id": "prod-invalid"
  }
}
```

---

## 🛒 Cart API

### 1. Get Cart

**Endpoint:** `GET /cart`  
**Authentication:** Required

**Success Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "id": "cart-uuid-123",
    "items": [
      {
        "id": "item-1",
        "product": {
          "id": "prod-123",
          "name": "گردنبند نقره",
          "price": 2500000,
          "image": "..."
        },
        "quantity": 2,
        "unit_price": 2500000,
        "total_price": 5000000
      }
    ],
    "summary": {
      "subtotal": 5000000,
      "discount": 500000,
      "tax": 450000,
      "shipping": 0,
      "total": 4950000
    },
    "updated_at": "2025-12-25T19:30:00Z"
  }
}
```

### 2. Add to Cart

**Endpoint:** `POST /cart/items`  
**Authentication:** Required

**Request Body:**

```json
{
  "product_id": "prod-uuid-123",
  "quantity": 1,
  "selected_variant": {
    "size": "M",
    "color": "silver"
  }
}
```

**Success Response (201 Created):**

```json
{
  "success": true,
  "data": {
    "cart": { /* full cart object */ },
    "added_item": {
      "id": "item-new",
      "product_id": "prod-uuid-123",
      "quantity": 1
    }
  },
  "message": "محصول به سبد خرید اضافه شد"
}
```

---

## 🔐 Authentication API

### 1. Login

**Endpoint:** `POST /auth/login`  
**Authentication:** Not required

**Request Body:**

```json
{
  "email": "user@example.com",
  "password": "SecurePass123!",
  "device_id": "device-uuid-123"
}
```

**Success Response (200 OK):**

```json
{
  "success": true,
  "data": {
    "user": {
      "id": "user-123",
      "email": "user@example.com",
      "name": "علی رضایی",
      "phone": "+989123456789",
      "avatar": "https://cdn.noghresod.ir/avatars/user-123.jpg"
    },
    "tokens": {
      "access_token": "eyJhbGc...",
      "refresh_token": "eyJhbGc...",
      "expires_in": 900
    }
  }
}
```

### 2. Register

**Endpoint:** `POST /auth/register`

**Request Body:**

```json
{
  "name": "علی رضایی",
  "email": "user@example.com",
  "password": "SecurePass123!",
  "password_confirmation": "SecurePass123!",
  "phone": "+989123456789",
  "terms_accepted": true
}
```

**Validation Rules:**

- **name**: 2-100 characters
- **email**: Valid email format
- **password**:
  - Min 8 characters
  - At least 1 uppercase
  - At least 1 lowercase
  - At least 1 number
  - At least 1 special character
- **phone**: Valid Iranian mobile format

---

## 📝 Orders API

### 1. Create Order

**Endpoint:** `POST /orders`  
**Authentication:** Required

**Request Body:**

```json
{
  "cart_id": "cart-123",
  "shipping_address": {
    "full_name": "علی رضایی",
    "phone": "+989123456789",
    "province": "تهران",
    "city": "تهران",
    "address": "خیابان ولیعصر، پلاک 123",
    "postal_code": "1234567890"
  },
  "payment_method": "online",
  "notes": "لطفاً در بسته‌بندی دقت کنید"
}
```

---

## ⚠️ Error Codes Reference

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `INVALID_PARAMETERS` | 400 | Invalid request parameters |
| `INVALID_CREDENTIALS` | 401 | Wrong email/password |
| `UNAUTHORIZED` | 401 | Missing or invalid token |
| `TOKEN_EXPIRED` | 401 | Access token expired |
| `FORBIDDEN` | 403 | No permission |
| `NOT_FOUND` | 404 | Resource not found |
| `OUT_OF_STOCK` | 409 | Product unavailable |
| `ACCOUNT_LOCKED` | 423 | Account temporarily locked |
| `RATE_LIMIT_EXCEEDED` | 429 | Too many requests |
| `INTERNAL_SERVER_ERROR` | 500 | Server error |
| `SERVICE_UNAVAILABLE` | 503 | Maintenance mode |

---

## 📊 Rate Limiting

| User Type | Limit | Window |
|-----------|-------|--------|
| Anonymous | 100 requests | 1 hour |
| Authenticated | 1000 requests | 1 hour |
| Premium | 5000 requests | 1 hour |

**Rate limit headers:**
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 987
X-RateLimit-Reset: 1703520000
```

---

## 🔄 Versioning

API uses URL versioning: `/v1/`, `/v2/`

**Deprecation Policy:**
- Old versions supported for 6 months after new version release
- Deprecation notices sent via `X-API-Deprecation` header

```
X-API-Deprecation: This endpoint will be deprecated on 2026-06-25
X-API-Sunset: 2026-06-25T00:00:00Z
```

---

**Generated with:** Swagger/OpenAPI  
**Last Updated:** 2025-12-25  
**Version:** 1.0.0  
**Maintainer:** NoghreSod Dev Team
