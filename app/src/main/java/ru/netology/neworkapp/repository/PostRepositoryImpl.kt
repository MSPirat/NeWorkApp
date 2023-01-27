package ru.netology.neworkapp.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.neworkapp.api.PostApiService
import ru.netology.neworkapp.dao.PostDao
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.entity.PostEntity
import ru.netology.neworkapp.entity.toDto
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val postDao: PostDao,
    private val postApiService: PostApiService,
) : PostRepository {

    override val data: Flow<List<Post>> =
        postDao.getAllPosts()
            .map { it.toDto() }
            .flowOn(Dispatchers.Default)

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
            throw UnknownError()
        }
    }
}