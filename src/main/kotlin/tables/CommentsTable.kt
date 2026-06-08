package org.example.tables

import org.jetbrains.exposed.sql.Table

/**
 * Definição da tabela COMMENTS
 */
object CommentsTable : Table("comments") {
    val id = integer("id").autoIncrement()
    val postId = integer("post_id") references PostsTable.id
    val userId = integer("user_id") references UsersTable.id
    val text = varchar("text", 500)
    val createdAt = long("created_at")

    override val primaryKey = PrimaryKey(id)
}

