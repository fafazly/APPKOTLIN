package org.example.service

import org.example.repository.PostRepository
import org.example.models.PostEntity

class PostService(private val postRepository: PostRepository = PostRepository) {
    
    fun addPost(userId: Int, imageUrl: String, caption: String?): PostEntity? {
        return try {
            postRepository.addPost(userId, imageUrl, caption)
        } catch (e: Exception) {
            null
        }
    }

    fun getPostsByUser(userId: Int): List<PostEntity> = postRepository.getPostsByUser(userId)

    fun getAllPosts(): List<PostEntity> = postRepository.getAllPosts()

    fun deletePost(postId: Int): Boolean {
        return try {
            postRepository.deletePost(postId)
        } catch (e: Exception) {
            false
        }
    }
}
