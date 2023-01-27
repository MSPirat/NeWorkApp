package ru.netology.neworkapp.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    suspend fun savePost(post: Post)
}