package com.example.assignment3.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.assignment3.database.converters.DateConverter
import java.util.Date

@Entity(tableName = "tasks")
@TypeConverters(DateConverter::class)
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String? = null,
    val dueDate: Date? = null,
    val priority: Int = 1, // 1: Low, 2: Medium, 3: High
    val category: String = "Personal",
    val isCompleted: Boolean = false,
    val createdAt: Date = Date()
)