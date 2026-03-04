package com.ch000se.ninjauser.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [UserDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class NinjaDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}