package ru.netology.neworkapp.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.netology.neworkapp.api.UserApiService
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.dto.Token
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.model.PhotoModel
import ru.netology.neworkapp.model.StateModel
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val userApiService: UserApiService,
) : ViewModel() {

    val data = MutableLiveData<Token>()

    private val noPhoto = PhotoModel()

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun registrationUser(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                val response = userApiService.registrationUser(
                    login.toRequestBody("text/plain".toMediaType()),
                    password.toRequestBody("text/plain".toMediaType()),
                    name.toRequestBody("text/plain".toMediaType()),
                    photo.value?.uri?.toFile()?.let {
                        val upload = MediaUpload(it)

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
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                _dataState.postValue(StateModel(error = true))
            } catch (e: Exception) {
                _dataState.postValue(StateModel(loginError = true))
            }
        }
        _photo.value = noPhoto
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }
}