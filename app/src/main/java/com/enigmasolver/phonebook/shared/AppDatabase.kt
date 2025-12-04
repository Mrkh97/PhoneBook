package com.enigmasolver.phonebook.shared

import androidx.room.Database
import androidx.room.RoomDatabase
import com.enigmasolver.phonebook.features.contacts.data.ContactDao
import com.enigmasolver.phonebook.features.contacts.domain.ContactSearchEntity

@Database(
    entities = [ContactSearchEntity::class],
    version = 1,
//    autoMigrations = [
//        AutoMigration(from = 1, to = 2)
//    ],
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
}