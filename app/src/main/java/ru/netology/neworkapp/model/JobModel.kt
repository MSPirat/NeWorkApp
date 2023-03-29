package ru.netology.neworkapp.model

import ru.netology.neworkapp.dto.Job

data class JobModel(
    val jobs: List<Job> = emptyList(),
    val empty: Boolean = false,
)