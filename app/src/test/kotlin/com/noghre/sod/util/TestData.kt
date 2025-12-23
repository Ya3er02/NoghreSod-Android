package com.noghre.sod.util

import com.noghre.sod.domain.model.Address
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Order
import com.noghre.sod.domain.model.OrderItem
import com.noghre.sod.domain.model.OrderStatus
import com.noghre.sod.domain.model.PaymentMethod
import com.noghre.sod.domain.model.Product
import com.noghre.sod.domain.model.User
import java.util.Date

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
        description = "توضيحات",
        categoryId = "1",
        rating = rating,
        reviewCount = reviewCount,
        stock = stock,
        discount = null,
        createdAt = Date(),
        updatedAt = Date()
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

    fun createAddress(
        id: String = "1",
        userId: String = "1",
        fullName: String = "محمد"
    ): Address = Address(
        id = id,
        userId = userId,
        fullName = fullName,
        phoneNumber = "09123456789",
        province = "تهران",
        city = "تهران",
        street = "بلوار",
        postalCode = "12345"
    )

    fun createOrder(
        id: String = "1",
        userId: String = "1",
        status: OrderStatus = OrderStatus.PENDING
    ): Order = Order(
        id = id,
        userId = userId,
        items = listOf(
            OrderItem(
                productId = "1",
                productName = "محصول",
                quantity = 1,
                price = 100000.0
            )
        ),
        address = createAddress(userId = userId),
        status = status,
        totalPrice = 100000.0,
        paymentMethod = PaymentMethod.ZARINPAL,
        createdAt = Date(),
        updatedAt = Date()
    )
}
