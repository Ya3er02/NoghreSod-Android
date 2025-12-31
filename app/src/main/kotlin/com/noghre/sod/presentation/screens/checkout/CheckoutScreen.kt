package com.noghre.sod.presentation.screens.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.noghre.sod.presentation.components.PersianButton
import com.noghre.sod.presentation.components.PersianTextField

/**
 * Checkout Screen - Order finalization.
 * 
* Shows order summary, delivery address, shipping method.
 * Payment method selection and coupon code.
 * 
 * @author NoghreSod Team
 * @version 1.0.0
 */
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onOrderSuccess: (String) -> Unit = {}
) {
    val uiState by viewModel.checkoutUiState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top app bar
        TopAppBar(
            title = { Text("تومان خرید") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        )

        if (error != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error ?: "خطای نامشخصی",
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        CheckoutContent(
            uiState = uiState,
            isLoading = isLoading,
            onAddressEdit = { /* TODO: Show address editor */ },
            onShippingMethodSelect = { viewModel.selectShippingMethod(it) },
            onPaymentMethodSelect = { viewModel.selectPaymentMethod(it) },
            onCouponApply = { viewModel.applyCoupon(it) },
            onPlaceOrder = { viewModel.placeOrder() }
        )
    }
}

@Composable
fun CheckoutContent(
    uiState: CheckoutUiState,
    isLoading: Boolean,
    onAddressEdit: () -> Unit,
    onShippingMethodSelect: (String) -> Unit,
    onPaymentMethodSelect: (String) -> Unit,
    onCouponApply: (String) -> Unit,
    onPlaceOrder: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Order summary
        item {
            OrderSummaryCard(
                items = uiState.cartItems,
                subtotal = uiState.subtotal,
                tax = uiState.tax
            )
        }

        // Delivery address
        item {
            DeliveryAddressCard(
                address = uiState.address,
                onEdit = onAddressEdit
            )
        }

        // Shipping method
        item {
            ShippingMethodCard(
                methods = uiState.shippingMethods,
                selected = uiState.selectedShippingMethod,
                onSelect = onShippingMethodSelect,
                shippingCost = uiState.shippingCost
            )
        }

        // Payment method
        item {
            PaymentMethodCard(
                methods = uiState.paymentMethods,
                selected = uiState.selectedPaymentMethod,
                onSelect = onPaymentMethodSelect
            )
        }

        // Coupon code
        item {
            CouponCard(
                onApplyCoupon = onCouponApply,
                discount = uiState.discount
            )
        }

        // Order total
        item {
            OrderTotalCard(
                subtotal = uiState.subtotal,
                shipping = uiState.shippingCost,
                tax = uiState.tax,
                discount = uiState.discount,
                total = uiState.total
            )
        }

        // Place order button
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PersianButton(
                    text = if (isLoading) "در حال پرداخت..." else "مربوط سفارش",
                    onClick = onPlaceOrder,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && uiState.address != null && uiState.selectedPaymentMethod != null
                )
            }
        }
    }
}

@Composable
fun OrderSummaryCard(
    items: List<Any>,
    subtotal: Double,
    tax: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "خلاصه سفارش",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "تعداد اشیاء:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = items.size.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Divider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "مبلغ واحد:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$subtotal ریال",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "لا بها:",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$tax ریال",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun DeliveryAddressCard(
    address: String?,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "آدرس برااس تحویل",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = address ?: "برای تعیین کلیک کنید",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun ShippingMethodCard(
    methods: List<ShippingMethod>,
    selected: String?,
    onSelect: (String) -> Unit,
    shippingCost: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "روش ارسال",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            methods.forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(method.id) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selected == method.id,
                        onClick = { onSelect(method.id) }
                    )
                    Column {
                        Text(
                            text = method.name,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = method.estimatedDays,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "${method.cost} ریال",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun PaymentMethodCard(
    methods: List<PaymentMethod>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "روش پرداخت",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            methods.forEach { method ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(method.id) }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RadioButton(
                        selected = selected == method.id,
                        onClick = { onSelect(method.id) }
                    )
                    Text(
                        text = method.name,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CouponCard(
    onApplyCoupon: (String) -> Unit,
    discount: Double = 0.0
) {
    var couponCode by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "کد تخفیف",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PersianTextField(
                    value = couponCode,
                    onValueChange = { couponCode = it },
                    placeholder = "کد تخفیف را وارد کنید",
                    modifier = Modifier.weight(1f)
                )
                PersianButton(
                    text = "اعمال",
                    onClick = { onApplyCoupon(couponCode) }
                )
            }

            if (discount > 0) {
                Text(
                    text = "تخفیف: $discount ریال",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun OrderTotalCard(
    subtotal: Double,
    shipping: Double,
    tax: Double,
    discount: Double,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("مبلغ واحد:")
                Text("$subtotal ریال")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("هزینه ارسال:")
                Text("$shipping ریال")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("لا به:")
                Text("$tax ریال")
            }
            if (discount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("تخفیف:")
                    Text("-$discount ریال")
                }
            }
            Divider()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "کل مبلغ:",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$total ریال",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// Data classes
data class ShippingMethod(
    val id: String,
    val name: String,
    val estimatedDays: String,
    val cost: Double
)

data class PaymentMethod(
    val id: String,
    val name: String
)

data class CheckoutUiState(
    val cartItems: List<Any> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shippingCost: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val address: String? = null,
    val selectedShippingMethod: String? = null,
    val selectedPaymentMethod: String? = null,
    val shippingMethods: List<ShippingMethod> = emptyList(),
    val paymentMethods: List<PaymentMethod> = emptyList()
)
