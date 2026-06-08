package org.example.repository

import org.example.tables.NotificationsTable
import org.example.models.NotificationEntity
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * Repository para operações CRUD de notificações
 */
object NotificationRepository {

    fun addNotification(userId: Int, message: String, type: String): NotificationEntity {
        return transaction {
            val now = System.currentTimeMillis()
            NotificationsTable.insert {
                it[NotificationsTable.userId] = userId
                it[NotificationsTable.message] = message
                it[NotificationsTable.type] = type
                it[NotificationsTable.status] = "unread"
                it[NotificationsTable.createdAt] = now
            }

            NotificationsTable.selectAll()
                .orderBy(NotificationsTable.id to SortOrder.DESC)
                .limit(1)
                .map {
                    NotificationEntity(
                        id = it[NotificationsTable.id],
                        userId = it[NotificationsTable.userId],
                        message = it[NotificationsTable.message],
                        type = it[NotificationsTable.type],
                        status = it[NotificationsTable.status],
                        createdAt = it[NotificationsTable.createdAt]
                    )
                }
                .first()
        }
    }

    fun getNotificationsByUser(userId: Int): List<NotificationEntity> = transaction {
        NotificationsTable.select { NotificationsTable.userId eq userId }
            .map {
                NotificationEntity(
                    id = it[NotificationsTable.id],
                    userId = it[NotificationsTable.userId],
                    message = it[NotificationsTable.message],
                    type = it[NotificationsTable.type],
                    status = it[NotificationsTable.status],
                    createdAt = it[NotificationsTable.createdAt]
                )
            }
    }

    fun markAsRead(notificationId: Int): Boolean {
        return transaction {
            NotificationsTable.update({ NotificationsTable.id eq notificationId }) {
                it[NotificationsTable.status] = "read"
            } > 0
        }
    }

    fun deleteNotification(notificationId: Int): Boolean {
        return transaction {
            NotificationsTable.deleteWhere { NotificationsTable.id eq notificationId } > 0
        }
    }
}
