package org.example.service

import org.example.repository.CommentRepository
import org.example.models.CommentEntity

class CommentService(private val commentRepository: CommentRepository = CommentRepository) {
    
    fun addComment(postId: Int, userId: Int, text: String): CommentEntity? {
        return try {
            commentRepository.addComment(postId, userId, text)
        } catch (e: Exception) {
            null
        }
    }

    fun getCommentsByPost(postId: Int): List<CommentEntity> = commentRepository.getCommentsByPost(postId)

    fun deleteComment(commentId: Int): Boolean {
        return try {
            commentRepository.deleteComment(commentId)
        } catch (e: Exception) {
            false
        }
    }
}
