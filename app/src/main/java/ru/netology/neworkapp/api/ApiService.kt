package ru.netology.neworkapp.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.neworkapp.dto.PushToken
import ru.netology.neworkapp.dto.Token
import ru.netology.neworkapp.dto.User

interface ApiService {

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @POST("users/push-tokens")
    suspend fun sendPushToken(@Body pushToken: PushToken): Response<Unit>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun updateUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
    ): Response<Token>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registrationUser(
        @Field("login") login: String,
        @Field("pass") pass: String,
        @Field("name") name: String,
    ): Response<Token>
}