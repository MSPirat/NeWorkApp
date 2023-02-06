package ru.netology.neworkapp.repository

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.FeedItem

interface WallRepository {

    val data: Flow<PagingData<FeedItem>>

    suspend fun load(id: Long)
}