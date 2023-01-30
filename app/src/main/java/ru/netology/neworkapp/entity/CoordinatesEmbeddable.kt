package ru.netology.neworkapp.entity

import ru.netology.neworkapp.dto.Coordinates

data class CoordinatesEmbeddable(
    val latitude: Double?,
    val longitude: Double?,
) {
    fun toDto() = Coordinates(latitude, longitude)

    companion object {
        fun fromDto(dto: Coordinates?) = dto?.let {
            CoordinatesEmbeddable(it.lat, it.long)
        }
    }
}
