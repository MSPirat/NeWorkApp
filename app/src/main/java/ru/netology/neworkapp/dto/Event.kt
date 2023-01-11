package ru.netology.neworkapp.dto

import ru.netology.neworkapp.enum.EventType
import java.time.Instant

data class Event(
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val dateTime: Instant? = null,
    val published: Instant? = null,
    val coordinates: Coordinates? = null,
    val type: EventType,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    val speakerIds: Set<Long> = emptySet(),
    val participantsIds: Set<Long> = emptySet(),
    val participatedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val link: String? = null,
)
