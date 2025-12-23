package com.noghre.sod.ui.utils

import com.noghre.sod.domain.model.Category
import com.noghre.sod.domain.model.Product

object PreviewData {
    val sampleCategory = Category(
        id = "1",
        name = "دستبند",
        nameEn = "Bracelet",
        description = "مجموعه ای از دستبندهای نقره ای زیبا",
        iconUrl = "",
        parentId = null,
        isActive = true,
        displayOrder = 1,
        productCount = 10
    )

    val sampleProduct = Product(
        id = "1",
        name = "دستبند نقره ای",
        nameEn = "Silver Bracelet",
        description = "دستبند زیبای نقره ای با طرح سنتی",
        descriptionEn = "Beautiful silver bracelet with traditional design",
        price = 500000.0,
        discountPrice = 450000.0,
        images = listOf("https://via.placeholder.com/300"),
        category = "Bracelets",
        stock = 10,
        rating = 4.5f,
        reviewCount = 25,
        weight = 5.0,
        material = "Silver",
        isFavorite = false,
        specifications = mapOf(
            "Weight" to "5g",
            "Material" to "Sterling Silver",
            "Design" to "Traditional"
        )
    )

    val sampleProducts = listOf(
        sampleProduct,
        sampleProduct.copy(id = "2", name = "دستبند دیگر"),
        sampleProduct.copy(id = "3", name = "دستبند سوم")
    )

    val sampleCategories = listOf(
        sampleCategory,
        sampleCategory.copy(id = "2", name = "گردنبند", nameEn = "Necklace"),
        sampleCategory.copy(id = "3", name = "انگشتر", nameEn = "Ring")
    )
}
