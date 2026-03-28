package com.example.proekt
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.proekt.api.ApiClient
import com.example.proekt.models.User
import com.squareup.picasso.Picasso
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.regex.Pattern

class Profil : AppCompatActivity() {
    private var currentUser: User? = null
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var tvProfileTitle: TextView
    private lateinit var btnBack: ImageView
    private lateinit var etName: EditText
    private lateinit var phone: EditText
    private lateinit var url: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSave: AppCompatButton
    private lateinit var zakazy: AppCompatButton
    private lateinit var delete: AppCompatButton
    private lateinit var btnLoadPhoto: AppCompatButton
    private lateinit var adressphoto: AppCompatButton
    private lateinit var bottomNavigation: BottomNavigationView
    private var userId: Int = 0
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profil)

        initializeViews()
        userId = intent.getIntExtra("USER_ID", 0)

        apiClient = ApiClient()

        loadUserProfile()

        setupClickListeners()
        bottomNavigation = findViewById(R.id.bottomNavigationView)
        bottomNavigation.selectedItemId = R.id.navigation_profile

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, CatalogProduct::class.java).apply { putExtra("USER_ID", userId) }
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_korzina -> {
                    val intent = Intent(this, BasketActivity::class.java).apply { putExtra("USER_ID", userId) }
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.navigation_profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun initializeViews() {
        ivProfilePhoto = findViewById(R.id.photourl)
        tvProfileTitle = findViewById(R.id.profil)
        delete = findViewById(R.id.del)
        btnBack = findViewById(R.id.samolet)
        etName = findViewById(R.id.FIO)
        phone = findViewById(R.id.phone)
        etEmail = findViewById(R.id.email)
        btnSave = findViewById(R.id.change)
        url = findViewById(R.id.urlforphoto)
        btnLoadPhoto = findViewById(R.id.addphoto)
        adressphoto = findViewById(R.id.adress)
        zakazy = findViewById(R.id.zakazy)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        zakazy.setOnClickListener { gotozakazy() }
        btnSave.setOnClickListener {
            updateProfile()
        }
        delete.setOnClickListener {
            deleteProfile()
        }

        btnLoadPhoto.setOnClickListener {
            loadPhotoFromUrl()
        }
        adressphoto.setOnClickListener {
            gotoadress()
        }
    }
    private fun gotozakazy() {
        val intent = Intent(this, OrdersActivity::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
        finish()
    }
    private fun gotoadress() {
        val intent = Intent(this, Adresses::class.java).apply { putExtra("USER_ID", userId) }
        startActivity(intent)
        finish()
    }
    private fun loadPhotoFromUrl() {
        val photoUrl = url.text.toString().trim()
        if (photoUrl.isNotEmpty()) {
            Picasso.get()
                .load(photoUrl)
                .into(ivProfilePhoto)
            showToast("Фото загружено")
        } else {
            showToast("Введите URL фото")
        }
    }

    private fun loadUserProfile() {
        apiClient.getProfil(userId) { userData ->
            runOnUiThread {
                if (userData != null) {
                    etName.setText(userData.surname+" "+userData.name + " "+userData.patronymic)
                    etEmail.setText(userData.email)
                    phone.setText(userData.phone)
                    currentUser=userData
                    if (!userData.photoMobile.isNullOrEmpty()) {
                        url.setText(userData.photoMobile)
                        Picasso.get()
                            .load(userData.photoMobile)
                            .into(ivProfilePhoto)
                    }
                } else {
                    showToast("Ошибка загрузки профиля")
                }
            }
        }
    }

    private fun getUpdatedData(): User? {
        val fio = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = phone.text.toString().trim()
        val photoUrl = url.text.toString().trim()
        val namePattern = Pattern.compile("^[\\p{L} \\-]+\$")
        if (!namePattern.matcher(fio).matches()) {
            showToast("ФИО может содержать только буквы, пробелы, дефисы и апострофы")
            return null
        }

        val parts = fio.split(" ").filter { it.isNotBlank() }
        if (parts.size < 2) {
            showToast("Введите фамилию и имя через пробел")
            return null
        }

        for (part in parts) {
            if (part.length < 2) {
                showToast("Каждая часть ФИО должна содержать минимум 2 символа")
                return null
            }
            if (part.length > 20) {
                showToast("Каждая часть ФИО не должна превышать 20 символов")
                return null
            }
        }
        val fioParts = fio.split(" ").filter { it.isNotBlank() }
        val surname = if (fioParts.isNotEmpty()) fioParts[0] else ""
        val name = if (fioParts.size > 1) fioParts[1] else ""
        val patronymic = if (fioParts.size > 2) fioParts[2] else ""

        return User(
            surname = if (surname.isNotEmpty()) surname else null,
            name = if (name.isNotEmpty()) name else null,
            patronymic = if (patronymic.isNotEmpty()) patronymic else null,
            email = email,
            phone = if (phone.isNotEmpty()) phone else null,
            photoMobile = if (photoUrl.isNotEmpty()) photoUrl else null,
            id = userId,
            password = currentUser?.password ,
            role = currentUser?.role,
            status = currentUser?.status,
            adresses = currentUser?.adresses
        )
    }

    private fun validateProfileData(name: String?, email: String?, phone: String?): Boolean {
        if (name.isNullOrEmpty() || email.isNullOrEmpty() || phone.isNullOrEmpty()) {
            showToast("Заполните все поля")
            return false
        }
        if (!email.contains("@")) {
            showToast("Введите корректный email")
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
        if (cleanPhone[0] !='8') {
            showToast("Номер телефона должен начинаться с 8")
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
            "^[A-Za-z0-9+_.!-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        )
        if (!emailPattern.matcher(email).matches()) {
            showToast("Введите корректный email адрес")
            return false
        }



        return true
    }

    private fun updateProfile() {
        val updateRequest = getUpdatedData()
        if(updateRequest==null){
            return
        }
        if (!validateProfileData(updateRequest.name, updateRequest.email, updateRequest.phone)) {
            return
        }

        apiClient.updateProfile( updateRequest) { userData ->
            runOnUiThread {
                if (userData != null) {
                    showToast("Профиль успешно обновлен")
                    if (!userData.photoMobile.isNullOrEmpty()) {
                        url.setText(userData.photoMobile)
                        Picasso.get()
                            .load(userData.photoMobile)
                            .into(ivProfilePhoto)
                    }
                } else {
                    showToast("Ошибка обновления")
                }
            }
        }
    }
    private fun deleteProfile() {

        apiClient.deleteUser( currentUser) { userData ->
            runOnUiThread {
                if (userData != null) {
                    showToast("Профиль успешно удален")
                    var intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    showToast("Ошибка обновления")
                }
            }
        }
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}