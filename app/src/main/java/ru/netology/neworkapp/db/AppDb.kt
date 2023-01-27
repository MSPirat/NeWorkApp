package ru.netology.neworkapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.netology.neworkapp.dao.PostDao
import ru.netology.neworkapp.entity.PostEntity
import ru.netology.neworkapp.utils.Converters

@Database(
    entities = [PostEntity::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun postDao(): PostDao
}