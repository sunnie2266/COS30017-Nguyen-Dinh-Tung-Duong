package com.example.assignment3.data

import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasks(sortByPriority: Boolean = false): Flow<List<Task>> =
        taskDao.getAllTasks(sortByPriority)

    fun getActiveTasks(): Flow<List<Task>> =
        taskDao.getTasksByCompletion(false)

    fun getCompletedTasks(): Flow<List<Task>> =
        taskDao.getTasksByCompletion(true)

    suspend fun insertTask(task: Task): Long =
        taskDao.insertTask(task)

    suspend fun updateTask(task: Task) =
        taskDao.updateTask(task)

    suspend fun deleteTask(task: Task) =
        taskDao.deleteTask(task)

    suspend fun deleteCompletedTasks() =
        taskDao.deleteCompletedTasks()
}