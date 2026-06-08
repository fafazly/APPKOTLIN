package org.example.repository

import org.example.tables.PostsTable
import org.example.models.PostEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * Repository para operações CRUD de posts
 */
object PostRepository {

    fun addPost(userId: Int, imageUrl: String, caption: String?): PostEntity {
        return transaction {
            val now = System.currentTimeMillis()
            PostsTable.insert {
                it[PostsTable.userId] = userId
                it[PostsTable.imageUrl] = imageUrl
                it[PostsTable.caption] = caption
                it[PostsTable.createdAt] = now
            }

            PostsTable.selectAll()
                .orderBy(PostsTable.id to SortOrder.DESC)
                .limit(1)
                .map { 
                    PostEntity(
                        id = it[PostsTable.id],
                        userId = it[PostsTable.userId],
                        imageUrl = it[PostsTable.imageUrl],
                        caption = it[PostsTable.caption],
                        createdAt = it[PostsTable.createdAt]
                    )
                }
                .first()
        }
    }

    fun getPostsByUser(userId: Int): List<PostEntity> = transaction {
        PostsTable.select { PostsTable.userId eq userId }
            .map {
                PostEntity(
                    id = it[PostsTable.id],
                    userId = it[PostsTable.userId],
                    imageUrl = it[PostsTable.imageUrl],
                    caption = it[PostsTable.caption],
                    createdAt = it[PostsTable.createdAt]
                )
            }
    }

    fun getAllPosts(): List<PostEntity> = transaction {
        PostsTable.selectAll()
            .map {
                PostEntity(
                    id = it[PostsTable.id],
                    userId = it[PostsTable.userId],
                    imageUrl = it[PostsTable.imageUrl],
                    caption = it[PostsTable.caption],
                    createdAt = it[PostsTable.createdAt]
                )
            }
    }

    fun deletePost(postId: Int): Boolean {
        return transaction {
            PostsTable.deleteWhere { PostsTable.id eq postId } > 0
        }
    }
}
