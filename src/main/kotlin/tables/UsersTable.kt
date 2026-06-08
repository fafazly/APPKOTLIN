package org.example.tables

import org.jetbrains.exposed.sql.Table

/**
 * Definição da tabela USERS
 */
object UsersTable : Table("users") {
    val id = integer("id").autoIncrement()
    val email = varchar("email", 255).uniqueIndex()
    val hashedPassword = varchar("hashed_password", 255)
    val name = varchar("name", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    override val primaryKey = PrimaryKey(id)
}

