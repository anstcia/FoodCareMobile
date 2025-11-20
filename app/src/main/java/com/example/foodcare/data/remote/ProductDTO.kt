package com.example.foodcare.data.remote

data class ProductDto(
    val product_id: String,
    val product_name: String,
    val product_thumbnail: String?,
    val product_type: String,
    val product_desc: String,
    val product_rating: Double?,
    val product_barcode: Long
)

