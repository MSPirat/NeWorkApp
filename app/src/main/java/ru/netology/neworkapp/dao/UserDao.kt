package ru.netology.neworkapp.dao

import androidx.room.*
import androidx.room.OnConflictStrategy
import ru.netology.neworkapp.entity.UserEntity

@Dao
interface UserDao {

    @Query("SELECT id FROM UserEntity")
    suspend fun id(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Query("DELETE FROM UserEntity")
    suspend fun removeAll()
}