package ru.netology.neworkapp.model

import android.net.Uri
import ru.netology.neworkapp.enumeration.AttachmentType
import java.io.InputStream

data class MediaModel(
    val uri: Uri? = null,
    val inputStream: InputStream? = null,
    val type: AttachmentType? = null,
)