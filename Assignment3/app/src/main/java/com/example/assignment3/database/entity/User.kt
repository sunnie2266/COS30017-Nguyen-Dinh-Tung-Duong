package com.example.assignment3.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val email: String,
    val password: String, // In real app, this should be hashed!
    val username: String,
    val createdAt: Long = System.currentTimeMillis()
)