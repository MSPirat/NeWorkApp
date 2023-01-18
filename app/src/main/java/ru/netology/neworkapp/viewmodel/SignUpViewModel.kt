package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.neworkapp.api.ApiService
import ru.netology.neworkapp.dto.Token
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val apiService: ApiService,
) : ViewModel() {

    val data = MutableLiveData<Token>()

    fun registrationUser(login: String, password: String, name: String) {
        viewModelScope.launch {
            try {
                val response = apiService.registrationUser(login, password, name)
                if (!response.isSuccessful) {
                    throw
                    ApiError(response.message())
                }
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                throw NetworkError
            } catch (e: Exception) {
                throw UnknownError()
            }
        }
    }
}