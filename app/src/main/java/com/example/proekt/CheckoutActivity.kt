package com.example.proekt

import Order
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proekt.adapters.AdressAdapter
import com.example.proekt.api.ApiClient
import com.example.proekt.models.Basket
import com.example.proekt.models.User
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var imageViewBack: ImageView
    private lateinit var rgDeliveryType: RadioGroup
    private lateinit var llPickup: LinearLayout
    private lateinit var llDelivery: LinearLayout
    private lateinit var spinnerPickupPoints: Spinner
    private lateinit var rvAddresses: RecyclerView
    private lateinit var btnAddAddress: AppCompatButton
    private lateinit var rgPaymentType: RadioGroup
    private lateinit var llCardDetails: LinearLayout
    private lateinit var etCardNumber: EditText
    private lateinit var etCardMonth: EditText
    private lateinit var etCardYear: EditText
    private lateinit var etCardCVV: EditText
    private lateinit var tvProductsTotal: TextView
    private lateinit var tvDeliveryCost: TextView
    private lateinit var tvTotalCost: TextView
    private lateinit var btnCheckout: androidx.appcompat.widget.AppCompatButton

    private lateinit var addressAdapter: AdressAdapter
    private val addresses = mutableListOf<String>()
    private val cartItems = mutableListOf<Basket>()
    private var selectedAddress: String? = null
    private var selectedPickupPoint: String = ""
    private var totalPrice = 0.0
    private var userId: Int = 0
    private lateinit var apiClient: ApiClient
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        userId = intent.getIntExtra("USER_ID", 0)
        val items = intent.getSerializableExtra("CART_ITEMS") as? ArrayList<Basket>
        items?.let { cartItems.addAll(it) }

        apiClient = ApiClient()

        initViews()
        setupRecyclerView()
        setupListeners()
        loadCartItems()
        loadUserData()
        setupPickupSpinner()
        updateTotals()
    }

    private fun initViews() {
        imageViewBack = findViewById(R.id.nazad)
        rgDeliveryType = findViewById(R.id.rgDeliveryType)
        llPickup = findViewById(R.id.llPickup)
        llDelivery = findViewById(R.id.llDelivery)
        spinnerPickupPoints = findViewById(R.id.spinnerPickupPoints)
        rvAddresses = findViewById(R.id.rvAddresses)
        btnAddAddress = findViewById(R.id.btnAddAddress)
        rgPaymentType = findViewById(R.id.rgPaymentType)
        llCardDetails = findViewById(R.id.llCardDetails)
        etCardNumber = findViewById(R.id.etCardNumber)
        etCardMonth = findViewById(R.id.etCardMonth)
        etCardYear = findViewById(R.id.etCardYear)
        etCardCVV = findViewById(R.id.etCardCVV)
        tvProductsTotal = findViewById(R.id.tvProductsTotal)
        tvDeliveryCost = findViewById(R.id.tvDeliveryCost)
        tvTotalCost = findViewById(R.id.tvTotalCost)
        btnCheckout = findViewById(R.id.btnCheckout)
    }

    private fun setupRecyclerView() {
        addressAdapter = AdressAdapter(addresses) { address ->
            selectedAddress = address
        }

        rvAddresses.layoutManager = LinearLayoutManager(this)
        rvAddresses.adapter = addressAdapter
    }

    private fun setupListeners() {
        imageViewBack.setOnClickListener {
            onBackPressed()
        }

        rgDeliveryType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbPickup -> {
                    llPickup.visibility = LinearLayout.VISIBLE
                    llDelivery.visibility = LinearLayout.GONE
                    tvDeliveryCost.text = "0 руб"
                    updateTotals()
                }
                R.id.rbDelivery -> {
                    llPickup.visibility = LinearLayout.GONE
                    llDelivery.visibility = LinearLayout.VISIBLE
                    tvDeliveryCost.text = "200 руб"
                    updateTotals()
                }
            }
        }

        rgPaymentType.setOnCheckedChangeListener { _, checkedId ->
            llCardDetails.visibility = if (checkedId == R.id.rbCard) {
                LinearLayout.VISIBLE
            } else {
                LinearLayout.GONE
            }
        }
        imageViewBack.setOnClickListener { gotoKorzina() }
        btnAddAddress.setOnClickListener {
            showAddAddressDialog()
        }

        btnCheckout.setOnClickListener {
            placeOrder()
        }
    }
    private fun gotoKorzina(){
        val intent = Intent(this, BasketActivity::class.java).apply {
            putExtra("USER_ID",userId)
        }
        startActivity(intent)
        finish()
    }
    private fun loadCartItems() {
        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        totalPrice = cartItems.sumOf { it.price * it.col_tovar }
        tvProductsTotal.text = "${totalPrice.toInt()} руб"
        updateTotals()
    }

    private fun loadUserData() {
        if (userId == 0) {
            Toast.makeText(this, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
            return
        }

        apiClient.getProfil(userId) { user ->
            runOnUiThread {
                user?.let {
                    currentUser = it
                    addresses.clear()
                    it.adresses?.let { userAddresses ->
                        addresses.addAll(userAddresses)
                    }
                    addressAdapter.notifyDataSetChanged()
                    if (addresses.isNotEmpty()) {
                        selectedAddress = addresses[0]
                    }
                }
            }
        }
    }

    private fun setupPickupSpinner() {
        val pickupPoints = arrayOf(
            "Пункт выдачи 1: ул. Центральная, 10",
            "Пункт выдачи 2: пр. Мира, 25",
            "Пункт выдачи 3: ул. Садовая, 5"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, pickupPoints)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPickupPoints.adapter = adapter

        spinnerPickupPoints.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                selectedPickupPoint = pickupPoints[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        if (pickupPoints.isNotEmpty()) {
            selectedPickupPoint = pickupPoints[0]
        }
    }

    private fun showAddAddressDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_adress, null)
        val etNewAddress = dialogView.findViewById<EditText>(R.id.etNewAddress)

        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Добавить новый адрес")
            .setView(dialogView)
            .setPositiveButton("Добавить") { _, _ ->
                val newAddress = etNewAddress.text.toString().trim()
                if (newAddress.isNotEmpty()) {
                    addresses.add(newAddress)
                    addressAdapter.notifyDataSetChanged()
                    selectedAddress = newAddress
                    Toast.makeText(this, "Адрес добавлен", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .create()

        dialog.show()
    }

    private fun updateTotals() {
        val deliveryCost = if (rgDeliveryType.checkedRadioButtonId == R.id.rbDelivery) 200.0 else 0.0
        val total = totalPrice + deliveryCost

        tvDeliveryCost.text = "${deliveryCost.toInt()} руб"
        tvTotalCost.text = "${total.toInt()} руб"
    }

    private fun placeOrder() {
        if (rgDeliveryType.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Выберите способ получения", Toast.LENGTH_SHORT).show()
            return
        }

        if (rgDeliveryType.checkedRadioButtonId == R.id.rbPickup && selectedPickupPoint.isEmpty()) {
            Toast.makeText(this, "Выберите пункт выдачи", Toast.LENGTH_SHORT).show()
            return
        }

        if (rgDeliveryType.checkedRadioButtonId == R.id.rbDelivery && selectedAddress == null) {
            Toast.makeText(this, "Выберите адрес доставки", Toast.LENGTH_SHORT).show()
            return
        }

        if (rgPaymentType.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Выберите способ оплаты", Toast.LENGTH_SHORT).show()
            return
        }

        if (rgPaymentType.checkedRadioButtonId == R.id.rbCard && !validateCardDetails()) {
            Toast.makeText(this, "Заполните данные карты", Toast.LENGTH_SHORT).show()
            return
        }

        val deliveryType = if (rgDeliveryType.checkedRadioButtonId == R.id.rbPickup) "Самовывоз" else "Доставка"
        val deliveryAddress = if (deliveryType == "Самовывоз") selectedPickupPoint else selectedAddress

        val paymentType = if (rgPaymentType.checkedRadioButtonId == R.id.rbCash) "Наличные" else "Карта"

        val deliveryCost = if (deliveryType == "Доставка") 200.0 else 0.0


        val order = Order(
            id = 0,
            userId = userId,
            order = cartItems,
            date = Date(),
            status = "В обработке",
            adress = deliveryAddress ?: "",
            typeOrder = deliveryType,
            typePay = paymentType,
            description = "Заказ от ${Date()}"
        )

        apiClient.createOrder(order) { success, createdOrder ->
            runOnUiThread {
                if (success && createdOrder != null) {
                    Toast.makeText(this, "Заказ #${createdOrder.id} успешно оформлен!", Toast.LENGTH_SHORT).show()

                    clearCartAfterOrder()

                    val intent = Intent(this, OrdersActivity::class.java).apply {
                        putExtra("USER_ID", userId)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Ошибка оформления заказа", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearCartAfterOrder() {
        cartItems.forEach { basketItem ->
            apiClient.removeFromCart(basketItem.id_busket) { success, _ ->
                if (!success) {
                    println("Ошибка удаления товара из корзины: ${basketItem.id_busket}")
                }
            }
        }
    }

    private fun validateCardDetails(): Boolean {
        val cardNumber = etCardNumber.text.toString().replace(" ", "")
        val month = etCardMonth.text.toString()
        val year = etCardYear.text.toString()
        val cvv = etCardCVV.text.toString()

        if (cardNumber.length != 16 || !cardNumber.all { it.isDigit() }) {
            etCardNumber.error = "Введите 16 цифр номера карты"
            return false
        }

        if (month.length != 2 || !month.all { it.isDigit() } || month.toInt() !in 1..12) {
            etCardMonth.error = "Введите месяц (01-12)"
            return false
        }

        if (year.length != 2 || !year.all { it.isDigit() }) {
            etCardYear.error = "Введите год (2 цифры)"
            return false
        }

        if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
            etCardCVV.error = "Введите 3 цифры CVV"
            return false
        }

        return true
    }
}