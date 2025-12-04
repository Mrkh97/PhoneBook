package com.enigmasolver.phonebook.features.contacts.domain

import com.google.gson.annotations.SerializedName

data class UploadImageResponse(
    @SerializedName("imageUrl")
    val imageUrl:String
)
