package org.example.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.ResultRow
import org.example.tables.UsersTable

/**
 * Classe de dados representando um usuário
 */
@Serializable
data class UserEntity(
    val id: Int,
    val email: String,
    val hashedPassword: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long
) {
    companion object {
        fun fromRow(row: ResultRow): UserEntity {
            return UserEntity(
                id = row[UsersTable.id],
                email = row[UsersTable.email],
                hashedPassword = row[UsersTable.hashedPassword],
                name = row[UsersTable.name],
                createdAt = row[UsersTable.createdAt],
                updatedAt = row[UsersTable.updatedAt]
            )
        }
    }
}

/**
 * DTOs para requisições
 */
@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class UpdateProfileRequest(
    val name: String
)

@Serializable
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

@Serializable
data class DeleteRequest(
    val userId: String,
    val password: String
)

/**
 * DTO para respostas da API
 */
@Serializable
data class ApiResponse(
    val success: Boolean,
    val message: String,
    val data: Map<String, String>? = null
)

