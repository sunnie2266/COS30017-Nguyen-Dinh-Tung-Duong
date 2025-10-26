package com.example.assignment2.data.repository

import com.example.assignment2.data.model.User

object UserManager {
    private var currentUser: User? = null
    fun setUser(user: User) {
        currentUser = user.copy()
    }

    fun getUser(): User? {
        return currentUser
    }

    fun updateCredits(credits: Int) {
        currentUser = currentUser?.copy(credits = credits)
    }

    fun clearUser() {
        currentUser = null
    }
}