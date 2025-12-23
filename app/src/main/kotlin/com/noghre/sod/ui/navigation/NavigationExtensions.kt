package com.noghre.sod.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptions

/**
 * Navigation helper extensions
 */

fun NavController.navigateToProductDetail(productId: String) {
    navigate(Screen.ProductDetail.createRoute(productId))
}

fun NavController.navigateToOrderDetail(orderId: String) {
    navigate(Screen.OrderDetail.createRoute(orderId))
}

fun NavController.navigateToOtp(phone: String) {
    navigate(Screen.OtpVerification.createRoute(phone))
}

fun NavController.navigateToAddEditAddress(addressId: String? = null) {
    navigate(Screen.AddEditAddress.createRoute(addressId))
}

fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun NavController.navigateHome() {
    navigateSingleTop(Screen.Home.route)
}

fun NavController.navigateToProfile() {
    navigateSingleTop(Screen.Profile.route)
}

fun NavController.popBackStackOrFinish() {
    if (!popBackStack()) {
        // No back stack, close app
    }
}

fun NavController.navigateToLogin() {
    navigate(Screen.Login.route) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
