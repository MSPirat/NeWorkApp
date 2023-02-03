package ru.netology.neworkapp.repository

import androidx.paging.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.neworkapp.api.PostApiService
import ru.netology.neworkapp.dao.PostDao
import ru.netology.neworkapp.dao.PostRemoteKeyDao
import ru.netology.neworkapp.db.AppDb
import ru.netology.neworkapp.dto.Ad
import ru.netology.neworkapp.dto.FeedItem
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.entity.PostEntity
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject
import kotlin.random.Random

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postApiService: PostApiService,
    postRemoteKeyDao: PostRemoteKeyDao,
    appDb: AppDb,
) : PostRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val data: Flow<PagingData<FeedItem>> = Pager(
        config = PagingConfig(pageSize = 10, enablePlaceholders = false),
        pagingSourceFactory = { postDao.getPagingSource() },
        remoteMediator = PostRemoteMediator(
            postApiService = postApiService,
            postDao = postDao,
            postRemoteKeyDao = postRemoteKeyDao,
            appDb = appDb,
        )
    ).flow
        .map {
            it.map(PostEntity::toDto)
                .insertSeparators { previous, _ ->
                    if (previous?.id?.rem(5) == 0L) {
                        Ad(Random.nextLong(), "figma.jpg")
                    } else {
                        null
                    }
                }
        }

    override suspend fun savePost(post: Post) {
        try {
            postDao.savePost(PostEntity.fromDto(post))
            val response = postApiService.savePost(post)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            postDao.insertPost(PostEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}