package com.example.assignment2.data.model

data class User(
    val name: String,
    val phone: String,
    val email: String,
    var credits: Int = 1000
)
