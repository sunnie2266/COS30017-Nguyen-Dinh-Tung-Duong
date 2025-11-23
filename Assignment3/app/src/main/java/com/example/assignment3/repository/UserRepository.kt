package com.example.assignment3.repository

import com.example.assignment3.database.dao.UserDao
import com.example.assignment3.database.entity.User

class UserRepository(private val userDao: UserDao) {

    suspend fun register(user: User): Result<Long> {
        return try {
            // Check if email already exists
            if (userDao.isEmailExists(user.email) > 0) {
                Result.failure(Exception("Email already exists"))
            } else {
                val userId = userDao.insertUser(user)
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val user = userDao.login(email, password)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isEmailExists(email: String): Boolean {
        return userDao.isEmailExists(email) > 0
    }

    suspend fun getUserById(userId: Long): User? {
        return userDao.getUserById(userId)
    }
}