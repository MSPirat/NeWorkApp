package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import ru.netology.neworkapp.dao.WallDao
import ru.netology.neworkapp.dto.FeedItem
import ru.netology.neworkapp.model.StateModel
import ru.netology.neworkapp.repository.WallRepository
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WallViewModel @Inject constructor(
    private val wallRepository: WallRepository,
    private val wallDao: WallDao,
) : ViewModel() {

    private val cached = wallRepository
        .data
        .cachedIn(viewModelScope)

    val data: Flow<PagingData<FeedItem>> = cached

    private val _dataState = MutableLiveData<StateModel>()
    val dataState: LiveData<StateModel>
        get() = _dataState

    fun loadWall(id: Long) = viewModelScope.launch {
        try {
            wallRepository.load(id)
        } catch (e: Exception) {
            _dataState.value = StateModel(error = true)
        }
    }

    fun removeWall() = viewModelScope.launch {
        wallDao.removeAll()
    }
}