package com.example.proekt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proekt.api.ApiClient
import com.example.proekt.models.User

class MainActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvGoToRegister: TextView

    private val apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()

        setupClickListeners()
    }
    private fun initializeViews() {
        etEmail = findViewById(R.id.login)
        etPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.auth)
        tvGoToRegister = findViewById(R.id.toregistration)
    }

    private fun setupClickListeners() {
        btnLogin.setOnClickListener {
            loginUser()
        }

        tvGoToRegister.setOnClickListener {
            navigateToRegister()
        }
    }

    private fun getInputData(): Pair<String, String> {
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString().trim()
        return Pair(email, password)
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            showToast("Заполните все поля")
            return false
        }
        return true
    }
    private fun loginUser() {
        val (email, password) = getInputData()

        if (!validateInput(email, password)) {
            return
        }

        val loginRequest = User(email = email, password = password)

        apiClient.login(loginRequest) { userData ->
            runOnUiThread {
                handleLoginResponse(userData)
            }
        }
    }

        private fun handleLoginResponse(userData: User?) {
            if (userData != null) {
                onLoginSuccess(userData)
            } else {
                showToast("Ошибка авторизации")
            }
        }

    private fun onLoginSuccess(userData: com.example.proekt.models.User) {
        showToast("Добро пожаловать, ${userData.name}!")
        navigateToMain(userData)
    }

    private fun navigateToMain(userData: com.example.proekt.models.User) {
        StaticUser.userId = userData.id!!
        val intent = Intent(this, CatalogProduct::class.java).apply {
            putExtra("USER_NAME", userData.name)
            putExtra("USER_ID", userData.id)
            putExtra("USER_SURNAME", userData.surname)
            putExtra("USER_PATRONYMIC", userData.patronymic)
            putExtra("USER_EMAIL", userData.email)
            putExtra("USER_PHONE", userData.phone)
            putExtra("USER_PASSWORD", userData.password)
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegistrationActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
