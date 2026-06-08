package org.example.repository

import org.example.tables.UsersTable
import org.example.models.UserEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * Repository para operações CRUD de usuários
 */
object UserRepository {

    /**
     * CREATE - Inserir novo usuário
     */
    fun createUser(email: String, hashedPassword: String, name: String): UserEntity {
        return transaction {
            val now = System.currentTimeMillis()
            UsersTable.insert {
                it[UsersTable.email] = email
                it[UsersTable.hashedPassword] = hashedPassword
                it[UsersTable.name] = name
                it[UsersTable.createdAt] = now
                it[UsersTable.updatedAt] = now
            }

            UsersTable.select { UsersTable.email eq email }
                .map { UserEntity.fromRow(it) }
                .first()
        }
    }

    /**
     * READ - Buscar usuário por email
     */
    fun getUserByEmail(email: String): UserEntity? {
        return transaction {
            UsersTable.select { UsersTable.email eq email }
                .map { UserEntity.fromRow(it) }
                .firstOrNull()
        }
    }

    /**
     * READ - Buscar usuário por ID
     */
    fun getUserById(id: Int): UserEntity? {
        return transaction {
            UsersTable.select { UsersTable.id eq id }
                .map { UserEntity.fromRow(it) }
                .firstOrNull()
        }
    }

    /**
     * READ - Buscar todos os usuários
     */
    fun getAllUsers(): List<UserEntity> {
        return transaction {
            UsersTable.selectAll()
                .map { UserEntity.fromRow(it) }
        }
    }

    /**
     * UPDATE - Atualizar nome do usuário
     */
    fun updateUserName(email: String, newName: String): Boolean {
        return transaction {
            val updated = UsersTable.update({ UsersTable.email eq email }) {
                it[UsersTable.name] = newName
                it[UsersTable.updatedAt] = System.currentTimeMillis()
            }
            updated > 0
        }
    }

    /**
     * UPDATE - Atualizar senha do usuário
     */
    fun updatePassword(email: String, newHashedPassword: String): Boolean {
        return transaction {
            val updated = UsersTable.update({ UsersTable.email eq email }) {
                it[UsersTable.hashedPassword] = newHashedPassword
                it[UsersTable.updatedAt] = System.currentTimeMillis()
            }
            updated > 0
        }
    }

    /**
     * DELETE - Deletar usuário por email
     */
    fun deleteUser(email: String): Boolean {
        return transaction {
            val deleted = UsersTable.deleteWhere { (UsersTable.email eq email) }
            deleted > 0
        }
    }

    /**
     * DELETE - Deletar usuário por ID
     */
    fun deleteUserById(id: Int): Boolean {
        return transaction {
            val deleted = UsersTable.deleteWhere { (UsersTable.id eq id) }
            deleted > 0
        }
    }

    /**
     * Verificar se email já existe
     */
    fun emailExists(email: String): Boolean {
        return transaction {
            UsersTable.select { UsersTable.email eq email }.count() > 0
        }
    }

    /**
     * Contar total de usuários
     */
    fun countUsers(): Long {
        return transaction {
            UsersTable.selectAll().count()
        }
    }
}
