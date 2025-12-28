package com.noghre.sod.domain.usecase

import com.noghre.sod.core.config.IranConfig
import com.noghre.sod.core.config.ShippingMethod
import com.noghre.sod.core.util.PersianNumberFormatter
import com.noghre.sod.domain.model.Insurance
import com.noghre.sod.domain.model.Province
import com.noghre.sod.domain.model.ProvinceDatabase
import com.noghre.sod.domain.model.ShippingOption
import com.noghre.sod.domain.model.ShippingSummary
import javax.inject.Inject

/**
 * محاسبه هزینه‌های ارسال ملي ایران
 */
class CalculateShippingUseCase @Inject constructor() {
    
    /**
     * محاسبه موجود اختيارات ارسال برای استان
     */
    fun getShippingOptions(
        province: Province,
        weight: Double
    ): List<ShippingOption> {
        return listOf(
            ShippingOption(
                method = ShippingMethod.DENA,
                cost = province.calculateShippingCost(weight, false),
                deliveryDays = province.standardDeliveryDays,
                description = "دنا - ارسال معمولی",
                isAvailable = true
            ),
            ShippingOption(
                method = ShippingMethod.MONTAQEL_OL_BAD,
                cost = (province.calculateShippingCost(weight, false) * 1.2).toLong(),
                deliveryDays = 2,
                description = "منتقل الباد - ارسال سريع",
                isAvailable = true
            ),
            ShippingOption(
                method = ShippingMethod.ZERBAHAR,
                cost = (province.calculateShippingCost(weight, false) * 1.5).toLong(),
                deliveryDays = 1,
                description = "زرباخر - ارسال فورا با Tracking",
                isAvailable = true
            )
        )
    }
    
    /**
     * گزینه‌های بیمه
     */
    fun getInsuranceOptions(): List<Insurance> {
        return listOf(
            Insurance(
                id = "insurance_none",
                name = "بدون بیمه",
                description = "برای اشياء كم ارزش",
                coveragePercentage = 0f,
                costPercentage = 0f
            ),
            Insurance(
                id = "insurance_standard",
                name = "بیمه معمولی",
                description = "بوله بیمه 50% قیمت",
                coveragePercentage = 0.5f,
                costPercentage = 0.02f
            ),
            Insurance(
                id = "insurance_premium",
                name = "بیمه کامل",
                description = "بوله بیمه 100% قیمت",
                coveragePercentage = 1f,
                costPercentage = 0.035f
            )
        )
    }
    
    /**
     * خلاصه بزین
     */
    fun calculateShippingSummary(
        provinceId: Int,
        weight: Double,
        selectedShippingMethod: ShippingMethod?,
        selectedInsurance: Insurance?,
        orderAmount: Long
    ): ShippingSummary? {
        val province = ProvinceDatabase.getProvinceById(provinceId) ?: return null
        val shippingOptions = getShippingOptions(province, weight)
        val selectedOption = shippingOptions.find { it.method == selectedShippingMethod }
            ?: shippingOptions.firstOrNull()
        
        val insuranceOptions = getInsuranceOptions()
        val insurance = selectedInsurance ?: insuranceOptions.first()
        
        return ShippingSummary(
            province = province,
            availableMethods = shippingOptions,
            selectedMethod = selectedOption,
            insuranceOptions = insuranceOptions,
            selectedInsurance = insurance,
            totalShippingCost = selectedOption?.cost ?: 0L,
            totalInsuranceCost = insurance.calculateCost(orderAmount)
        )
    }
    
    /**
     * محاسبه قیمت نهایی با مالیات
     */
    fun calculateOrderTotal(
        basePrice: Long,
        shippingCost: Long,
        insuranceCost: Long,
        taxRate: Float = 0.09f
    ): Long {
        val subtotal = basePrice + shippingCost + insuranceCost
        return PersianNumberFormatter.addTax(subtotal, taxRate)
    }
    
    /**
     * بررسی رایگان ارسال
     */
    fun isFreeShippingApplicable(
        orderAmount: Long,
        province: Province
    ): Boolean {
        return orderAmount >= IranConfig.config.maxFreeShipping &&
               province.id in 1..5  // Only for major cities
    }
}
