package com.example.proekt
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proekt.api.ApiClient
import com.example.proekt.models.User

class Adresses : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var strelochka: ImageView
    private var userId = 0
    private var addressesList = ArrayList<String>()
    private lateinit var apiClient: ApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adresses)

        initializeViews()
        userId = intent.getIntExtra("USER_ID", 0)
        apiClient = ApiClient()

        setupClickListeners()
        loadUserProfile()
    }

    private fun initializeViews(){
        listView = findViewById(R.id.listview)
        strelochka = findViewById(R.id.strelochka)
    }

    private fun setupClickListeners(){
        strelochka.setOnClickListener { gotoprofil() }
    }

    private fun gotoprofil(){
        val intent = Intent(this, Profil::class.java).apply {
            putExtra("USER_ID", userId)
        }
        startActivity(intent)
        finish()
    }

    private fun loadUserProfile() {
        apiClient.getProfil(userId) { userData ->
            runOnUiThread {
                if (userData != null) {
                    addressesList.clear()

                    userData.adresses?.let { addresses ->
                        if (addresses.isNotEmpty()) {
                            addressesList.addAll(addresses)
                        }
                    }

                    updateAddressesList()

                } else {
                    Toast.makeText(this@Adresses, "Ошибка загрузки профиля", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateAddressesList() {
        if (addressesList.isEmpty()) {
            addressesList.add("Адреса не найдены")
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            addressesList
        )
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->
            if (addressesList.isNotEmpty() && addressesList[0] != "Адреса не найдены") {
                val selectedAddress = addressesList[position]
                Toast.makeText(this, "Выбран: $selectedAddress", Toast.LENGTH_SHORT).show()
            }
        }
    }
}