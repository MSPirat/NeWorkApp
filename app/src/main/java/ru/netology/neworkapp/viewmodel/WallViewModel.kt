package ru.netology.neworkapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.repository.WallRepository
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class WallViewModel @Inject constructor(
    private val wallRepository: WallRepository,
//    private val wallDao: WallDao,
    private val appAuth: AppAuth,
) : ViewModel() {

    private val _userId = MutableLiveData<Long>()
    val userId: LiveData<Long>
        get() = _userId


    fun loadUserWall(id: Long) = appAuth.authStateFlow
        .flatMapLatest { (myId, _) ->
            wallRepository.loadUserWall(id).map { pagingData ->
                pagingData.map { post ->
                    post.copy(ownedByMe = post.authorId == myId)
                }
            }
        }
}