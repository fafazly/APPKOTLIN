package org.example.tables

import org.jetbrains.exposed.sql.Table

/**
 * Definição da tabela POSTS
 */
object PostsTable : Table("posts") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id") references UsersTable.id
    val imageUrl = varchar("image_url", 255)
    val caption = varchar("caption", 500).nullable()
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

