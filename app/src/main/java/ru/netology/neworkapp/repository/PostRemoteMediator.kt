package ru.netology.neworkapp.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.LoadType.*
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import ru.netology.neworkapp.api.PostApiService
import ru.netology.neworkapp.dao.PostDao
import ru.netology.neworkapp.dao.PostRemoteKeyDao
import ru.netology.neworkapp.db.AppDb
import ru.netology.neworkapp.entity.PostEntity
import ru.netology.neworkapp.entity.PostRemoteKeyEntity
import ru.netology.neworkapp.errors.ApiError
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator(
    private val postDao: PostDao,
    private val postApiService: PostApiService,
    private val postRemoteKeyDao: PostRemoteKeyDao,
    private val appDb: AppDb,
) : RemoteMediator<Int, PostEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PostEntity>,
    ): MediatorResult {
        try {
            val result = when (loadType) {
                REFRESH -> {
//                    apiService.getLatest(state.config.pageSize)
                    postRemoteKeyDao.max()?.let {
                        postApiService.getAfter(it, state.config.pageSize)
                    } ?: postApiService.getLatest(state.config.initialLoadSize)
                }
                APPEND -> {
                    val id = postRemoteKeyDao.min() ?: return MediatorResult.Success(false)
                    postApiService.getBefore(id, state.config.pageSize)
                }
                PREPEND -> {
                    val id = postRemoteKeyDao.max() ?: return MediatorResult.Success(false)
                    postApiService.getAfter(id, state.config.pageSize)
//                    return MediatorResult.Success(false)
                }
            }

            if (!result.isSuccessful) {
                throw ApiError(result.message())
            }

            if (result.body().isNullOrEmpty())
                return MediatorResult.Success(true)

            val body = result.body() ?: throw ApiError(result.message())

            appDb.withTransaction {
                when (loadType) {
                    REFRESH -> {
//                        postDao.clear()
//                        postRemoteKeyDao.insert(
//                            listOf(
//                                PostRemoteKeyEntity(
//                                    PostRemoteKeyEntity.KeyType.AFTER,
//                                    body.first().id,
//                                ),
//                                PostRemoteKeyEntity(
//                                    PostRemoteKeyEntity.KeyType.BEFORE,
//                                    body.last().id,
//                                ),
//                            )
//                        )
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.first().id
                            )
                        )

                        if (postRemoteKeyDao.isEmpty()) {
                            postRemoteKeyDao.insert(
                                PostRemoteKeyEntity(
                                    PostRemoteKeyEntity.KeyType.BEFORE,
                                    body.last().id
                                )
                            )
                        }
                    }

                    APPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.BEFORE,
                                body.last().id,
                            ),
                        )
                    }

                    PREPEND -> {
                        postRemoteKeyDao.insert(
                            PostRemoteKeyEntity(
                                PostRemoteKeyEntity.KeyType.AFTER,
                                body.first().id,
                            ),
                        )
                    }
                }
                postDao.insertPosts(body.map(PostEntity.Companion::fromDto))
            }
            return MediatorResult.Success(body.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        }
    }
}