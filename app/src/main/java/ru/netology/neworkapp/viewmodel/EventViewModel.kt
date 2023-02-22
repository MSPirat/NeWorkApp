package ru.netology.neworkapp.viewmodel

import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.dto.Event
import ru.netology.neworkapp.dto.MediaUpload
import ru.netology.neworkapp.enumeration.EventType
import ru.netology.neworkapp.model.PhotoModel
import ru.netology.neworkapp.model.StateModel
import ru.netology.neworkapp.repository.EventRepository
import ru.netology.neworkapp.utils.SingleLiveEvent
import javax.inject.Inject

private val empty = Event(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2023-01-27T17:00:00.000Z",
    datetime = "2023-01-27T17:00:00.000Z",
    type = EventType.ONLINE
)

private val noPhoto = PhotoModel()

@ExperimentalCoroutinesApi
@HiltViewModel
class EventViewModel @Inject constructor(
    private val eventRepository: EventRepository,
    appAuth: AppAuth,
) : ViewModel() {

    private val cached = eventRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Event>> =
        appAuth.authStateFlow
            .flatMapLatest { (myId, _) ->
                cached.map { pagingData ->
                    pagingData.map { event ->
                        event.copy(
                            ownedByMe = event.authorId == myId,
                            likedByMe = event.likeOwnerIds.contains(myId),
                            participatedByMe = event.participantsIds.contains(myId)
                        )
                    }
                }
            }

    val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _eventCreated = SingleLiveEvent<Unit>()
    val eventCreated: LiveData<Unit>
        get() = _eventCreated

    private val _photo = MutableLiveData(noPhoto)
    val photo: LiveData<PhotoModel>
        get() = _photo

    private val scope = MainScope()

    fun save() {
        edited.value?.let { event ->
            viewModelScope.launch {
                try {
                    when (_photo.value) {
                        noPhoto ->
                            eventRepository.saveEvent(event)
                        else ->
                            _photo.value?.uri?.let {
                                MediaUpload(it.toFile())
                            }?.let {
                                eventRepository.saveWithAttachment(event, it)
                            }
                    }
                    _dataState.value = StateModel()
                    _eventCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }
            }
        }
        edited.value = empty
        _photo.value = noPhoto
    }

    fun changeContent(content: String, date: String) {
        edited.value?.let {
            val text = content.trim()
            val dateText = content.trim()

            if (edited.value?.content != text) {
                edited.value = edited.value?.copy(content = text)
            }
            if (edited.value?.datetime != date)
                edited.value = edited.value?.copy(datetime = dateText)
        }
    }

    fun changePhoto(uri: Uri?) {
        _photo.value = PhotoModel(uri)
    }

    fun removeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.removeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun edit(event: Event) {
        edited.value = event
    }

    fun likeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.likeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun unlikeById(id: Long) = viewModelScope.launch {
        try {
            eventRepository.unlikeById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun participate(id: Long) = viewModelScope.launch {
        try {
            eventRepository.participate(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun doNotParticipate(id: Long) = viewModelScope.launch {
        try {
            eventRepository.doNotParticipate(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }


    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}
