package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.neworkapp.dto.FeedItem
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
) : ViewModel() {

    private val cached = postRepository.data.cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = cached
//        .map(::PostModel)
//        .asLiveData(Dispatchers.Default)

    private val edited = MutableLiveData(empty)

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

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
}