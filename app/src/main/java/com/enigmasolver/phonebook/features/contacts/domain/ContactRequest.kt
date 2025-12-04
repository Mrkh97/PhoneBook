package com.enigmasolver.phonebook.features.contacts.domain

data class ContactRequest(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val profileImageUrl: String?
)
