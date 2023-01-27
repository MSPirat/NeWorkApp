package ru.netology.neworkapp.dto

import ru.netology.neworkapp.enumeration.AttachmentType

data class Attachment(
    val url: String,
    val type: AttachmentType,
)
