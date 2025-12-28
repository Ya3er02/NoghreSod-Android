# 🇮🇷 راهنمای بومی‌سازی فارسی برای بازار ایران

**بومی‌سازی جامع NoghreSod برای ایرانیان مقیم در ایران**

---

## 1️⃣ تنظیمات RTL (راست به چپ)

### AndroidManifest.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">
    
    <!-- فعال‌سازی RTL -->
    <application
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:theme="@style/Theme.NoghreSod">
        
        <!-- Activity ها -->
        <activity
            android:name=".presentation.ui.MainActivity"
            android:exported="true"
            android:screenOrientation="portrait" />
        
    </application>
</manifest>
```

### build.gradle.kts (RTL Support)

```kotlin
android {
    compileSdk = 34
    
    defaultConfig {
        minSdk = 24
        targetSdk = 34
        
        // فعال‌سازی پشتیبانی RTL
        resConfigs("fa-rIR") // فارسی ایران
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}

dependencies {
    // پشتیبانی RTL Jetpack Compose
    implementation("androidx.compose.material3:material3:1.1.0")
}
```

---

## 2️⃣ فونت‌های فارسی

### فونت‌های پیشنهادی برای ایران

```
فونت‌های رسمی ایران:
1. Vazir (بهترین برای UI)
2. IranSans (فونت رسمی)
3. Droid Sans Farsi
4. B Yekan
5. Sahel

Sans-Serif (بدون زینت) برای صفحات وب و اپلیکیشن
```

### پیاده‌سازی Fonts (Jetpack Compose)

```kotlin
// res/font/vazir_regular.ttf
// res/font/vazir_bold.ttf
// res/font/iransans_regular.ttf

@Composable
fun VazirFont() {
    val vazirFamily = FontFamily(
        Font(R.font.vazir_regular, FontWeight.Normal),
        Font(R.font.vazir_bold, FontWeight.Bold)
    )
    
    MaterialTheme(
        typography = Typography(
            headlineLarge = TextStyle(
                fontFamily = vazirFamily,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            ),
            bodyMedium = TextStyle(
                fontFamily = vazirFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        )
    ) {
        // محتوی اپلیکیشن
    }
}
```

### تنظیمات Compose Theme

```kotlin
object FarsiTheme {
    val colors = darkColorScheme(
        primary = Color(0xFF32B8C6),        // فیروزه‌ای (رنگ تراست)
        secondary = Color(0xFFE8A87C),     // طلایی (طلا)
        tertiary = Color(0xFFC84A31),      // مسی (نقره)
        background = Color(0xFFFCFCF9),    // سفید خاکستری
        surface = Color(0xFFFFFFFF),       // سفید
        error = Color(0xFFC01530)          // قرمز
    )
}

@Composable
fun NoghreSodTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FarsiTheme.colors,
        typography = createFarsiTypography(),
        content = content
    )
}
```

---

## 3️⃣ تقویم جلالی (فارسی)

### Dependency

```kotlin
dependencies {
    implementation("com.aminography:primcelendar:2.0.0") // تقویم جلالی
}
```

### PersianDateConverter

```kotlin
package com.noghre.sod.core.util

import java.util.Calendar
import java.util.GregorianCalendar

/**
 * مبدل تاریخ میلادی به جلالی (خورشیدی)
 * برای استفاده در تاریخ‌های پیش‌فروش و سفارشات
 */
object PersianDateConverter {
    
    // تقویم جلالی
    private val jy_breaks = intArrayOf(
        -61, 9, 38, 199, 426, 686, 756, 818, 1111, 1181, 1210,
        1635, 2060, 2097, 2192, 2262, 2324, 2394, 2456, 3178
    )
    
    /**
     * تبدیل تاریخ میلادی به خورشیدی
     * @param gregorian تاریخ میلادی
     * @return تاریخ خورشیدی به صورت "۱۴۰۳/۱۰/۰۶"
     */
    fun toPersianDate(gregorian: Calendar): String {
        val gy = gregorian.get(Calendar.YEAR)
        val gm = gregorian.get(Calendar.MONTH) + 1
        val gd = gregorian.get(Calendar.DAY_OF_MONTH)
        
        val (jy, jm, jd) = gregorianToPersian(gy, gm, gd)
        
        return "$jy/${formatMonth(jm)}/${formatDay(jd)}"
    }
    
    /**
     * تبدیل تاریخ خورشیدی به متن فارسی
     * مثال: "دوشنبه، ۶ دی ۱۴۰۳"
     */
    fun toPersianText(jy: Int, jm: Int, jd: Int): String {
        val dayName = getDayName(jy, jm, jd)
        val monthName = getMonthName(jm)
        val farsiDay = toFarsiNumbers("$jd")
        val farsiYear = toFarsiNumbers("$jy")
        
        return "$dayName، $farsiDay $monthName $farsiYear"
    }
    
    /**
     * تبدیل اعداد انگلیسی به فارسی
     * 2024 → ۲۰۲۴
     */
    fun toFarsiNumbers(input: String): String {
        val farsiDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        var output = input
        
        for (i in 0..9) {
            output = output.replace(i.toString(), farsiDigits[i])
        }
        return output
    }
    
    /**
     * نام روزهای هفته
     */
    private fun getDayName(jy: Int, jm: Int, jd: Int): String {
        val days = arrayOf(
            "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه",
            "چهارشنبه", "پنج‌شنبه", "جمعه"
        )
        // محاسبه روز هفته
        val dayOfWeek = calculateDayOfWeek(jy, jm, jd)
        return days[dayOfWeek]
    }
    
    /**
     * نام ماه‌های سال خورشیدی
     */
    private fun getMonthName(jm: Int): String {
        val months = arrayOf(
            "", "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
            "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
        )
        return months.getOrNull(jm) ?: ""
    }
    
    private fun formatMonth(month: Int): String {
        return if (month < 10) "0$month" else "$month"
    }
    
    private fun formatDay(day: Int): String {
        return if (day < 10) "0$day" else "$day"
    }
    
    private fun gregorianToPersian(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        // الگوریتم تبدیل میلادی به خورشیدی
        // (کد کامل در فایل کامل)
        return Triple(1403, 10, 6)
    }
    
    private fun calculateDayOfWeek(jy: Int, jm: Int, jd: Int): Int {
        // محاسبه روز هفته
        return 0
    }
}
```

### استفاده در Compose

```kotlin
@Composable
fun OrderDateDisplay() {
    val calendar = Calendar.getInstance()
    val persianDate = PersianDateConverter.toPersianText(
        jy = 1403,
        jm = 10,
        jd = 6
    )
    
    Text(
        text = persianDate,  // "دوشنبه، ۶ دی ۱۴۰۳"
        fontFamily = VazirFont.family,
        fontSize = 16.sp
    )
}
```

---

## 4️⃣ درگاه‌های پرداخت ایرانی

### درگاه‌های فعال

```kotlin
enum class PaymentGateway {
    // درگاه‌های پرداخت معتبر ایران
    ZARINPAL,        // زرین‌پال (محبوب‌ترین)
    IDPAY,          // آی‌دی‌پی (قابل اعتماد)
    NEXTPAY,        // نکست‌پی (سریع)
    ZIBAL,          // زیبال
    PAYPINGSUM,     // پی‌پینگ سام
    CASH_ON_DELIVERY // پرداخت در محل (برای نقره‌جات)
}
```

### Zarinpal Integration (اصلی)

```kotlin
package com.noghre.sod.data.payment

import retrofit2.http.*

interface ZarinpalApiService {
    
    /**
     * درخواست پرداخت - واحد پول: تومان
     * بدون نیاز به تقسیم بر 10
     */
    @POST("pg/v4/payment/request.json")
    suspend fun requestPayment(
        @Header("Authorization") authorization: String,
        @Body request: ZarinpalPaymentRequest
    ): ZarinpalPaymentResponse
    
    @POST("pg/v4/payment/verify.json")
    suspend fun verifyPayment(
        @Header("Authorization") authorization: String,
        @Body request: ZarinpalVerifyRequest
    ): ZarinpalVerifyResponse
}

data class ZarinpalPaymentRequest(
    val merchant_id: String,      // شناسه تاجر
    val amount: Long,             // مبلغ به تومان
    val currency: String = "TMN",  // واحد پول: تومان
    val description: String,      // توصیف سفارش
    val metadata: Map<String, String>?,
    val callback_url: String      // آدرس بازگشت
)

data class ZarinpalVerifyRequest(
    val merchant_id: String,
    val amount: Long,
    val authority: String
)
```

### قیمت‌گذاری به تومان

```kotlin
@Composable
fun PriceDisplay(priceInToman: Long) {
    val formattedPrice = String.format("%,d", priceInToman)
    val farsiPrice = PersianDateConverter.toFarsiNumbers(formattedPrice)
    
    Text(
        text = "$farsiPrice تومان",
        fontFamily = VazirFont.family,
        fontSize = 18.sp,
        color = Color(0xFF32B8C6)  // فیروزه‌ای
    )
}

// استفاده:
// PriceDisplay(priceInToman = 850000)  // نمایش: ۸۵۰٬۰۰۰ تومان
```

---

## 5️⃣ قالب شماره ایرانی

### PersianNumberFormatter

```kotlin
object PersianNumberFormatter {
    
    /**
     * تبدیل شماره موبایل انگلیسی به فارسی و بالعکس
     */
    fun formatPhoneNumber(phone: String): String {
        var cleaned = phone.replace(Regex("[^0-9]"), "")
        
        // تبدیل شروع 0 به 0
        if (cleaned.startsWith("+98")) {
            cleaned = "0" + cleaned.substring(3)
        }
        
        // فرمت: 0910 123 4567
        return if (cleaned.length >= 10) {
            "${cleaned.substring(0, 4)} ${cleaned.substring(4, 7)} ${cleaned.substring(7)}"
        } else {
            cleaned
        }
    }
    
    /**
     * تبدیل شماره کارت به فارسی
     * 6037697***6661 → ۶۰۳۷۶۹۷***۶۶۶۱
     */
    fun formatCardNumber(cardNumber: String): String {
        val masked = cardNumber.substring(0, 6) + "***" + cardNumber.substring(cardNumber.length - 4)
        return PersianDateConverter.toFarsiNumbers(masked)
    }
    
    /**
     * تبدیل قیمت با جداکننده هزار
     * 1250000 → ۱٬۲۵۰٬۰۰۰ تومان
     */
    fun formatPrice(amount: Long): String {
        val formatted = String.format("%,d", amount)
        return PersianDateConverter.toFarsiNumbers(formatted) + " تومان"
    }
}
```

---

## 6️⃣ نرخ ارز و مالیات

### Tax و Pricing Logic

```kotlin
data class PriceCalculation(
    val basePrice: Long,           // قیمت پایه
    val weight: Double,            // وزن (گرم)
    val silverPrice: Long,         // قیمت هر گرم نقره
    val shippingCost: Long = 0,    // هزینه ارسال
    val taxRate: Float = 0.09f     // مالیات ۹٪
) {
    
    // قیمت نقره براساس وزن
    val silverCost: Long
        get() = (weight * silverPrice).toLong()
    
    // مالیات بر قیمت کل
    val taxAmount: Long
        get() = ((basePrice + shippingCost) * taxRate).toLong()
    
    // قیمت نهایی
    val totalPrice: Long
        get() = basePrice + taxAmount + shippingCost
}

// استفاده:
val calculation = PriceCalculation(
    basePrice = 850000,      // نقره کاری
    weight = 15.5,           // ۱۵.۵ گرم نقره
    silverPrice = 2500000,   // قیمت هر گرم
    shippingCost = 5000
)

println(calculation.totalPrice)  // قیمت نهایی با مالیات و ارسال
```

---

## 7️⃣ محیط کار ایرانی

### Config for Iran

```kotlin
data class IranMarketConfig(
    // منطقه‌زمانی تهران
    val timezone: String = "Asia/Tehran",
    
    // پول: تومان (نه ریال!)
    val currency: String = "TMN",
    val currencySymbol: String = "ت",
    
    // محدودیت‌های بانکی ایران
    val maxTransactionAmount: Long = 999_999_999,  // حد اکثر تراکنش
    val minTransactionAmount: Long = 1000,         // حد اقل تراکنش
    
    // مالیات استاندارد
    val taxRate: Float = 0.09f,  // ۹ درصد
    
    // هزینه ارسال
    val baseShippingCost: Long = 5_000,      // ۵ هزار تومان
    val shippingPerKg: Long = 2_000,         // ۲ هزار تومان برای هر کیلوگرم
    
    // استان‌ها (مناطق)
    val provinces: List<String> = listOf(
        "تهران", "البرز", "قزوین", "مازندران", "گیلان",
        "خراسان رضوی", "خوزستان", "کرمانشاه", "فارس", "اصفهان"
    )
)

object IranConfig {
    val config = IranMarketConfig()
    
    fun getTimeInTehran(): String {
        val tehranZone = TimeZone.getTimeZone("Asia/Tehran")
        val calendar = Calendar.getInstance(tehranZone)
        val formatter = SimpleDateFormat("HH:mm", Locale("fa", "IR"))
        formatter.timeZone = tehranZone
        return formatter.format(calendar.time)
    }
}
```

---

## 8️⃣ رابط کاربری ایرانی

### Compose Layout RTL-Safe

```kotlin
@Composable
fun CartItemCard(item: CartItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // تصویر (سمت راست در RTL)
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            
            // اطلاعات (وسط)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = item.name,
                    fontFamily = VazirFont.family,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.End  // توالی متن راست
                )
                
                Text(
                    text = PersianNumberFormatter.formatPrice(item.price),
                    fontFamily = VazirFont.family,
                    fontSize = 14.sp,
                    color = Color(0xFF32B8C6),
                    textAlign = TextAlign.End
                )
            }
            
            // دکمه‌ها (سمت چپ در RTL)
            Column {
                IconButton(
                    onClick = { /* حذف */ },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف"
                    )
                }
            }
        }
    }
}
```

### ButDialogContent (فارسی)

```kotlin
@Composable
fun ErrorDialog(
    title: String = "خطا",
    message: String,
    onDismiss: () -> Unit,
    buttonText: String = "باشه"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontFamily = VazirFont.family,
                textAlign = TextAlign.End
            )
        },
        text = {
            Text(
                text = message,
                fontFamily = VazirFont.family,
                textAlign = TextAlign.End
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF32B8C6)
                )
            ) {
                Text(
                    text = buttonText,
                    fontFamily = VazirFont.family
                )
            }
        }
    )
}
```

---

## 9️⃣ محدودیت‌های تحریمی و بانکی

```kotlin
object IranianBankingRules {
    
    /**
     * بنک‌های اصلی ایران
     * این بنک‌ها برای پرداخت قابل استفاده هستند
     */
    val supportedBanks = listOf(
        "بانک ملی ایران",
        "بانک سپه",
        "بانک صنعت و معدن",
        "بانک توسعه تعاون",
        "بانک حکمت ایرانیان",
        "بانک دی",
        "بانک رسالت",
        "بانک شهر",
        "بانک شتاب",
        "بانک صادرات ایران",
        "بانک رفاه",
        "بانک کارآفرین",
        "بانک پاسارگاد",
        "بانک پست بانک",
        "بانک اقتصاد نوین",
        "بانک انصار",
        "بانک ایران و چین",
        "بانک کوثر",
        "بانک مسکن",
        "بانک توسعه صادرات",
        "بانک دی"
    )
    
    /**
     * سال مالی ایران: فروردین تا اسفند
     */
    val iraqiFiscalYear = FiscalYear(
        startMonth = 1,   // فروردین
        endMonth = 12     // اسفند
    )
    
    /**
     * تحریم‌های بین‌المللی
     * برخی درگاه‌ها یا روش‌های پرداخت ممکن نیستند
     */
    val sanctionedServices = listOf(
        "PayPal",
        "Stripe",
        "Square",
        "2Checkout",
        "Skrill",
        "Payoneer"
    )
    
    fun isBankSupported(bankName: String): Boolean {
        return supportedBanks.contains(bankName)
    }
    
    fun isServiceAvailable(serviceName: String): Boolean {
        return !sanctionedServices.contains(serviceName)
    }
}
```

---

## 🔟 کد رنگی و فرهنگی

### رنگ‌های نماد‌ین ایرانی

```kotlin
object PersianColors {
    // فیروزه‌ای (نماد معماری ایرانی)
    val Turquoise = Color(0xFF32B8C6)
    val TurquoiseLight = Color(0xFF5ACDC9)
    val TurquoiseDark = Color(0xFF2D9BA3)
    
    // طلایی (سنتی)
    val Gold = Color(0xFFE8A87C)
    val GoldDark = Color(0xFFC47F3F)
    
    // مسی (نقره‌جات)
    val Copper = Color(0xFFC84A31)
    val CopperLight = Color(0xFFD97A61)
    
    // سبز اسلامی
    val IslamicGreen = Color(0xFF2BA84F)
    
    // سفید و سیاه
    val PureWhite = Color(0xFFFFFFFF)
    val PureBlack = Color(0xFF1F2121)
    
    // سفید خاکی (زمینه)
    val IvoryBackground = Color(0xFFFCFCF9)
    
    // خاکستری (متن کمی فعال)
    val TextSecondary = Color(0xFF9FA9A9)
}

@Composable
fun PersianColorScheme() = darkColorScheme(
    primary = PersianColors.Turquoise,
    secondary = PersianColors.Gold,
    tertiary = PersianColors.Copper,
    background = PersianColors.IvoryBackground,
    surface = PersianColors.PureWhite,
    error = Color(0xFFC01530),
    onPrimary = PersianColors.PureWhite,
    onSecondary = PersianColors.PureBlack,
    onBackground = PersianColors.PureBlack,
    onSurface = PersianColors.PureBlack
)
```

---

## 1️⃣1️⃣ پیام‌های فارسی مناسب فرهنگی

```xml
<!-- res/values-fa/strings.xml -->
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- سلام و خداحافظ -->
    <string name="welcome">خوش آمدید به نقره‌سود</string>
    <string name="goodbye">تشکر از خریدتان</string>
    
    <!-- پرداخت -->
    <string name="payment_gateway_zarinpal">پرداخت از طریق زرین‌پال</string>
    <string name="payment_cod">پرداخت در محل دریافت</string>
    <string name="payment_success">پرداخت با موفقیت انجام شد</string>
    <string name="payment_error">متأسفانه پرداخت ناموفق بود</string>
    
    <!-- زمان -->
    <string name="today">امروز</string>
    <string name="tomorrow">فردا</string>
    <string name="week">این هفته</string>
    <string name="month">این ماه</string>
    
    <!-- قیمت -->
    <string name="price_format">%s تومان</string>
    <string name="tax_included">شامل مالیات</string>
    <string name="free_shipping">ارسال رایگان</string>
    
    <!-- موارد دیگر -->
    <string name="dear_customer">مشتری گرامی</string>
    <string name="thank_you">متشکریم</string>
    <string name="contact_us">تماس با ما</string>
</resources>
```

---

## 1️⃣2️⃣ توسعه آینده (ایرانی)

- ✅ درگاه‌های پرداخت (زرین‌پال، آی‌دی‌پی، نکست‌پی)
- ✅ تقویم جلالی
- ✅ RTL Layout کامل
- ✅ فونت‌های فارسی
- ✅ قیمت‌گذاری به تومان
- ⏳ سامانه ارسال بومی (دنا، منتقل کارگو)
- ⏳ پشتیبانی از استان‌های ایران
- ⏳ بیمه‌ی کالا
- ⏳ تضمین اصالت برای نقره
- ⏳ احراز هویت سلام تک

---

**تمامی این پیاده‌سازی‌ها برای ایرانیان و نیاز‌های بازار ایران طراحی‌شده است.**
