package com.noghre.sod.utils

object InputValidators {

    fun isValidEmail(email: String): Boolean {
        return email.matches(Regex("^[A-Za-z0-9+_.-]+@(.+)$"))
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8 &&
                password.any { it.isUpperCase() } &&
                password.any { it.isDigit() } &&
                password.any { !it.isLetterOrDigit() }
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.matches(Regex("^989\\d{9}$")) || // Iranian format
                phone.matches(Regex("^\\+989\\d{9}$"))
    }

    fun isValidPostalCode(postalCode: String): Boolean {
        return postalCode.matches(Regex("^\\d{10}$"))
    }

    fun isValidFirstName(firstName: String): Boolean {
        return firstName.isNotBlank() && firstName.length >= 2
    }

    fun isValidLastName(lastName: String): Boolean {
        return lastName.isNotBlank() && lastName.length >= 2
    }

    fun isValidCreditCard(cardNumber: String): Boolean {
        return cardNumber.replace(" ", "").length in 13..19 &&
                cardNumber.all { it.isDigit() || it.isWhitespace() }
    }

    fun isValidDiscountCode(code: String): Boolean {
        return code.length in 4..20 && code.matches(Regex("^[A-Z0-9-]*$"))
    }
}
