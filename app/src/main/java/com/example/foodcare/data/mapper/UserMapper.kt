package com.example.foodcare.data.mapper

import com.example.foodcare.data.remote.UserResponse
import com.example.foodcare.domain.entity.User

fun UserResponse.toDomain() = User(
    userName = user_name,
    userLogin = user_login
)