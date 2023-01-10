package ru.netology.neworkapp.dto

data class User(
    val id: Long,
    val login: String,
    val name: String,
    val avatar: String? = null,
    val authentication: List<String>,
)