package ru.netology.neworkapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.neworkapp.api.WallApiService
import ru.netology.neworkapp.dao.*
import ru.netology.neworkapp.db.AppDb
import ru.netology.neworkapp.entity.WallEntity
import ru.netology.neworkapp.entity.WallRemoteKeyEntity
import ru.netology.neworkapp.errors.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class WallRemoteMediator(
    private val wallDao: WallDao,
    private val wallApiService: WallApiService,
    private val wallRemoteKeyDao: WallRemoteKeyDao,
    private val appDb: AppDb,
    private val userDao: UserDao,
) : RemoteMediator<Int, WallEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, WallEntity>,
    ): MediatorResult {
        try {
            val userId = userDao.id()
            val result = when (loadType) {
                REFRESH ->
                    wallApiService.getWallLatest(userId, state.config.initialLoadSize)

                APPEND -> {
                    val id = wallRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    wallApiService.getWallBefore(userId, id, state.config.pageSize)
                }

                PREPEND -> {
                    val id = wallRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    wallApiService.getWallAfter(userId, id, state.config.pageSize)
                }
            }

            if (!result.isSuccessful) {
                throw ApiError(result.message())
            }

            val body = result.body() ?: throw ApiError(result.message())

            appDb.withTransaction {
                when (loadType) {
                    REFRESH -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                WallRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )

                        if (wallRemoteKeyDao.isEmpty()) {
                            wallRemoteKeyDao.insert(
                                WallRemoteKeyEntity(
                                    WallRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        }
                    }

                    APPEND -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                WallRemoteKeyEntity.KeyType.BEFORE,
                                body.last().id,
                            ),
                        )
                    }

                    PREPEND -> {
                        wallRemoteKeyDao.insert(
                            WallRemoteKeyEntity(
                                WallRemoteKeyEntity.KeyType.AFTER,
                                body.first().id,
                            ),
                        )
                    }
                }
                wallDao.insertPosts(body.map(WallEntity::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}