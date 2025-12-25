package com.noghre.sod.data.mapper

import com.noghre.sod.data.remote.dto.ProductDto
import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Currency
import com.noghre.sod.domain.model.Product
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Mapper for converting ProductDto to Product domain model.
 */
object ProductMapper {

    /**
     * Convert ProductDto to Product domain model.
     */
    fun ProductDto.toDomain(): Product = Product(
        id = id.orEmpty(),
        name = name.orEmpty(),
        description = description.orEmpty(),
        price = Product.Money(
            amount = price?.toBigDecimal() ?: BigDecimal.ZERO,
            currency = parseCurrency(currency)
        ),
        images = images.orEmpty().mapIndexed { index, url ->
            Product.ImageUrl(
                url = url,
                isMain = index == 0
            )
        },
        category = Category(
            id = categoryId.orEmpty(),
            name = categoryName.orEmpty()
        ),
        specifications = Product.ProductSpecifications(
            weight = weight,
            dimensions = dimensions,
            material = material,
            color = color,
            brand = brand,
            sku = sku
        ),
        availability = parseStockStatus(stock, preOrderDate),
        rating = Product.Rating(
            average = rating ?: 0.0,
            count = reviewCount ?: 0
        ),
        reviews = reviews.orEmpty().map { reviewDto ->
            Product.ProductReview(
                id = reviewDto.id.orEmpty(),
                userId = reviewDto.userId.orEmpty(),
                userName = reviewDto.userName.orEmpty(),
                rating = reviewDto.rating ?: 0,
                title = reviewDto.title.orEmpty(),
                content = reviewDto.content.orEmpty(),
                helpful = reviewDto.helpful ?: 0,
                createdAt = reviewDto.createdAt.orEmpty()
            )
        },
        tags = tags.orEmpty(),
        isNew = isNew ?: false,
        isOnSale = isOnSale ?: false,
        createdAt = createdAt.orEmpty(),
        updatedAt = updatedAt.orEmpty()
    )

    /**
     * Parse currency from string.
     */
    private fun parseCurrency(currencyCode: String?): Currency {
        return when (currencyCode?.uppercase()) {
            "USD" -> Currency.USD
            "EUR" -> Currency.EUR
            "GBP" -> Currency.GBP
            else -> Currency.IRR
        }
    }

    /**
     * Parse stock status from DTO.
     */
    private fun parseStockStatus(
        stock: Int?,
        preOrderDate: String?
    ): Product.StockStatus {
        return when {
            !preOrderDate.isNullOrBlank() -> {
                try {
                    val date = LocalDate.parse(preOrderDate, DateTimeFormatter.ISO_LOCAL_DATE)
                    Product.StockStatus.PreOrder(date)
                } catch (e: Exception) {
                    Product.StockStatus.OutOfStock
                }
            }
            stock == null || stock <= 0 -> Product.StockStatus.OutOfStock
            else -> Product.StockStatus.Available(stock)
        }
    }
}

/**
 * Convert ProductDto to Product.
 */
fun ProductDto.toDomain(): Product = ProductMapper.run { toDomain() }

/**
 * Convert list of ProductDto to list of Product.
 */
fun List<ProductDto>.toDomainList(): List<Product> = map { it.toDomain() }