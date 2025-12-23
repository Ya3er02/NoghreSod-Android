package com.noghre.sod.util

import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.User

object TestData {
    fun createProduct(
        id: String = "1",
        name: String = "تست محصول",
        price: Double = 100000.0,
        rating: Float = 4.5f,
        reviewCount: Int = 10,
        stock: Int = 5
    ): Product = Product(
        id = id,
        name = name,
        price = price,
        images = listOf("https://via.placeholder.com/300"),
        rating = rating,
        reviewCount = reviewCount,
        stock = stock
    )

    fun createCategory(
        id: String = "1",
        name: String = "دسته"
    ): Category = Category(
        id = id,
        name = name,
        nameEn = "Bracelets",
        description = "Description",
        iconUrl = "",
        isActive = true
    )

    fun createUser(
        id: String = "1",
        phone: String = "09123456789",
        fullName: String = "محمد رضا"
    ): User = User(
        id = id,
        phone = phone,
        fullName = fullName,
        email = null,
        avatarUrl = null,
        isVerified = true
    )
}
