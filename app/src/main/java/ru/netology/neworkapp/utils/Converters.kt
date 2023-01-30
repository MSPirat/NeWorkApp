package ru.netology.neworkapp.utils

import androidx.room.TypeConverter
import ru.netology.neworkapp.enumeration.AttachmentType

class Converters {

    @TypeConverter
    fun toAttachmentType(value: String) = enumValueOf<AttachmentType>(value)

    @TypeConverter
    fun fromAttachmentType(value: AttachmentType) = value.name

    @TypeConverter
    fun fromSet(set: Set<Long>): String = set.joinToString("-")

    @TypeConverter
    fun toSet(data: String): Set<Long> = data.split("-").map { it.toLong() }.toSet()
}