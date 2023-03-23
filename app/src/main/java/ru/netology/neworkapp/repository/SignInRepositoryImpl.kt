package ru.netology.neworkapp.repository

import ru.netology.neworkapp.api.UserApiService
import ru.netology.neworkapp.dto.Token
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class SignInRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
) : SignInRepository {
    override suspend fun authorizationUser(login: String, password: String): Token {
        try {
            val response = userApiService.updateUser(login, password)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            return response.body() ?: throw ApiError(response.message())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}