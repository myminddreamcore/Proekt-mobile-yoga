package com.example.proekt

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.adapters.BasketAdapter
import com.example.proekt.api.ApiClient
import com.example.proekt.models.Basket
import com.google.android.material.bottomnavigation.BottomNavigationView

class BasketActivity : AppCompatActivity() {

    private lateinit var rvCart: RecyclerView
    private lateinit var adapter: BasketAdapter
    private lateinit var tvTotalCount: TextView
    private lateinit var tvTotalPrice: TextView
    private lateinit var btnOformit: AppCompatButton
    private val apiClient = ApiClient()
    private var userId: Int = 0
    private val cartItems = mutableListOf<Basket>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.basket)

        userId = intent.getIntExtra("USER_ID", 0)
        initViews()
        setupRecyclerView()
        setupBottomNav()
        loadCart()
    }

    private fun initViews() {
        rvCart = findViewById(R.id.rvProducts)
        tvTotalCount = findViewById(R.id.count)
        tvTotalPrice = findViewById(R.id.allcost)
        btnOformit = findViewById(R.id.btnOformit)
    }

    private fun setupRecyclerView() {
        rvCart.layoutManager = LinearLayoutManager(this)

        adapter = BasketAdapter(
            cartItems,
            onQuantityIncrease = { basketItem ->
                increaseQuantity(basketItem)
            },
            onQuantityDecrease = { basketItem ->
                decreaseQuantity(basketItem)
            },
            onItemDelete = { basketItem ->
                deleteItem(basketItem)
            }
        )
        btnOformit.setOnClickListener { gotoOformit() }
        rvCart.adapter = adapter
    }
    private fun gotoOformit() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, CheckoutActivity::class.java).apply {
            putExtra("USER_ID", userId)
            putExtra("CART_ITEMS", ArrayList(cartItems))
        }
        startActivity(intent)
    }
    private fun setupBottomNav() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.navigation_korzina
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_profile -> {
                    if (userId != 0) {
                        Intent(this, Profil::class.java).apply {
                            putExtra("USER_ID", userId)
                            startActivity(this)
                            finish()
                        }
                    }
                    true
                }
                R.id.navigation_korzina -> {

                    true
                }
                R.id.navigation_home -> {
                    val userId = intent.getIntExtra("USER_ID", 0)
                    if (userId != 0) {
                        Intent(this, CatalogProduct::class.java).apply {
                            putExtra("USER_ID", userId)
                            startActivity(this)
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadCart() {
        if (userId == 0) {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
            return
        }

        apiClient.getAllCartItems(userId) { success, items ->
            runOnUiThread {
                if (success && items != null) {
                    cartItems.clear()
                    cartItems.addAll(items)
                    adapter.updateCartItems(items)
                    updateTotals()
                } else {
                    cartItems.clear()
                    adapter.updateCartItems(emptyList())
                    updateTotals()
                    Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun increaseQuantity(basketItem: Basket) {
        apiClient.increaseQuantity(basketItem.id_busket) { success, message ->
            runOnUiThread {
                if (success) {
                    val index = cartItems.indexOfFirst { it.id_busket == basketItem.id_busket }
                    if (index != -1) {
                        val updatedItem = cartItems[index].copy(col_tovar = cartItems[index].col_tovar + 1)
                        cartItems[index] = updatedItem
                        adapter.updateCartItems(cartItems)
                        updateTotals()
                    }
                    Toast.makeText(this, message ?: "Количество увеличено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message ?: "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun decreaseQuantity(basketItem: Basket) {
        apiClient.decreaseQuantity(basketItem.id_busket) { success, message ->
            runOnUiThread {
                if (success) {
                    val index = cartItems.indexOfFirst { it.id_busket == basketItem.id_busket }
                    if (index != -1) {
                        val newQuantity = cartItems[index].col_tovar - 1
                        if (newQuantity > 0) {
                            val updatedItem = cartItems[index].copy(col_tovar = newQuantity)
                            cartItems[index] = updatedItem
                        } else {
                            cartItems.removeAt(index)
                        }
                        adapter.updateCartItems(cartItems)
                        updateTotals()
                    }
                    Toast.makeText(this, message ?: "Количество уменьшено", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message ?: "Ошибка", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteItem(basketItem: Basket) {
        apiClient.removeFromCart(basketItem.id_busket) { success, message ->
            runOnUiThread {
                if (success) {
                    val index = cartItems.indexOfFirst { it.id_busket == basketItem.id_busket }
                    if (index != -1) {
                        cartItems.removeAt(index)
                        adapter.updateCartItems(cartItems)
                        updateTotals()
                    }
                    Toast.makeText(this, message ?: "Товар удален", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, message ?: "Ошибка удаления", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateTotals() {
        val totalCount = cartItems.sumOf { it.col_tovar }
        val totalPrice = cartItems.sumOf { it.price * it.col_tovar }

        tvTotalCount.text = "Всего: $totalCount"
        tvTotalPrice.text = "Итоговая стоимость: ${totalPrice.toInt()} руб."
    }
}