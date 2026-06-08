package org.example.models

import kotlinx.serialization.Serializable

@Serializable
data class NotificationEntity(
    val id: Int,
    val userId: Int,
    val message: String,
    val type: String,
    val status: String,
    val createdAt: Long
)

