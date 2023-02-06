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
        UserEntity::class,
    ],
    version = 4,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
    abstract fun postRemoteKeyDao(): PostRemoteKeyDao
    abstract fun userDao(): UserDao
}