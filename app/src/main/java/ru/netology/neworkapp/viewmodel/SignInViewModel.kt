package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.neworkapp.api.UserApiService
import ru.netology.neworkapp.dto.Token
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.model.StateModel
//import ru.netology.neworkapp.repository.SignInRepository
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
//    private val signInRepository: SignInRepository,
    private val userApiService: UserApiService,
) : ViewModel() {

    val data = MutableLiveData<Token>()

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun authorizationUser(login: String, password: String) {
        viewModelScope.launch {
            _dataState.postValue(StateModel(loading = true))
            try {
//                data.value = signInRepository.authorizationUser(login, password)
//                _dataState.postValue(StateModel())
                val response = userApiService.updateUser(login, password)
                if (!response.isSuccessful) {
                    throw ApiError(response.message())
                }
                _dataState.postValue(StateModel())
                val body = response.body() ?: throw ApiError(response.message())
                data.value = Token(body.id, body.token)
            } catch (e: IOException) {
                _dataState.postValue(StateModel(error = true))
            } catch (e: Exception) {
                _dataState.postValue(StateModel(loginError = true))
            }
        }
    }
}