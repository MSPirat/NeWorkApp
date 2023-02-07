package ru.netology.neworkapp.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.Post

interface PostRepository {
    val data: Flow<PagingData<Post>>

    suspend fun savePost(post: Post)

    suspend fun deleteById(id: Long)
}