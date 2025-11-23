package com.example.assignment3.util

import com.example.assignment3.R

object Priority {
    const val LOW = 1
    const val MEDIUM = 2
    const val HIGH = 3

    fun getPriorityName(priority: Int): String {
        return when (priority) {
            HIGH -> "High"
            MEDIUM -> "Medium"
            LOW -> "Low"
            else -> "Unknown"
        }
    }

    fun getPriorityColorRes(priority: Int): Int {
        return when (priority) {
            HIGH -> R.color.priority_high
            MEDIUM -> R.color.priority_medium
            LOW -> R.color.priority_low
            else -> R.color.priority_low
        }
    }
}