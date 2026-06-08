package org.example.service

import org.example.repository.UserRepository
import org.example.models.UserEntity

class UserService(private val userRepository: UserRepository = UserRepository) {
    
    fun createUser(email: String, hashedPassword: String, name: String): UserEntity? {
        return try {
            userRepository.createUser(email, hashedPassword, name)
        } catch (e: Exception) {
            null
        }
    }

    fun getUserByEmail(email: String): UserEntity? = userRepository.getUserByEmail(email)

    fun getUserById(userId: Int): UserEntity? = userRepository.getUserById(userId)

    fun getAllUsers(): List<UserEntity> = userRepository.getAllUsers()

    fun updateUserName(email: String, newName: String): Boolean {
        return try {
            userRepository.updateUserName(email, newName)
        } catch (e: Exception) {
            false
        }
    }

    fun updatePassword(email: String, newHashedPassword: String): Boolean {
        return try {
            userRepository.updatePassword(email, newHashedPassword)
        } catch (e: Exception) {
            false
        }
    }

    fun deleteUser(email: String): Boolean {
        return try {
            userRepository.deleteUser(email)
        } catch (e: Exception) {
            false
        }
    }

    fun deleteUserById(userId: Int): Boolean {
        return try {
            userRepository.deleteUserById(userId)
        } catch (e: Exception) {
            false
        }
    }

    fun emailExists(email: String): Boolean = userRepository.emailExists(email)

    fun countUsers(): Long = userRepository.countUsers()
}
