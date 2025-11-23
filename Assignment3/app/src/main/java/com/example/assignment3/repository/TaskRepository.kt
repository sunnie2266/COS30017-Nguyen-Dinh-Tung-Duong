package com.example.assignment3.repository

import androidx.lifecycle.LiveData
import com.example.assignment3.database.TaskDatabase
import com.example.assignment3.database.entity.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(private val database: TaskDatabase) {

    suspend fun insertTask(task: Task): Long = withContext(Dispatchers.IO) {
        database.taskDao().insert(task)
    }

    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        database.taskDao().update(task)
    }

    suspend fun deleteTask(task: Task) = withContext(Dispatchers.IO) {
        database.taskDao().delete(task)
    }

    fun getAllTasks(): LiveData<List<Task>> = database.taskDao().getAllTasks()

    fun getPendingTasks(): LiveData<List<Task>> = database.taskDao().getPendingTasks()

    fun getCompletedTasks(): LiveData<List<Task>> = database.taskDao().getCompletedTasks()

    fun getTasksByPriority(priority: Int): LiveData<List<Task>> =
        database.taskDao().getTasksByPriority(priority)

    fun getCompletedCount(): LiveData<Int> = database.taskDao().getCompletedCount()

    fun getPendingCount(): LiveData<Int> = database.taskDao().getPendingCount()

    fun getPendingCountByPriority(priority: Int): LiveData<Int> =
        database.taskDao().getPendingCountByPriority(priority)
}