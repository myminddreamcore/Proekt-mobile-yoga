package com.example.proekt
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.proekt.api.ApiClient
import com.example.proekt.models.Basket
import com.example.proekt.models.Product
import com.squareup.picasso.Picasso

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var ivProductDetail: ImageView
    private lateinit var tvProductName: TextView
    private lateinit var tvCategory: TextView
    private lateinit var tvDescription: TextView
    private lateinit var tvCol: TextView
    private lateinit var tvPrice: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnAddToCart: AppCompatButton
    private lateinit var currentproduct: Product
    private val apiClient = ApiClient()
    public var productId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)
        productId = intent.getIntExtra("PRODUCT_ID", 0)
        initializeViews()
        loadProduct()
        setupClickListeners()
    }



    private fun initializeViews() {
        ivProductDetail = findViewById(R.id.ivProductDetail)
        tvProductName = findViewById(R.id.tvProductName)
        tvCategory = findViewById(R.id.tvCategory)
        tvDescription = findViewById(R.id.tvDescription)
        tvPrice = findViewById(R.id.tvPrice)
        btnBack = findViewById(R.id.btnBack)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        tvCol = findViewById(R.id.tvCol)
    }

    private fun setupClickListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnAddToCart.setOnClickListener {
            Toast.makeText(this, "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
            Add(productId)
        }
    }
    private fun Add(productId:Int){

            val basketItem = Basket(
                id_busket = 0,
                id_user = StaticUser.userId,
                col_tovar = 1,
                id_tovar = productId,
                photoMobile = currentproduct.photoMobile!!,
                price = currentproduct.price,
                name = currentproduct.name
            )

            apiClient.addToCart(basketItem) { success, message ->
                runOnUiThread {
                    if (success) {
                        Toast.makeText(this, message ?: "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, message ?: "Ошибка добавления", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    private fun loadProduct() {
        apiClient.getProduct(productId) { success, product ->
            runOnUiThread {
                if (success && product != null) {
                    currentproduct = product
                    displayProduct(product)
                } else {
                    Toast.makeText(this, "Ошибка загрузки товара", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun displayProduct(product: Product) {
        Picasso.get()
            .load(product.photoMobile)
            .placeholder(R.drawable.samolet)
            .error(R.drawable.logo)
            .into(ivProductDetail)

        tvProductName.text = product.name
        tvCol.text = "В наличии: ${product.col} "
        tvCategory.text = product.razdel
        tvDescription.text = product.description
        tvPrice.text = "${product.price} руб."
    }
}