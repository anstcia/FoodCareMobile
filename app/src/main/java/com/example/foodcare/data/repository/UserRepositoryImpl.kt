package com.example.foodcare.data.repository

import com.example.foodcare.api.ApiService
import com.example.foodcare.data.mapper.toDomain
import com.example.foodcare.domain.entity.User
import com.example.foodcare.domain.repository.UserRepository
import javax.inject.Inject


class UserRepositoryImpl @Inject constructor(
    private val api: ApiService): UserRepository{
    override suspend fun getUser(userId: String): User = try {
        val response = api.getUserById(userId)
        if (response.isEmpty()) error("User not found")
        response.first().toDomain()
    }  catch (e: IllegalStateException) {
        throw e
    }
}