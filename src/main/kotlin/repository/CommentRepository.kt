package org.example.repository

import org.example.tables.CommentsTable
import org.example.models.CommentEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * Repository para operações CRUD de comentários
 */
object CommentRepository {

    fun addComment(postId: Int, userId: Int, text: String): CommentEntity {
        return transaction {
            val now = System.currentTimeMillis()
            CommentsTable.insert {
                it[CommentsTable.postId] = postId
                it[CommentsTable.userId] = userId
                it[CommentsTable.text] = text
                it[CommentsTable.createdAt] = now
            }

            CommentsTable.selectAll()
                .orderBy(CommentsTable.id to SortOrder.DESC)
                .limit(1)
                .map {
                    CommentEntity(
                        id = it[CommentsTable.id],
                        postId = it[CommentsTable.postId],
                        userId = it[CommentsTable.userId],
                        text = it[CommentsTable.text],
                        createdAt = it[CommentsTable.createdAt]
                    )
                }
                .first()
        }
    }

    fun getCommentsByPost(postId: Int): List<CommentEntity> = transaction {
        CommentsTable.select { CommentsTable.postId eq postId }
            .map {
                CommentEntity(
                    id = it[CommentsTable.id],
                    postId = it[CommentsTable.postId],
                    userId = it[CommentsTable.userId],
                    text = it[CommentsTable.text],
                    createdAt = it[CommentsTable.createdAt]
                )
            }
    }

    fun deleteComment(commentId: Int): Boolean {
        return transaction {
            CommentsTable.deleteWhere { CommentsTable.id eq commentId } > 0
        }
    }
}
