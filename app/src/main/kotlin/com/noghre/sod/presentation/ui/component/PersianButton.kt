package com.noghre.sod.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.noghre.sod.core.ext.bounceClick
import com.noghre.sod.ui.theme.PersianFontFamily

/**
 * Primary button component with RTL support.
 * 
* Material 3 primary button with Persian font and haptic feedback.
 * 
 * @param text Button text
 * @param onClick Click callback
 * @param modifier Modifier for styling
 * @param enabled Whether button is enabled
 * @param isLoading Show loading spinner
 * @param leadingIcon Icon to show at start (RTL-aware)
 * @param trailingIcon Icon to show at end (RTL-aware)
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun PersianButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null
) {
    val layoutDirection = LocalLayoutDirection.current
    
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading icon (start in RTL, end in LTR)
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Text(
                text = text,
                fontFamily = PersianFontFamily,
                style = MaterialTheme.typography.labelLarge
            )
            
            // Trailing icon (end in RTL, start in LTR)
            if (trailingIcon != null) {
                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Secondary/Outline button component with RTL support.
 * 
* Material 3 outlined button with Persian font.
 * 
 * @param text Button text
 * @param onClick Click callback
 * @param modifier Modifier for styling
 * @param enabled Whether button is enabled
 * @param isLoading Show loading spinner
 * @param leadingIcon Icon to show at start
 * @param trailingIcon Icon to show at end
 */
@Composable
fun PersianOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: Painter? = null,
    trailingIcon: Painter? = null
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 2.dp
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    painter = leadingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
            
            Text(
                text = text,
                fontFamily = PersianFontFamily,
                style = MaterialTheme.typography.labelLarge
            )
            
            if (trailingIcon != null) {
                Icon(
                    painter = trailingIcon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * Text/Minimal button component with RTL support.
 * 
* Material 3 text button with Persian font.
 * 
 * @param text Button text
 * @param onClick Click callback
 * @param modifier Modifier for styling
 * @param enabled Whether button is enabled
 * @param color Text color
 */
@Composable
fun PersianTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary
) {
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.textButtonColors(
            contentColor = color,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        Text(
            text = text,
            fontFamily = PersianFontFamily,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
