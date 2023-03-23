package ru.netology.neworkapp.repository

import android.net.Uri
import androidx.core.net.toFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.neworkapp.api.UserApiService
import ru.netology.neworkapp.dto.PhotoUpload
import ru.netology.neworkapp.dto.Token
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class SignUpRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService,
) : SignUpRepository {
    override suspend fun registrationUser(
        login: String,
        password: String,
        name: String,
        image: Uri,
    ): Token {
        try {
            val response = userApiService.registrationUser(
                login.toRequestBody("text/plain".toMediaType()),
                password.toRequestBody("text/plain".toMediaType()),
                name.toRequestBody("text/plain".toMediaType()),
                image.toFile().let {
                    val upload = PhotoUpload(it)
                    MultipartBody.Part.createFormData(
                        "file",
                        upload.file.name,
                        upload.file.asRequestBody()
                    )
                }
            )
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