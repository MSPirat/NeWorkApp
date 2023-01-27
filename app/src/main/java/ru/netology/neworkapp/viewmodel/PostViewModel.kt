package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.model.PostModel
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
    published = "",
)

@ExperimentalCoroutinesApi
@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    val data: LiveData<PostModel> = postRepository.data
        .map(::PostModel)
        .asLiveData(Dispatchers.Default)

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