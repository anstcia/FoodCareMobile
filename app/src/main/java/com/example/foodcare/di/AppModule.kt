package com.example.foodcare.di

import android.content.Context
import com.example.foodcare.api.ApiService
import com.example.foodcare.data.repository.ProductRepositoryImpl
import com.example.foodcare.domain.repository.ProductRepository
import com.example.foodcare.presentation.viewmodel.UserPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext context: Context): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        apiService: ApiService,
        userPreferences: UserPreferences
    ): ProductRepository {
        return ProductRepositoryImpl(apiService, userPreferences)
    }
}

