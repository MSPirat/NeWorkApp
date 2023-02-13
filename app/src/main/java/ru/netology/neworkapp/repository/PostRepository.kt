package ru.netology.neworkapp.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.Media
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun savePost(post: Post)

    suspend fun saveWithAttachment(post: Post, upload: MediaUpload)

    suspend fun uploadWithContent(upload: MediaUpload): Media

    suspend fun removeById(id: Long)
}