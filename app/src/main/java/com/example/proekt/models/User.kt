package com.example.proekt.models

data class User(
    val id: Int?=null,
    var surname: String?=null,
    var name: String?=null,
    var patronymic: String?=null,
    var email: String,
    var password: String?=null,
    var phone: String?=null,
    var role: String? = null,
    var status: String? = null,
    var photoMobile: String? = null,
    var adresses: List<String>? = emptyList()
)
