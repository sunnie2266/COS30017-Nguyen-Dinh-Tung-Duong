package com.example.assignment3.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.assignment3.R
import com.example.assignment3.MainActivity
import com.example.assignment3.database.AppDatabase
import com.example.assignment3.repository.UserRepository
import com.example.assignment3.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etUsername: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvSwitchToRegister: TextView

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(this).userDao()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupObservers()
        setupClickListeners()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etUsername = findViewById(R.id.etUsername)
        btnLogin = findViewById(R.id.btnLogin)
        tvSwitchToRegister = findViewById(R.id.tvSwitchToRegister)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            userViewModel.loginResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        // Login successful
                        val user = it.getOrNull()
                        Toast.makeText(this@LoginActivity, "Welcome, ${user?.username}!", Toast.LENGTH_SHORT).show()

                        // Navigate to MainActivity
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Login failed
                        val errorMessage = it.exceptionOrNull()?.message ?: "Login failed"
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    userViewModel.clearResults()
                }
            }
        }

        lifecycleScope.launch {
            userViewModel.registrationResult.collect { result ->
                result?.let {
                    if (it.isSuccess) {
                        // Registration successful
                        Toast.makeText(this@LoginActivity, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                        // Switch to login mode
                        btnLogin.text = "Login"
                        tvSwitchToRegister.text = "Don't have an account? Register"
                        etUsername.visibility = View.GONE
                    } else {
                        // Registration failed
                        val errorMessage = it.exceptionOrNull()?.message ?: "Registration failed"
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    userViewModel.clearResults()
                }
            }
        }
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                if (btnLogin.text == "Login") {
                    userViewModel.login(email, password)
                } else {
                    val username = etUsername.text.toString().trim()
                    if (username.isNotEmpty()) {
                        userViewModel.register(email, password, username)
                    } else {
                        Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        tvSwitchToRegister.setOnClickListener {
            if (btnLogin.text == "Login") {
                // Switch to register mode
                btnLogin.text = "Register"
                tvSwitchToRegister.text = "Already have an account? Login"
                etUsername.visibility = View.VISIBLE
            } else {
                // Switch to login mode
                btnLogin.text = "Login"
                tvSwitchToRegister.text = "Don't have an account? Register"
                etUsername.visibility = View.GONE
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}

// ViewModel Factory - SEPARATE CLASS
class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}