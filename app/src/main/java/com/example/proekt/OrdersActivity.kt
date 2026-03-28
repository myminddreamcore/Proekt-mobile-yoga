package com.example.proekt

import Order
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.adapters.OrderAdapter
import com.example.proekt.api.ApiClient
import com.example.proekt.models.Basket
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrdersActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var rvOrders: RecyclerView
    private lateinit var nazad: ImageView
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var apiClient: ApiClient
    private var userId: Int = 0

    private val activeOrders = mutableListOf<Order>()
    private val completedOrders = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        userId = intent.getIntExtra("USER_ID", 0)



        if (userId == 0) {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        apiClient = ApiClient()

        orderAdapter = OrderAdapter(
            orders = emptyList(),
            onMoreClick = { order, view ->
                showOrderDetails(order)
            }
        )

        initViews()
        setupRecyclerView()
        setupTabs()
        loadOrders(userId)
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
        rvOrders = findViewById(R.id.rvOrders)
        nazad = findViewById<ImageView>(R.id.nazad)
        nazad.setOnClickListener { gotoProfil() }
    }
    private fun gotoProfil(){
        var intent = Intent(this, Profil::class.java).apply { putExtra("USER_ID",userId) }
        startActivity(intent)
        finish()
    }
    private fun setupRecyclerView() {
        rvOrders.layoutManager = LinearLayoutManager(this)
        rvOrders.adapter = orderAdapter
    }

    private fun setupTabs() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {


                when (tab?.position) {
                    0 -> loadActiveOrders(userId)
                    1 -> loadCompletedOrders(userId)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadOrders(userId: Int) {
        loadActiveOrders(userId)
    }

    private fun loadActiveOrders(userId: Int) {


        apiClient.getActiveOrders(userId) { success, orders ->
            runOnUiThread {

                if (success && orders != null) {
                    activeOrders.clear()
                    activeOrders.addAll(orders)
                    orderAdapter.updateOrders(activeOrders)

                    if (orders.isEmpty()) {
                        showEmptyState("У вас нет активных заказов")
                    }
                } else {
                    Toast.makeText(
                        this@OrdersActivity,
                        "Ошибка загрузки активных заказов",
                        Toast.LENGTH_SHORT
                    ).show()
                    showEmptyState("Не удалось загрузить заказы")
                }
            }
        }
    }

    private fun loadCompletedOrders(userId: Int) {


        apiClient.getCompletedOrders(userId) { success, orders ->
            runOnUiThread {

                if (success && orders != null) {
                    completedOrders.clear()
                    completedOrders.addAll(orders)
                    orderAdapter.updateOrders(completedOrders)

                    if (orders.isEmpty()) {
                        showEmptyState("У вас нет завершенных заказов")
                    }
                } else {
                    Toast.makeText(
                        this@OrdersActivity,
                        "Ошибка загрузки завершенных заказов",
                        Toast.LENGTH_SHORT
                    ).show()
                    showEmptyState("Не удалось загрузить заказы")
                }
            }
        }
    }


    private fun showOrderDetails(order: Order) {
        val itemsText = order.order?.joinToString("\n") {
            "${it.name} x${it.col_tovar} - ${it.price * it.col_tovar} руб"
        } ?: "Товары не указаны"

        val totalPrice = order.order?.sumOf { it.price * it.col_tovar } ?: 0.0

        val message = """
        Заказ #${order.id}
        Статус: ${order.status ?: "Не указан"}
        Способ получения: ${order.typeOrder ?: "Не указан"}
        Адрес: ${order.adress ?: "Не указан"}
        Оплата: ${order.typePay ?: "Не указан"}
        Дата: ${order.date?.let {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(it)
        } ?: "Не указана"}
        ${if (!order.description.isNullOrEmpty()) "Описание: ${order.description}" else ""}
        
        Товары:
        $itemsText
        
        Итого: ${totalPrice.toInt()} руб
        """.trimIndent()

        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Детали заказа")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }


    private fun showEmptyState(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}