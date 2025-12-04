package com.enigmasolver.phonebook.features.contacts.domain

import com.google.gson.annotations.SerializedName

data class ListContactResponse(
    @SerializedName("users")
    val users: List<ContactResponse>
)
