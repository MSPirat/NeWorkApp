package ru.netology.neworkapp.model

import ru.netology.neworkapp.dto.Post

data class PostModel(
    val posts: List<Post> = emptyList(),
    val empty: Boolean = false,
)
