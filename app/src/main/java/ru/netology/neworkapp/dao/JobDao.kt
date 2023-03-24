package ru.netology.neworkapp.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.netology.neworkapp.entity.JobEntity

@Dao
interface JobDao {

    @Query("SELECT * FROM JobEntity ORDER BY start DESC")
    fun getJob(): Flow<List<JobEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJobs(jobs: List<JobEntity>)

//    @Query(
//        """
//            UPDATE JobEntity SET
//            name = :name,
//            position = :position,
//            start = :start,
//            finish = :finish
//            WHERE id = :id
//        """
//    )
//    suspend fun updateContentById(
//        id: Long,
//        name: String,
//        position: String,
//        start: String,
//        finish: String?,
//    )

//    suspend fun saveJob(jobEntity: JobEntity) =
//        if (jobEntity.id == 0L) insertJob(jobEntity)
//        else updateContentById(
//            jobEntity.id,
//            jobEntity.name,
//            jobEntity.position,
//            jobEntity.start,
//            jobEntity.finish
//        )

    @Query("DELETE FROM JobEntity WHERE id = :id")
    suspend fun removeById(id: Long)

    @Query("DELETE FROM JobEntity")
    suspend fun deleteAll()
}