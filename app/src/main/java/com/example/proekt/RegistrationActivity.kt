package com.example.proekt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proekt.api.ApiClient
import com.example.proekt.models.User
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var image: ImageView
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPassword2: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnRegister: Button
    private lateinit var tvGoTologin: TextView
    private lateinit var checkBox: CheckBox

    private val apiClient = ApiClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration)
        initializeViews()
        setupClickListeners()
    }

    private fun initializeViews() {
        etName = findViewById(R.id.FIO)
        etEmail = findViewById(R.id.email)
        etPhone = findViewById(R.id.phone)
        etPassword = findViewById(R.id.password1)
        etPassword2 = findViewById(R.id.password2)
        btnRegister = findViewById(R.id.btnRegister)
        tvGoTologin = findViewById(R.id.toregistration)
        checkBox = findViewById(R.id.checkBox)
        image = findViewById<ImageView>(R.id.imageView)
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            registerUser()
        }
        tvGoTologin.setOnClickListener {
            navigateTologin()
        }
        image.isClickable = true
        image.isFocusable = true
        image.setOnClickListener {
            navigateTolog()
        }
    }

    private fun validateName(name: String): Boolean {
        if (name.isEmpty()) {
            showToast("Поле ФИО не может быть пустым")
            return false
        }

        val namePattern = Pattern.compile("^[\\p{L} \\-]+\$")
        if (!namePattern.matcher(name).matches()) {
            showToast("ФИО может содержать только буквы, пробелы, дефисы и апострофы")
            return false
        }

        val parts = name.split(" ").filter { it.isNotBlank() }
        if (parts.size < 2) {
            showToast("Введите фамилию и имя через пробел")
            return false
        }

        for (part in parts) {
            if (part.length < 2) {
                showToast("Каждая часть ФИО должна содержать минимум 2 символа")
                return false
            }
            if (part.length > 20) {
                showToast("Каждая часть ФИО не должна превышать 20 символов")
                return false
            }
        }

        return true
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            showToast("Поле email не может быть пустым")
            return false
        }

        if (email.length < 6) {
            showToast("Email должен содержать минимум 6 символов")
            return false
        }

        if (email.length > 60) {
            showToast("Email не должен превышать 60 символов")
            return false
        }

        val emailPattern = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$"
        )
        if (!emailPattern.matcher(email).matches()) {
            showToast("Введите корректный email адрес")
            return false
        }

        return true
    }

    private fun validatePhone(phone: String): Boolean {
        if (phone.isEmpty()) {
            showToast("Поле телефона не может быть пустым")
            return false
        }

        val cleanPhone = phone.replace("\\D".toRegex(), "")
        if (cleanPhone.length < 10) {
            showToast("Номер телефона должен содержать минимум 10 цифр")
            return false
        }
        if (cleanPhone.length > 12) {
            showToast("Номер телефона должен содержать максимум 12 цифр")
            return false
        }
        return true
    }

    private fun validatePassword(password: String, confirmPassword: String, fullName: String, email: String): Boolean {
        if (password.isEmpty()) {
            showToast("Пароль не может быть пустым")
            return false
        }

        if (password.length < 8) {
            showToast("Пароль должен содержать минимум 8 символов")
            return false
        }

        if (password.length > 64) {
            showToast("Пароль не должен превышать 64 символа")
            return false
        }

        if (!password.any { it.isUpperCase() }) {
            showToast("Пароль должен содержать хотя бы одну заглавную букву")
            return false
        }

        if (!password.any { it.isLowerCase() }) {
            showToast("Пароль должен содержать хотя бы одну строчную букву")
            return false
        }

        if (!password.any { it.isDigit() }) {
            showToast("Пароль должен содержать хотя бы одну цифру")
            return false
        }

        val specialCharPattern = Pattern.compile("[+=-_.,!?]")
        if (!specialCharPattern.matcher(password).find()) {
            showToast("Пароль должен содержать хотя бы один специальный символ (+=-!?и т.д.)")
            return false
        }

        val commonPasswords = listOf("123456", "password", "qwerty", "111111", "admin")
        if (commonPasswords.any { password.contains(it, true) }) {
            showToast("Пароль слишком простой")
            return false
        }

        val nameParts = fullName.split(" ").filter { it.length > 2 }
        if (nameParts.any { password.contains(it, true) }) {
            showToast("Пароль не должен содержать ваше имя или фамилию")
            return false
        }

        val emailLocalPart = email.substringBefore("@")
        if (emailLocalPart.length > 3 && password.contains(emailLocalPart, true)) {
            showToast("Пароль не должен содержать часть вашего email")
            return false
        }

        if (password != confirmPassword) {
            showToast("Пароли не совпадают")
            return false
        }

        return true
    }

    private fun registerUser() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val password2 = etPassword2.text.toString().trim()

        if (!validateName(name)) return
        if (!validateEmail(email)) return
        if (!validatePhone(phone)) return
        if (!validatePassword(password, password2, name, email)) return
        if(!checkBox.isChecked) {
            showToast("согласитесь на обработку персональных данных")
            return
        }

        val nameParts = name.split(" ").filter { it.isNotBlank() }

        val surname = if (nameParts.isNotEmpty()) nameParts[0] else ""
        val userName = if (nameParts.size > 1) nameParts[1] else ""
        val patronymic = if (nameParts.size > 2) nameParts[2] else ""

        val user = User(
            surname = surname,
            name = userName,
            patronymic = patronymic,
            email = email,
            password = password,
            phone = phone,
            role = "Пользователь",
            status = "Действителен"
        )

        apiClient.register(user) { userData ->
            runOnUiThread {
                if (userData != null) {
                    showToast("Успешная регистрация! Добро пожаловать, ${userData.name}!")
                    navigateTologin(userData)
                } else {
                    showToast("Ошибка регистрации")
                }
            }
        }
    }

    private fun navigateTologin(userData: User? = null) {
        val intent = Intent(this, MainActivity::class.java)
        userData?.let {
            intent.putExtra("USER_NAME", it.name)
            intent.putExtra("USER_ID", it.id)
        }
        startActivity(intent)
        finish()
    }
    private fun navigateTolog() {
        val intent = Intent(this, MainActivity::class.java)

        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}