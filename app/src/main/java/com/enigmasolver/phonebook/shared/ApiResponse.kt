package com.enigmasolver.phonebook.shared

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val success: Boolean,
    val status: Int,
    val messages: List<String>?,
    @SerializedName("data")
    val data: T
)