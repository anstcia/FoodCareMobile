package com.example.foodcare.api

import com.android.identity.util.UUID
import com.example.foodcare.data.remote.ProductDto
import com.example.foodcare.data.remote.UserResponse
import com.example.foodcare.domain.entity.UserProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Date

data class RegisterRequest(val user_login: String, val password: String, val user_name: String)
data class LoginRequest(val user_login: String, val password: String)
data class ApiResponse(
    val user_id: String?,
    val access_token: String?,
    val refresh_token: String?,
    val token_type: String?
)

data class RecipesRequest(val user_id: String)

data class RecipeResponse(
    val name: String,
    val complexity: String,
    val time: String,
    val recipe: String
)
data class RefreshRequest(
    val refresh_token: String
)


data class ProductRequest(val user_id: String,val scannedText: String)

data class BarcodeScanRequest(
    val barcode: String,
    val user_id: UUID
)

data class ProductResponse(
    val order_id: UUID,
    val product_id: UUID,
    val created_product: Boolean,
    val created_product_type: Boolean,
    val product: ProductDto
)

data class OrderProductCreate(
    val id_order: UUID,
    val id_product: UUID,
    val product_date_end: Date?,
    val product_date_start: Date
)

data class OrderProductResponse(
    val order_product_id: UUID,
    val product_date_start: Date?,
    val product_date_end: Date
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
        @Query("user_id") userId: String?
    ): List<UserProductResponse>

    @DELETE("/delete_order_product_by_id")
    suspend fun deleteProductOfUser(
        @Query("UUID") productId: String
    )

    @POST("/openfoodfacts/scan")
    suspend fun scanBarcode(
        @Query("user_id") userId: UUID,
        @Query("barcode") barcode: String,
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String
    ): Response<ProductResponse>

    @GET("/get_user_by_id/{user_id}")
    suspend fun getUserById(
        @Path("user_id") userId: String
    ): List<UserResponse>
}
