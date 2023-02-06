package ru.netology.neworkapp.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.neworkapp.dto.Post

interface PostApiService {

    @GET("posts/{id}/before")
    suspend fun getPostBefore(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("posts/{id}/after")
    suspend fun getPostAfter(
        @Path("id") id: Long,
        @Query("count") count: Int,
    ): Response<List<Post>>

    @GET("posts/latest")
    suspend fun getPostLatest(@Query("count") count: Int): Response<List<Post>>

    @POST("posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeById(@Path("id") id: Long): Response<Unit>
}