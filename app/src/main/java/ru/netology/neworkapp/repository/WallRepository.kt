package ru.netology.neworkapp.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.Post

interface WallRepository {

    fun loadUserWall(userId: Long): Flow<PagingData<Post>>
}