package com.example.assignment3.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.assignment3.database.TaskDatabase
import com.example.assignment3.database.entity.Task
import com.example.assignment3.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>
    val pendingTasks: LiveData<List<Task>>
    val completedTasks: LiveData<List<Task>>
    val completedCount: LiveData<Int>
    val pendingCount: LiveData<Int>

    private val _navigateToTaskDetail = MutableLiveData<Long?>()
    val navigateToTaskDetail: LiveData<Long?> get() = _navigateToTaskDetail

    init {
        val database = TaskDatabase.getInstance(application)
        repository = TaskRepository(database) // Now passing TaskDatabase instead of TaskDao
        allTasks = repository.getAllTasks()
        pendingTasks = repository.getPendingTasks()
        completedTasks = repository.getCompletedTasks()
        completedCount = repository.getCompletedCount()
        pendingCount = repository.getPendingCount()
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        repository.insertTask(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        repository.updateTask(task)
    }

    fun deleteTask(task: Task) = viewModelScope.launch {
        repository.deleteTask(task)
    }

    fun onTaskClicked(taskId: Long) {
        _navigateToTaskDetail.value = taskId
    }

    fun onTaskDetailNavigated() {
        _navigateToTaskDetail.value = null
    }

    fun getPendingCountByPriority(priority: Int): LiveData<Int> {
        return repository.getPendingCountByPriority(priority)
    }

    fun getTaskById(taskId: Long): LiveData<Task?> {
        val result = MutableLiveData<Task?>()
        viewModelScope.launch {
            allTasks.value?.find { it.id == taskId }?.let { task ->
                result.postValue(task)
            }
        }
        return result
    }
}