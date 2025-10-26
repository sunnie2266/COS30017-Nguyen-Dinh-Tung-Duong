package com.example.assignment2.activity

import androidx.activity.enableEdgeToEdge
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.assignment2.R
import com.example.assignment2.data.model.User
import com.example.assignment2.data.repository.UserManager

class LoginActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)


        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        btnLogin = findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            if (validateInput()) {
                val user = User(
                    name = etName.text.toString(),
                    phone = etPhone.text.toString(),
                    email = etEmail.text.toString()
                )
                UserManager.setUser(user)
                startActivity(Intent(this, InstrumentItemActivity::class.java))
                finish()
            }
        }
    }

    private fun validateInput(): Boolean {
        val name = etName.text.toString()
        val phone = etPhone.text.toString()
        val email = etEmail.text.toString()

        if (name.isBlank()) {
            etName.error = "Name is required"
            return false
        }

        if (phone.isBlank()) {
            etPhone.error = "Phone is required"
            return false
        }

        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Valid email is required"
            return false
        }

        return true
    }
}