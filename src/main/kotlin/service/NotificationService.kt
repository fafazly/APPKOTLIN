package org.example.service

import org.example.repository.NotificationRepository
import org.example.models.NotificationEntity

class NotificationService(private val notificationRepository: NotificationRepository = NotificationRepository) {
    
    fun addNotification(userId: Int, message: String, type: String): NotificationEntity? {
        return try {
            notificationRepository.addNotification(userId, message, type)
        } catch (e: Exception) {
            null
        }
    }

    fun getNotificationsByUser(userId: Int): List<NotificationEntity> = notificationRepository.getNotificationsByUser(userId)

    fun markAsRead(notificationId: Int): Boolean {
        return try {
            notificationRepository.markAsRead(notificationId)
        } catch (e: Exception) {
            false
        }
    }

    fun deleteNotification(notificationId: Int): Boolean {
        return try {
            notificationRepository.deleteNotification(notificationId)
        } catch (e: Exception) {
            false
        }
    }
}
