package com.noghre.sod.core.config

/**
 * API endpoint constants organized by feature domain.
 * These are aligned with the NoghreSod backend API specification.
 *
 * All endpoints are relative to [AppConfig.Api.BASE_URL]
 * Example: GET https://api.noghresod.com/api/products
 */
object ApiEndpoints {

    // ============ Products Endpoints ============
    object Products {
        const val LIST = "/api/products"
        const val DETAIL = "/api/products/{id}"
        const val SEARCH = "/api/products/search"
        const val BY_CATEGORY = "/api/products/category/{categoryId}"
    }

    // ============ Categories Endpoints ============
    object Categories {
        const val LIST = "/api/categories"
        const val DETAIL = "/api/categories/{id}"
    }

    // ============ Pricing Endpoints ============
    object Prices {
        const val LIVE = "/api/prices/live"
        const val HISTORY = "/api/prices/history"
        const val CALCULATE = "/api/prices/calculate"
    }

    // ============ Authentication Endpoints ============
    object Auth {
        const val LOGIN = "/auth/login"
        const val REGISTER = "/auth/register"
        const val REFRESH = "/auth/refresh"
        const val LOGOUT = "/auth/logout"
    }

    // ============ User Endpoints ============
    object Users {
        const val PROFILE = "/api/users/profile"
        const val UPDATE_PROFILE = "/api/users/profile"
        const val CHANGE_PASSWORD = "/api/users/password"
    }

    // ============ Query Parameters ============
    object QueryParams {
        // Pagination
        const val PAGE = "page"
        const val LIMIT = "limit"

        // Search
        const val QUERY = "q"

        // Filtering
        const val CATEGORY_ID = "category"
        const val CATEGORY = "category"
        const val SORT = "sort"

        // Pricing
        const val DAYS = "days"
        const val WEIGHT = "weight"
        const val PURITY = "purity"
    }

    // ============ Header Keys ============
    object Headers {
        const val AUTHORIZATION = "Authorization"
        const val CONTENT_TYPE = "Content-Type"
        const val ACCEPT = "Accept"
        const val USER_AGENT = "User-Agent"
    }

    // ============ Response Field Names ============
    object ResponseFields {
        const val DATA = "data"
        const val MESSAGE = "message"
        const val STATUS = "status"
        const val ERROR = "error"
        const val PAGE = "page"
        const val LIMIT = "limit"
        const val TOTAL = "total"
        const val TOTAL_PAGES = "totalPages"
    }

    // ============ HTTP Status Codes ============
    object HttpStatus {
        const val OK = 200
        const val CREATED = 201
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val CONFLICT = 409
        const val UNPROCESSABLE_ENTITY = 422
        const val INTERNAL_SERVER_ERROR = 500
        const val BAD_GATEWAY = 502
        const val SERVICE_UNAVAILABLE = 503
        const val GATEWAY_TIMEOUT = 504
    }
}
