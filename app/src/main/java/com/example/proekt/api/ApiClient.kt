package com.example.proekt.api

import Order
import com.example.proekt.models.*
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken


class ApiClient {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:5090/api/User/"
        private const val BASE_URL_Tovar = "http://10.0.2.2:5090/api/Tover/"
        private const val BASE_URL_Basket = "http://10.0.2.2:5090/api/Basket/"
        private const val BASE_URL_ORDER = "http://10.0.2.2:5090/api/Order/"
        private const val TAG = "ApiClient"
        private val client = OkHttpClient()
        private val gson = Gson()

        private val JSON = "application/json; charset=utf-8".toMediaType()
    }

    fun register(user: User, callback: (User?) -> Unit) {
        val json = gson.toJson(user)
        Log.d(TAG, "Register JSON: $json")
        val body = json.toRequestBody(JSON)
        val request = Request.Builder()
            .url("${BASE_URL}Registration")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Register failed connection/internet: ${e.message}", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val statusCode = it.code
                    val responseBody = it.body?.string()
                    Log.d(TAG, "Register response code: $statusCode")
                    Log.d(TAG, "Register response body: $responseBody")
                    if (it.isSuccessful) {
                        Log.d(TAG, "Registration successful: $responseBody")
                        callback(user)
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }
    fun login(loginRequest: User, callback: (User?) -> Unit) {
        val url = "${BASE_URL}Authorization/${loginRequest.email}/${loginRequest.password}"
        Log.d(TAG, "Login URL: $url")

        val request = Request.Builder()
            .url(url)
            .post("".toRequestBody())
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Login failed: ${e.message}", e)
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val statusCode = it.code
                    val responseBody = it.body?.string()

                    Log.d(TAG, "Login response code: $statusCode")
                    Log.d(TAG, "Login response body: $responseBody")

                    if (it.isSuccessful && responseBody != null) {
                        try {
                            val authResponse = gson.fromJson(responseBody, User::class.java)
                            callback(authResponse)
                        } catch (e: Exception) {
                            Log.e(TAG, "Parse error: ${e.message}", e)
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }
    fun getProfil(userId: Int, callback: (User?) -> Unit) {
        val url = "$BASE_URL$userId"
        Log.d(TAG, "GetProfile URL: $url")

        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "GetProfile failed: ${e.message}")
                callback(null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val statusCode = it.code
                    val responseBody = it.body?.string()
                    Log.d(TAG, "GetProfile response code: $statusCode, body: $responseBody")

                    if (it.isSuccessful && responseBody != null) {
                        try {
                            val user = gson.fromJson(responseBody, User::class.java)
                            if (user.id != 0) {
                                callback(user)
                            } else {
                                callback(null)
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Parse error in getProfil: ${e.message}")
                            callback(null)
                        }
                    } else {
                        callback(null)
                    }
                }
            }
        })
    }
    fun updateProfile(
        updateRequest: User,
        callback: ( User?) -> Unit
    ) {

        val json = gson.toJson(updateRequest)
        val body = json.toRequestBody(JSON)
        val request = Request.Builder()
            .url("${BASE_URL}EditUser")
            .put(body)
            .build()

        client.newCall(request).enqueue(responseCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                callback( null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        val authResponse = gson.fromJson(responseBody, User::class.java)

                        callback( authResponse)
                    } else {
                        callback( null)
                    }
                }
            }
        })
    }
    fun deleteUser(
        updateRequest: User?,
        callback: ( User?) -> Unit
    ) {

        val json = gson.toJson(updateRequest)
        val body = json.toRequestBody(JSON)
        val request = Request.Builder()
            .url("${BASE_URL}DeleteUser")
            .put(body)
            .build()

        client.newCall(request).enqueue(responseCallback = object : Callback {
            override fun onFailure(call: Call, e: IOException) {

                callback( null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        val responseBody = it.body?.string()
                        val authResponse = gson.fromJson(responseBody, User::class.java)

                        callback( authResponse)
                    } else {

                        callback( null)
                    }
                }
            }
        })
    }


    fun getAllProducts(callback: (Boolean, List<Product>?) -> Unit) {
        val request = Request.Builder()
            .url("${BASE_URL_Tovar}GetAllActualTovars")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        try {
                            val responseBody = it.body?.string()
                            val type = object : TypeToken<List<Product>>() {}.type
                            val products = gson.fromJson<List<Product>>(responseBody, type)

                            callback(true, products)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callback(false, null)
                        }
                    } else {
                        callback(false, null)
                    }
                }
            }
        })
    }
    fun getProduct(productId: Int, callback: (Boolean, Product?) -> Unit) {
        val request = Request.Builder()
            .url("${BASE_URL_Tovar}Tovar/$productId")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        try {
                            val responseBody = it.body?.string()
                            val product = gson.fromJson(responseBody, Product::class.java)
                            callback(true, product)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            callback(false, null)
                        }
                    } else {
                        callback(false, null)
                    }
                }
            }
        })
    }

    fun getAllCartItems(userId: Int, callback: (Boolean, List<Basket>?) -> Unit) {

        val url = "${BASE_URL_Basket}UserAll_$userId"
        println("DEBUG: Запрос к URL: $url")
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("DEBUG: Ошибка сети: ${e.message}")
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    println("DEBUG: Ответ получен. Код: ${response.code}")

                    if (it.isSuccessful) {
                        try {
                            val responseBody = it.body?.string()
                            println("DEBUG: Тело ответа: $responseBody")

                            if (!responseBody.isNullOrEmpty()) {
                                val type = object : TypeToken<List<Basket>>() {}.type
                                val basketItems = gson.fromJson<List<Basket>>(responseBody, type)
                                callback(true, basketItems)
                            } else {
                                println("DEBUG: Пустое тело ответа")
                                callback(false, null)
                            }
                        } catch (e: Exception) {
                            println("DEBUG: Ошибка парсинга: ${e.message}")
                            callback(false, null)
                        }
                    } else {
                        println("DEBUG: Неуспешный ответ: ${response.code} ${response.message}")
                        callback(false, null)
                    }
                }
            }
        })
    }

    fun addToCart(basketItem: Basket, callback: (Boolean, String?) -> Unit) {
        val json = Gson().toJson(basketItem)
        println("DEBUG: Отправляемый JSON: $json")

        val body = json.toRequestBody(JSON)

        val url = "${BASE_URL_Basket}User/PutIntoBasket"
        println("DEBUG: URL запроса: $url")

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("DEBUG: Ошибка сети: ${e.message}")
                callback(false, "Ошибка сети: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val message = it.body?.string()
                    val success = it.isSuccessful

                    println("DEBUG: Ответ сервера: Код=${response.code}, Успех=$success, Сообщение=$message")

                    callback(success, message)
                }
            }
        })
    }

    fun increaseQuantity(basketId: Int, callback: (Boolean, String?) -> Unit) {

        val request = Request.Builder()
            .url("${BASE_URL_Basket}User/EditBasketPlusOne/$basketId")
            .put(RequestBody.create(null, ByteArray(0)))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val message = it.body?.string()
                    callback(it.isSuccessful, message)
                }
            }
        })
    }

    fun decreaseQuantity(basketId: Int, callback: (Boolean, String?) -> Unit) {
        val request = Request.Builder()
            .url("${BASE_URL_Basket}User/EditBasketMinusOne/$basketId")
            .put(RequestBody.create(null, ByteArray(0)))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val message = it.body?.string()
                    callback(it.isSuccessful, message)
                }
            }
        })
    }

    fun removeFromCart(basketId: Int, callback: (Boolean, String?) -> Unit) {
        val request = Request.Builder()
            .url("${BASE_URL_Basket}DeleteBasket/$basketId")
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val message = it.body?.string()
                    callback(it.isSuccessful, message)
                }
            }
        })
    }
    fun createOrder(order: Order, callback: (Boolean, Order?) -> Unit) {
        val gsonBuilder = GsonBuilder()
        gsonBuilder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val gson = gsonBuilder.create()
        val json = gson.toJson(order)
        println("DEBUG: Отправляю заказ: $json")

        val body = json.toRequestBody(JSON)

        val request = Request.Builder()
            .url("${BASE_URL_ORDER}PutNewOrder")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    val message = it.body?.string()

                    if (it.isSuccessful && message != null) {
                        try {
                            val createdOrder = gson.fromJson(message, Order::class.java)
                            callback(true, createdOrder)
                        } catch (e: Exception) {
                            callback(true, null)
                        }
                    } else {
                        callback(false, null)
                    }
                }
            }
        })
    }

    fun getActiveOrders(userId: Int, callback: (Boolean, List<Order>?) -> Unit) {
        val request = Request.Builder()
            .url("${BASE_URL_ORDER}OrderByIdUser/Actual/$userId")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        try {
                            val responseBody = it.body?.string()
                            val type = object : TypeToken<List<Order>>() {}.type
                            val orders = Gson().fromJson<List<Order>>(responseBody, type)
                            callback(true, orders)
                        } catch (e: Exception) {
                            callback(false, null)
                        }
                    } else {
                        callback(false, null)
                    }
                }
            }
        })
    }

    fun getCompletedOrders(userId: Int, callback: (Boolean, List<Order>?) -> Unit) {
        val request = Request.Builder()
            .url("${BASE_URL_ORDER}OrderByIdUser/DisActual/$userId")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (it.isSuccessful) {
                        try {
                            val responseBody = it.body?.string()
                            val type = object : TypeToken<List<Order>>() {}.type
                            val orders = Gson().fromJson<List<Order>>(responseBody, type)
                            callback(true, orders)
                        } catch (e: Exception) {
                            callback(false, null)
                        }
                    } else {
                        callback(false, null)
                    }
                }
            }
        })
    }



}
