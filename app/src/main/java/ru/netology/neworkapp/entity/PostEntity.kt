package ru.netology.neworkapp.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.neworkapp.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val authorId: Long,
    val author: String,
    val authorAvatar: String?,
    val content: String,
    val published: String,
    @Embedded
    val coordinates: CoordinatesEmbeddable?,
    val link: String? = null,
    val mentionIds: Set<Long> = emptySet(),
    val mentionedMe: Boolean = false,
    val likedByMe: Boolean = false,
    @Embedded
    val attachment: AttachmentEmbeddable?,
    val likeOwnerIds: Set<Long> = emptySet(),
    val ownedByMe: Boolean = false,
) {
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        content = content,
        published = published,
        coordinates = coordinates?.toDto(),
        link = link,
        mentionIds = mentionIds,
        mentionedMe = mentionedMe,
        likedByMe = likedByMe,
        attachment = attachment?.toDto(),
        likeOwnerIds = likeOwnerIds,
        ownedByMe = ownedByMe,
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                dto.id,
                dto.authorId,
                dto.author,
                dto.authorAvatar,
                dto.content,
                dto.published,
                CoordinatesEmbeddable.fromDto(dto.coordinates),
                dto.link,
                dto.mentionIds,
                dto.mentionedMe,
                dto.likedByMe,
                AttachmentEmbeddable.fromDto(dto.attachment),
                dto.likeOwnerIds,
                dto.likedByMe,
            )
    }
}

fun List<PostEntity>.toDto() = map(PostEntity::toDto)
fun List<Post>.toPostEntity() = map(PostEntity::fromDto)
