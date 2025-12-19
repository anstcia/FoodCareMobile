package com.example.foodcare.data.mapper


import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.domain.entity.UserProductResponse

fun UserProductResponse.toDomain() = UserProduct(
    orderProductId = order_product.order_product_id,
    productId = product.product_id,
    name = product.product_name,
    desc = product.product_desc,
    endDate = order_product.product_date_end,
    barcode = product.product_barcode
)

