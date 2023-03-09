package ru.netology.neworkapp.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.Media
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.enumeration.AttachmentType

interface PostRepository {

    val data: Flow<PagingData<Post>>

    suspend fun savePost(post: Post)

    suspend fun saveWithAttachment(
        post: Post,
        upload: MediaUpload,
        type: AttachmentType,
    )

    suspend fun uploadWithContent(upload: MediaUpload): Media

    suspend fun removeById(id: Long)

    suspend fun likeById(id: Long)

    suspend fun unlikeById(id: Long)
}