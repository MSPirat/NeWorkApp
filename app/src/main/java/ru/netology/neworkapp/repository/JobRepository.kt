package ru.netology.neworkapp.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.dto.Job

interface JobRepository {

    val data: Flow<List<Job>>

    suspend fun saveJob(job: Job)

    suspend fun getUserById(id: Long)

    suspend fun removeById(id: Long)
}