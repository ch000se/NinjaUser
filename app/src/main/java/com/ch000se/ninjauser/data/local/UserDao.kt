package com.ch000se.ninjauser.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface UserDao {

    @Query("SELECT * FROM user_table")
    suspend fun getUsers(): List<UserDbModel>

    @Query("SELECT * FROM user_table WHERE id = :userId")
    suspend fun getUser(userId: String): UserDbModel?

    @Insert(onConflict = REPLACE)
    suspend fun insertUsers(users: List<UserDbModel>)

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    @Transaction
    suspend fun replaceAllUsers(users: List<UserDbModel>) {
        deleteAllUsers()
        insertUsers(users)
    }
}