package com.enigmasolver.phonebook.features.contacts.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contactSearches")
data class ContactSearchEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val searchTerm: String
)
