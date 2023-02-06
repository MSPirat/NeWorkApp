package ru.netology.neworkapp.dao

import androidx.room.*
import ru.netology.neworkapp.entity.WallRemoteKeyEntity

@Dao
interface WallRemoteKeyDao {
    @Query("SELECT COUNT(*) == 0 FROM WallRemoteKeyEntity")
    suspend fun isEmpty(): Boolean

    @Query("SELECT MAX(`key`) FROM WallRemoteKeyEntity")
    suspend fun max(): Long?

    @Query("SELECT MIN(`key`) FROM WallRemoteKeyEntity")
    suspend fun min(): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallRemoteKeyEntity: WallRemoteKeyEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(wallRemoteKeyEntity: List<WallRemoteKeyEntity>)

    @Query("DELETE FROM WallRemoteKeyEntity")
    suspend fun removeAll()
}