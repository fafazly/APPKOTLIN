package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class PostEntity(
    val id: Int,
    val userId: Int,
    val imageUrl: String,
    val caption: String?,
    val createdAt: Long
)

