package com.noghre.sod.domain.model

import com.noghre.sod.core.config.ShippingMethod

/**
 * مدل استان‌های ایران
 */
data class Province(
    val id: Int,
    val name: String,
    val persianName: String,
    val baseShippingCost: Long,  // هزینه ارسال و رهان مرکز
    val costPerKg: Long,         // هزینه برای هر کیلوگرم
    val standardDeliveryDays: Int,
    val expressDeliveryDays: Int
) {
    fun calculateShippingCost(
        weight: Double,
        isExpress: Boolean = false
    ): Long {
        val weightCost = (weight * costPerKg).toLong()
        return baseShippingCost + weightCost
    }
}

/**
 * روش‌های ارسال
 */
data class ShippingOption(
    val method: ShippingMethod,
    val cost: Long,
    val deliveryDays: Int,
    val description: String,
    val isAvailable: Boolean = true
)

/**
 * ناشانی برای ارسال
 */
data class ShippingAddress(
    val id: String,
    val recipientName: String,
    val phoneNumber: String,
    val province: Province,
    val city: String,
    val postalCode: String,
    val addressDetails: String,
    val isDefault: Boolean = false
)

/**
 * بیمه
 */
data class Insurance(
    val id: String,
    val name: String,
    val description: String,
    val coveragePercentage: Float,  // 100% برای انتخاب بیمه کامل
    val costPercentage: Float = 0.01f  // 1% ز قیمت
) {
    fun calculateCost(orderAmount: Long): Long {
        return (orderAmount * costPercentage).toLong()
    }
}

/**
 * خلاصه راهای ممکنه ارسال
 */
data class ShippingSummary(
    val province: Province,
    val availableMethods: List<ShippingOption>,
    val selectedMethod: ShippingOption?,
    val insuranceOptions: List<Insurance>,
    val selectedInsurance: Insurance?,
    val totalShippingCost: Long,
    val totalInsuranceCost: Long
) {
    val totalCost: Long
        get() = totalShippingCost + totalInsuranceCost
}

/**
 * Provinces Database
 */
object ProvinceDatabase {
    val provinces = listOf(
        Province(1, "Tehran", "تهران", 3000, 500, 1, 0),
        Province(2, "Alborz", "البرز", 4000, 600, 1, 0),
        Province(3, "Qazvin", "قزوین", 5000, 700, 2, 1),
        Province(4, "Mazandaran", "مازندران", 5000, 700, 2, 1),
        Province(5, "Gilan", "گیلان", 5000, 700, 2, 1),
        Province(6, "Ardabil", "اردبیل", 7000, 800, 3, 2),
        Province(7, "Isfahan", "اصفهان", 6000, 700, 2, 1),
        Province(8, "Semnan", "سمنان", 6000, 700, 2, 1),
        Province(9, "Zanjan", "زنجان", 6000, 700, 2, 1),
        Province(10, "Hamedan", "همدان", 6000, 700, 2, 1),
        Province(11, "Lorestan", "لرستان", 7000, 800, 3, 2),
        Province(12, "Bushehr", "بوشهر", 8000, 900, 3, 2),
        Province(13, "Razavi Khorasan", "خراسان رضوی", 8000, 900, 3, 2),
        Province(14, "North Khorasan", "خراسان شمالی", 8000, 900, 3, 2),
        Province(15, "Sistan and Baluchestan", "سیستان و بلوچستان", 10000, 1000, 4, 3),
        Province(16, "Khuzestan", "خوزستان", 7000, 800, 3, 2),
        Province(17, "Hormozgan", "هرمزگان", 9000, 1000, 4, 3),
        Province(18, "Hirmanshal", "هرمسلاز", 9000, 1000, 4, 3),
        Province(19, "Kerman", "کرمان", 8000, 900, 3, 2),
        Province(20, "Kermanshah", "کرمانشاه", 7000, 800, 3, 2),
        Province(21, "Ilam", "الام", 8000, 900, 3, 2),
        Province(22, "Kohgiluyeh and Boyer-Ahmad", "کهگیلويه و بويراحمد", 8000, 900, 3, 2),
        Province(23, "Fars", "فارس", 7000, 800, 3, 2),
        Province(24, "Yazd", "يزد", 7000, 800, 3, 2),
        Province(25, "Qom", "قم", 5000, 600, 1, 0),
        Province(26, "Markazi", "مركزي", 6000, 700, 2, 1),
        Province(27, "Esfahan", "اصفهان", 6000, 700, 2, 1),
        Province(28, "Chaharmahal and Bakhtiari", "چهارمحال و بختياري", 7000, 800, 3, 2),
        Province(29, "South Khorasan", "خراسان جنوبي", 9000, 1000, 4, 3),
        Province(30, "Qazvin", "قزوین", 5000, 700, 2, 1),
        Province(31, "Other", "ديگر", 15000, 2000, 7, 5)
    )
    
    fun getProvinceByName(name: String): Province? {
        return provinces.find { it.persianName.contains(name, ignoreCase = true) }
    }
    
    fun getProvinceById(id: Int): Province? {
        return provinces.find { it.id == id }
    }
    
    fun getAllProvinces(): List<Province> = provinces
}
