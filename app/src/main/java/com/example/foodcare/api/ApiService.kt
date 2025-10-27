package com.example.foodcare.api
import retrofit2.http.Body
import retrofit2.http.POST

data class RegisterRequest(val user_login: String, val password: String, val user_name: String)
data class LoginRequest(val user_login: String, val password: String)
data class ApiResponse(val message: String, val token: String?)

interface ApiService {
    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse
}
