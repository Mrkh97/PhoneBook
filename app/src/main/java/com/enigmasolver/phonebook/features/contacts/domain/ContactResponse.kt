package com.enigmasolver.phonebook.features.contacts.domain

import com.google.gson.annotations.SerializedName

data class ContactResponse(
    @SerializedName("id")
    val id: String,

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("firstName")
    val firstName: String,

    @SerializedName("lastName")
    val lastName: String,

    @SerializedName("phoneNumber")
    val phoneNumber: String,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String
)