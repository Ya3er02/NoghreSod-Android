package com.noghre.sod.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.noghre.sod.R

// رنگ‌های ایرانی
object PersianColors {
    // فیروزه‌ای (نماد معماری ایرانی)
    val Turquoise = Color(0xFF32B8C6)
    val TurquoiseLight = Color(0xFF5ACDC9)
    val TurquoiseDark = Color(0xFF2D9BA3)
    
    // طلایی (طلا و سنتی)
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
    val IvoryDark = Color(0xFFF5F5F0)
    
    // خاکستری (متن رانش)
    val TextSecondary = Color(0xFF9FA9A9)
    val TextTertiary = Color(0xFFA7A9A9)
    
    // قرمز (خطا)
    val Red = Color(0xFFC01530)
    val RedLight = Color(0xFFFF5459)
}

// فونت Vazir
val VazirFont = FontFamily(
    Font(R.font.vazir_regular, FontWeight.Normal),
    Font(R.font.vazir_bold, FontWeight.Bold),
    Font(R.font.vazir_medium, FontWeight.Medium),
    Font(R.font.vazir_light, FontWeight.Light)
)

// لاياوت تپوگرافي فارسی
@Composable
fun FarsiTypography(): Typography = Typography(
    // Heading Large
    headlineLarge = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.02).sp
    ),
    // Heading Medium
    headlineMedium = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    // Heading Small
    headlineSmall = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    // Title Large
    titleLarge = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    // Title Medium
    titleMedium = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (0.01).sp
    ),
    // Title Small
    titleSmall = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (0.01).sp
    ),
    // Body Large
    bodyLarge = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = (0.02).sp
    ),
    // Body Medium
    bodyMedium = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (0.02).sp
    ),
    // Body Small
    bodySmall = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (0.04).sp
    ),
    // Label Large
    labelLarge = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = (0.01).sp
    ),
    // Label Medium
    labelMedium = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = (0.02).sp
    ),
    // Label Small
    labelSmall = TextStyle(
        fontFamily = VazirFont,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = (0.02).sp
    )
)

// رنگ شب (Dark Mode)
private val DarkColorScheme = darkColorScheme(
    primary = PersianColors.Turquoise,
    onPrimary = PersianColors.PureWhite,
    primaryContainer = PersianColors.TurquoiseDark,
    onPrimaryContainer = PersianColors.TurquoiseLight,
    
    secondary = PersianColors.Gold,
    onSecondary = PersianColors.PureBlack,
    secondaryContainer = PersianColors.GoldDark,
    onSecondaryContainer = PersianColors.Gold,
    
    tertiary = PersianColors.Copper,
    onTertiary = PersianColors.PureWhite,
    tertiaryContainer = PersianColors.CopperLight,
    onTertiaryContainer = PersianColors.Copper,
    
    error = PersianColors.Red,
    onError = PersianColors.PureWhite,
    errorContainer = PersianColors.RedLight,
    onErrorContainer = PersianColors.Red,
    
    background = PersianColors.PureBlack,
    onBackground = PersianColors.PureWhite,
    
    surface = Color(0xFF26282A),
    onSurface = PersianColors.PureWhite,
    surfaceVariant = Color(0xFF49454E),
    onSurfaceVariant = PersianColors.TextSecondary,
    
    outline = PersianColors.TextTertiary
)

// رنگ روز (Light Mode)
private val LightColorScheme = lightColorScheme(
    primary = PersianColors.Turquoise,
    onPrimary = PersianColors.PureWhite,
    primaryContainer = PersianColors.TurquoiseLight,
    onPrimaryContainer = PersianColors.TurquoiseDark,
    
    secondary = PersianColors.Gold,
    onSecondary = PersianColors.PureBlack,
    secondaryContainer = PersianColors.GoldDark,
    onSecondaryContainer = PersianColors.Gold,
    
    tertiary = PersianColors.Copper,
    onTertiary = PersianColors.PureWhite,
    tertiaryContainer = PersianColors.CopperLight,
    onTertiaryContainer = PersianColors.Copper,
    
    error = PersianColors.Red,
    onError = PersianColors.PureWhite,
    errorContainer = PersianColors.RedLight,
    onErrorContainer = PersianColors.Red,
    
    background = PersianColors.IvoryBackground,
    onBackground = PersianColors.PureBlack,
    
    surface = PersianColors.PureWhite,
    onSurface = PersianColors.PureBlack,
    surfaceVariant = PersianColors.IvoryDark,
    onSurfaceVariant = PersianColors.TextSecondary,
    
    outline = PersianColors.TextTertiary
)

/**
 * نقره‌سود Persian Theme برای Jetpack Compose
 */
@Composable
fun NoghreSodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = FarsiTypography(),
        content = content
    )
}
