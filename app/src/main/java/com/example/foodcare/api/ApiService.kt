package com.example.foodcare.api
import com.example.foodcare.domain.entity.UserProductResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class RegisterRequest(val user_login: String, val password: String, val user_name: String)
data class LoginRequest(val user_login: String, val password: String)
data class ApiResponse(val message: String, val token: String?)

interface ApiService {
    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse


    @GET("order/getallproductsuser")
    suspend fun getAllProductsOfUser(
        @Query("user_id") userId: String
    ): List<UserProductResponse>

}
