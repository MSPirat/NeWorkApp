package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.neworkapp.api.UserApiService
import ru.netology.neworkapp.dto.Event
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.dto.User
import ru.netology.neworkapp.model.StateModel
import ru.netology.neworkapp.repository.UserRepository
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userApiService: UserApiService,
    private val userRepository: UserRepository,
) : ViewModel() {

    val data: LiveData<List<User>> =
        userRepository.data
            .asLiveData(Dispatchers.Default)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _user = MutableLiveData<User>()
    val user: LiveData<User>
        get() = _user

    private val _userIds = MutableLiveData<Set<Long>>()
    val userIds: LiveData<Set<Long>>
        get() = _userIds

    init {
        getUsers()
    }

    private fun getUsers() = viewModelScope.launch {
        try {
            userRepository.getAll()
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    fun getUserById(id: Long) = viewModelScope.launch {
        try {
            val response = userApiService.getUserById(id)
            if (response.isSuccessful) {
                _user.value = response.body()
            }
        } catch (e: IOException) {
            _dataState.postValue(StateModel(error = true))
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    fun getMentionIds(post: Post) =
        viewModelScope.launch {
            _userIds.value = post.mentionIds
        }

    fun getLikeOwnerIds(post: Post) =
        viewModelScope.launch {
            _userIds.value = post.likeOwnerIds
        }

    fun getSpeakerIds(event: Event) =
        viewModelScope.launch {
            _userIds.value = event.speakerIds
        }

    fun getLikeOwnerIds(event: Event) =
        viewModelScope.launch {
            _userIds.value = event.likeOwnerIds
        }

    fun getParticipants(event: Event) =
        viewModelScope.launch {
            _userIds.value = event.participantsIds
        }
}