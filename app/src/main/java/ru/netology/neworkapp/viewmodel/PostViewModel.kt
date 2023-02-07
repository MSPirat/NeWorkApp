package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.*
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
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.model.StateModel
import ru.netology.neworkapp.repository.PostRepository
import ru.netology.neworkapp.utils.SingleLiveEvent
import javax.inject.Inject

private val empty = Post(
    id = 0,
    authorId = 0,
    author = "",
    authorAvatar = "",
    content = "",
    published = "2023-01-27T17:00:00.000Z",
)

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    appAuth: AppAuth,
) : ViewModel() {

    private val cached = postRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<Post>> =
        appAuth.authStateFlow
            .flatMapLatest { (myId, _) ->
                cached.map { pagingData ->
                    pagingData.map { post ->
                        post.copy(ownedByMe = post.authorId == myId)
                    }
                }
            }

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    private val scope = MainScope()


    fun save() {
        edited.value?.let { post ->
            viewModelScope.launch {
                try {
                    postRepository.savePost(post)
                    _dataState.value = StateModel()
                    _postCreated.value = Unit
                } catch (e: Exception) {
                    _dataState.value = StateModel(error = true)
                }
            }
        }
        edited.value = empty
    }

    fun changeContent(content: String) {
        val text = content.trim()
        if (edited.value?.content == text) {
            return
        }
        edited.value = edited.value?.copy(content = text)
    }

    fun deleteById(id: Long) = viewModelScope.launch {
        try {
            postRepository.deleteById(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    override fun onCleared() {
        super.onCleared()
        scope.cancel()
    }
}