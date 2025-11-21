package com.example.foodcare.api

import com.android.identity.util.UUID
import com.example.foodcare.domain.entity.UserProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class RegisterRequest(val user_login: String, val password: String, val user_name: String)
data class LoginRequest(val user_login: String, val password: String)
data class ApiResponse(val user_id: String?, val access_token: String?, val token_type: String?)

data class RecipesRequest(val user_id: String)

data class RecipeResponse(
    val name: String,
    val complexity: String,
    val time: String,
    val recipe: String
)

interface ApiService {

    @POST("/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse

    @POST("/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse

    @POST("/GPT/generate_recipes")
    suspend fun generateRecipes(
        @Query("user_id") userId: UUID
    ): Response<List<RecipeResponse>>

    @GET("order/getallproductsuser")
    suspend fun getAllProductsOfUser(
        @Query("user_id") userId: String
    ): List<UserProductResponse>
}
