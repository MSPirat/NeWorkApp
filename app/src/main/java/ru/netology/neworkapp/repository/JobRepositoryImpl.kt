package ru.netology.neworkapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import ru.netology.neworkapp.api.JobApiService
import ru.netology.neworkapp.dao.JobDao
import ru.netology.neworkapp.dto.Job
import ru.netology.neworkapp.entity.JobEntity
import ru.netology.neworkapp.entity.toDto
import ru.netology.neworkapp.entity.toJobEntity
import ru.netology.neworkapp.errors.ApiError
import ru.netology.neworkapp.errors.NetworkError
import java.io.IOException
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(

    private val jobDao: JobDao,
    private val jobApiService: JobApiService
) : JobRepository {

    override val data: Flow<List<Job>> = jobDao.getJob()
        .map { it.toDto() }
        .flowOn(Dispatchers.Default)

    private val _data = MutableLiveData<List<Job>>()

    override suspend fun saveJob(job: Job) {
        try {
            jobDao.saveJob(JobEntity.fromDto(job))
            val response = jobApiService.saveJob(job)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            jobDao.insertJob(JobEntity.fromDto(body))
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun getJobByUserId(id: Long) {
        try {
            jobDao.deleteAll()
            val response = jobApiService.getJobByUserId(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
            val body = response.body() ?: throw ApiError(response.message())
            _data.postValue(body)
            jobDao.insertJobs(body.toJobEntity())
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }

    override suspend fun removeById(id: Long) {
        try {
            jobDao.removeById(id)
            val response = jobApiService.removeById(id)
            if (!response.isSuccessful) {
                throw ApiError(response.message())
            }
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError()
        }
    }
}