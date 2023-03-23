package ru.netology.neworkapp.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.User

interface UserRepository {

    val data: Flow<List<User>>

    suspend fun getAll()
//
//    suspend fun getUserById(id: Long): User
}