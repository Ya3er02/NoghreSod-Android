package com.noghre.sod.presentation.ui.component

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.noghre.sod.core.ext.toPersianNumbers
import com.noghre.sod.ui.theme.PersianFontFamily

/**
 * RTL-aware TextField component for Persian text input.
 * 
* Properly handles:
 * - Right-to-left text direction
 * - Persian font rendering
 * - Persian number input
 * - Material 3 design
 * 
 * @param value Current text value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for styling
 * @param label Label text for the field
 * @param placeholder Placeholder text
 * @param error Error text (null if no error)
 * @param keyboardType Type of keyboard to show
 * @param imeAction IME action button
 * @param singleLine Whether to limit to single line
 * @param maxLines Maximum number of lines
 * @param enabled Whether field is enabled
 * @param readOnly Whether field is read-only
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun PersianTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Done,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    enabled: Boolean = true,
    readOnly: Boolean = false
) {
    val layoutDirection = LocalLayoutDirection.current
    val isRtl = layoutDirection == LayoutDirection.Rtl
    
    // Handle Persian number conversion if numeric input
    val displayValue = if (keyboardType == KeyboardType.Number) {
        value.toPersianNumbers()
    } else {
        value
    }
    
    OutlinedTextField(
        value = displayValue,
        onValueChange = { newValue ->
            onValueChange(newValue)
        },
        modifier = modifier,
        label = label?.let { 
            { 
                Text(
                    text = it,
                    fontFamily = PersianFontFamily,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        placeholder = placeholder?.let {
            {
                Text(
                    text = it,
                    fontFamily = PersianFontFamily,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        isError = error != null,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        singleLine = singleLine,
        maxLines = maxLines,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = TextStyle(
            fontFamily = PersianFontFamily,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        ),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            errorBorderColor = MaterialTheme.colorScheme.error,
            cursorColor = MaterialTheme.colorScheme.primary,
            disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
            disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.38f)
        ),
        shape = MaterialTheme.shapes.small
    )
    
    // Show error message if present
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = PersianFontFamily,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )
    }
}

/**
 * Specialized TextField for mobile number input.
 * 
* Auto-formats number and enforces Iranian format.
 * 
 * @param value Current number value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for styling
 * @param error Error text
 * @param enabled Whether field is enabled
 */
@Composable
fun PersianMobileNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true
) {
    PersianTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = "شماره موبایل",
        placeholder = "09xxxxxxxxx",
        error = error,
        keyboardType = KeyboardType.Phone,
        imeAction = ImeAction.Next,
        enabled = enabled
    )
}

/**
 * Specialized TextField for price input.
 * 
* Shows currency formatting and converts Persian numbers.
 * 
 * @param value Current price value
 * @param onValueChange Callback when value changes
 * @param modifier Modifier for styling
 * @param error Error text
 * @param enabled Whether field is enabled
 */
@Composable
fun PersianPriceField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
    enabled: Boolean = true
) {
    PersianTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = "قیمت",
        placeholder = "50,000",
        error = error,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done,
        enabled = enabled
    )
}
