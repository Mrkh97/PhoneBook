package com.enigmasolver.phonebook.shared.utils

object ValidationUtils {
    fun isValidPhone(phone: String): Boolean {
        val phoneRegex = Regex("^\\+?[0-9]{10,14}\$")
        return phone.isNotBlank() && phoneRegex.matches(phone)
    }
}