package com.noghre.sod.presentation.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

object Routes {
    const val HOME = "home"
    const val PRODUCT_DETAIL = "product_detail/{productId}"
    const val PRODUCT_LIST = "product_list"
    const val CART = "cart"
    const val CHECKOUT = "checkout"
    const val ORDERS = "orders"
    const val PROFILE = "profile"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val EDIT_PROFILE = "edit_profile"
    const val SEARCH = "search"

    val productDetailArgs: List<NamedNavArgument> = listOf(
        navArgument("productId") { type = NavType.StringType }
    )

    fun productDetail(productId: String): String = "product_detail/$productId"
    fun search(query: String): String = "search?query=$query"
}
