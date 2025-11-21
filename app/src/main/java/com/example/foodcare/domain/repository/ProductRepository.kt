package com.example.foodcare.domain.repository

import com.example.foodcare.domain.entity.UserProduct

interface ProductRepository {
    suspend fun getUserProducts(): List<UserProduct>
}