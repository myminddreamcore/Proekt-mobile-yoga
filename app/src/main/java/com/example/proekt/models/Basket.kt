
package com.example.proekt.models

import java.io.Serializable

data class Basket(
    val id_busket: Int,
    val id_user: Int,
    val col_tovar: Int,
    val id_tovar: Int,
    val photoMobile: String,
    val price: Double,
    val name: String
) : Serializable