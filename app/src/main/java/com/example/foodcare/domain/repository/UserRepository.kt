package com.example.foodcare.domain.repository

import com.example.foodcare.domain.entity.User
import com.example.foodcare.domain.entity.UserProduct

interface UserRepository {
    suspend fun getUser(userId: String): User

}