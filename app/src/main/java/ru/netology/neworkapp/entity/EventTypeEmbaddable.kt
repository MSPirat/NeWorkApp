package ru.netology.neworkapp.entity

import ru.netology.neworkapp.enumeration.EventType

data class EventTypeEmbeddable(
    val eventType: String,
) {
    fun toDto() = EventType.valueOf(eventType)

    companion object {
        fun fromDto(dto: EventType) = EventTypeEmbeddable(dto.name)
    }
}