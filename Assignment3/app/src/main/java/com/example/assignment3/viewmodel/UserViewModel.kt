package com.example.assignment3.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment3.database.entity.User
import com.example.assignment3.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registrationResult = MutableStateFlow<Result<Long>?>(null)
    val registrationResult: StateFlow<Result<Long>?> = _registrationResult

    private val _loginResult = MutableStateFlow<Result<User>?>(null)
    val loginResult: StateFlow<Result<User>?> = _loginResult

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun register(email: String, password: String, username: String) {
        viewModelScope.launch {
            val user = User(
                email = email,
                password = password, // In real app, hash this!
                username = username
            )
            _registrationResult.value = userRepository.register(user)
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = userRepository.login(email, password)
            // If login successful, set current user
            _loginResult.value?.getOrNull()?.let { user ->
                _currentUser.value = user
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _loginResult.value = null
    }

    fun clearResults() {
        _registrationResult.value = null
        _loginResult.value = null
    }
}