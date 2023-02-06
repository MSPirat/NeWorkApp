package ru.netology.neworkapp.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.neworkapp.api.WallApiService
import ru.netology.neworkapp.dao.UserDao
import ru.netology.neworkapp.dao.WallDao
import ru.netology.neworkapp.dao.WallRemoteKeyDao
import ru.netology.neworkapp.db.AppDb
import ru.netology.neworkapp.dto.FeedItem
import ru.netology.neworkapp.entity.WallEntity
import ru.netology.neworkapp.entity.toWallEntity
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class WallRepositoryImpl @Inject constructor(
    private val wallDao: WallDao,
    private val wallApiService: WallApiService,
    wallRemoteKeyDao: WallRemoteKeyDao,
    appDb: AppDb,
    userDao: UserDao,
) : WallRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        remoteMediator = WallRemoteMediator(
            wallApiService = wallApiService,
            wallDao = wallDao,
            wallRemoteKeyDao = wallRemoteKeyDao,
            appDb = appDb,
            userDao = userDao
        ),
        pagingSourceFactory = { wallDao.getPagingSource() }
    ).flow
        .map {
            it.map(WallEntity::toDto)
        }

    override suspend fun load(id: Long) {
        try {
            val response = wallApiService.getWallLatest(id, 10)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            wallDao.insertPosts(body.toWallEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}