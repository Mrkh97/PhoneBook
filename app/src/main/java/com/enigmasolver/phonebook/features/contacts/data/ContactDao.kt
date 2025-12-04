package com.enigmasolver.phonebook.features.contacts.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.enigmasolver.phonebook.features.contacts.domain.ContactSearchEntity

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContactSearch(contactSearch: ContactSearchEntity)

    @Query("SELECT * FROM contactSearches")
    suspend fun listContactSearches(): List<ContactSearchEntity>

    @Query("DELETE FROM contactSearches WHERE id = :id")
    suspend fun deleteContactSearchById(id: Int)

    @Query("DELETE FROM contactSearches")
    suspend fun clearTable()

}