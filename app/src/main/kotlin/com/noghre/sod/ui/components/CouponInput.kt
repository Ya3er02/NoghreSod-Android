package com.noghre.sod.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.noghre.sod.ui.theme.Spacing

@Composable
fun CouponInput(
    onApplyCoupon: (String) -> Unit,
    appliedCoupon: String?,
    onRemoveCoupon: () -> Unit,
    isLoading: Boolean = false
) {
    val couponCode = remember { mutableStateOf("") }

    if (appliedCoupon == null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.small)
        ) {
            OutlinedTextField(
                value = couponCode.value,
                onValueChange = { couponCode.value = it },
                label = { Text("کد تخفیف") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = Spacing.small)
            )
            Button(
                onClick = { onApplyCoupon(couponCode.value) },
                enabled = !isLoading,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("اعمال")
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.small),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(كوبون: $appliedCoupon", modifier = Modifier.weight(1f))
            IconButton(onClick = onRemoveCoupon) {
                Icon(Icons.Default.Close, contentDescription = "Remove coupon")
            }
        }
    }
