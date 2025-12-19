package com.example.foodcare.domain.entity

data class UserProduct(
    val orderProductId: String,
    val productId: String,
    val name: String,
    val desc: String?,
    val endDate: String?,
    val barcode: Long
)
