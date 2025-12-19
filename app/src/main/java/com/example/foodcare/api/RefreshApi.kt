package com.example.foodcare.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RefreshApi {

    @POST("/refresh")
    fun refreshSync(
        @Body request: RefreshRequest
    ): Call<ApiResponse>

}