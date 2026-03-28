package com.example.proekt

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.adapters.ProductAdapter
import com.example.proekt.api.ApiClient
import com.example.proekt.models.Basket
import com.example.proekt.models.Product
import com.google.android.material.bottomnavigation.BottomNavigationView

class CatalogProduct : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private lateinit var adapter: ProductAdapter
    private val apiClient = ApiClient()
    private lateinit var etSearch: EditText
    private lateinit var spCategory: Spinner
    private lateinit var spSortPrice: Spinner
    private lateinit var spSortRating: Spinner

    private var allProducts: List<Product> = emptyList()

    private val categories = mutableListOf("Все разделы")
    private val priceSortOptions = listOf("Цена", "Цена ↑", "Цена ↓")
    private val ratingSortOptions = listOf("Рейтинг", "Рейтинг ↑", "Рейтинг ↓")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.catalogproduct)

        initViews()
        setupRecyclerView()
        setupSearchAndSorting()
        setupBottomNav()
        loadProducts()
    }

    private fun initViews() {
        rvProducts = findViewById(R.id.rvProducts)
        etSearch = findViewById(R.id.etSearch)
        spCategory = findViewById(R.id.spcategory)
        spSortPrice = findViewById(R.id.spprice)
        spSortRating = findViewById(R.id.sprating)
    }
    private fun setupRecyclerView() {
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        adapter = ProductAdapter(
            products = emptyList(),
            onItemClick = { product ->
                val intent = Intent(this, ProductDetailActivity::class.java)
                intent.putExtra("PRODUCT_ID", product.id)
                startActivity(intent)
            },
            onAddToCart = { product ->
                addToCart(product)
            }
        )
        rvProducts.adapter = adapter
    }

    private fun addToCart(product: Product) {
        val basketItem = Basket(
            id_busket = 0,
            id_user = StaticUser.userId,
            col_tovar = 1,
            id_tovar = product.id,
            photoMobile = product.photoMobile ?: "",
            price = product.price,
            name = product.name
        )

        apiClient.addToCart(basketItem) { success, message ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(
                        this,
                        "Товар '${product.name}' добавлен в корзину",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        message ?: "Ошибка добавления в корзину",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupSearchAndSorting() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                applyFiltersAndSorting()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        setupSpinner(spCategory, categories)
        setupSpinner(spSortPrice, priceSortOptions)
        setupSpinner(spSortRating, ratingSortOptions)

        spCategory.onItemSelectedListener = createSpinnerListener()
        spSortPrice.onItemSelectedListener = createSpinnerListener()
        spSortRating.onItemSelectedListener = createSpinnerListener()
    }

    private fun setupSpinner(spinner: Spinner, items: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun createSpinnerListener() = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
            applyFiltersAndSorting()
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    private fun setupBottomNav() {
        val bottomNav: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNav.selectedItemId = R.id.navigation_home
        bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_profile -> {
                    val userId = intent.getIntExtra("USER_ID", 0)
                    if (userId != 0) {
                        Intent(this@CatalogProduct, Profil::class.java).apply {
                            putExtra("USER_ID", userId)
                            startActivity(this)
                            finish()
                        }
                    }
                    true
                }
                R.id.navigation_korzina -> {
                    val userId = intent.getIntExtra("USER_ID", 0)
                    if (userId != 0) {
                        Intent(this, BasketActivity::class.java).apply {
                            putExtra("USER_ID", userId)
                            startActivity(this)
                        }
                    }
                    true
                }
                R.id.navigation_home -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun loadProducts() {
        apiClient.getAllProducts { success, products ->
            runOnUiThread {
                if (success && products != null) {
                    allProducts = products
                    updateCategories(products)
                    applyFiltersAndSorting()
                } else {
                    Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateCategories(products: List<Product>) {
        val uniqueCategories = products.map { it.razdel }.distinct()
        categories.clear()
        categories.add("Все разделы")
        categories.addAll(uniqueCategories)
        (spCategory.adapter as ArrayAdapter<String>).notifyDataSetChanged()
    }

    private fun applyFiltersAndSorting() {
        if (allProducts.isEmpty()) return

        var filtered = allProducts

        val search = etSearch.text.toString().trim().lowercase()
        if (search.isNotEmpty()) {
            filtered = filtered.filter { it.name.lowercase().contains(search) }
        }

        val category = spCategory.selectedItem as String
        if (category != "Все разделы") {
            filtered = filtered.filter { it.razdel == category }
        }

        val priceSort = spSortPrice.selectedItem as String
        filtered = when (priceSort) {
            "Цена ↑" -> filtered.sortedBy { it.price }
            "Цена ↓" -> filtered.sortedByDescending { it.price }
            else -> filtered
        }

        val ratingSort = spSortRating.selectedItem as String
        filtered = when (ratingSort) {
            "Рейтинг ↑" -> filtered.sortedBy { it.rating }
            "Рейтинг ↓" -> filtered.sortedByDescending { it.rating }
            else -> filtered
        }

        adapter.updateProducts(filtered)
    }
}