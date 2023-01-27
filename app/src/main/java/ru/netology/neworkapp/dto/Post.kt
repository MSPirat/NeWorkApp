package ru.netology.neworkapp.dto

//sealed interface FeedItem {
//    val id: Long
//}

data class Post(
//    override val id: Long,
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    val coordinates: Coordinates? = null,
    val link: String? = null,
    val mentionIds: Set<Long> = emptySet(),
    val mentionedMe: Boolean = false,
    val likeOwnerIds: Set<Long> = emptySet(),
    val likedByMe: Boolean = false,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean = false,
)
//) : FeedItem
//
//data class Ad(
//    override val id: Long,
//    val image: String,
//) : FeedItem
