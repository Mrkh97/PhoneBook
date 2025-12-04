package com.enigmasolver.phonebook.features.contacts.data

import com.enigmasolver.phonebook.features.contacts.domain.ContactSearchEntity
import javax.inject.Inject

class LocalContactRepository @Inject constructor(
    private val db: ContactDao
) {
    suspend fun addSearchTerm(searchTerm: String) {
        db.insertContactSearch(ContactSearchEntity(0, searchTerm))
    }

    suspend fun removeSearchTerm(id: Int) {
        db.deleteContactSearchById(id)
    }

    suspend fun listSearchTerms(): List<ContactSearchEntity> {
        return db.listContactSearches()
    }

    suspend fun clearAllSearchTerms() {
        return db.clearTable();
    }

}