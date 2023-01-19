package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.netology.neworkapp.api.ApiService
import ru.netology.neworkapp.dto.User
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val apiService: ApiService,
) : ViewModel() {

    private val _data = MutableLiveData<List<User>>()

    val data: LiveData<List<User>>
        get() = _data

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        try {
            val response = apiService.getUsers()
            if (response.isSuccessful) {
                _data.value = response.body()
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}