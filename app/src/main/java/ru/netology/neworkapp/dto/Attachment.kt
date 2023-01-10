package ru.netology.neworkapp.dto

import ru.netology.neworkapp.enum.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
