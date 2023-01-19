package ru.netology.neworkapp.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @Multipart
    @POST("users/registration")
    suspend fun registrationUser(
        @Part("login") login: RequestBody,
        @Part("pass") pass: RequestBody,
        @Part("name") name: RequestBody,
        @Part image: MultipartBody.Part?,
    ): Response<Token>
}