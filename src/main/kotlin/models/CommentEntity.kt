package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class CommentEntity(
    val id: Int,
    val postId: Int,
    val userId: Int,
    val text: String,
    val createdAt: Long
)

