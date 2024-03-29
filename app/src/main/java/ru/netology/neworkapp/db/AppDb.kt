package ru.netology.neworkapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.neworkapp.dao.*
import ru.netology.neworkapp.entity.*
import ru.netology.neworkapp.utils.Converters

@Database(
    entities = [
        PostEntity::class,
        PostRemoteKeyEntity::class,
        EventEntity::class,
        EventRemoteKeyEntity::class,
        UserEntity::class,
        JobEntity::class,
    ],
    version = 11,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun eventDao(): EventDao
    abstract fun eventRemoteKeyDao(): EventRemoteKeyDao
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
}