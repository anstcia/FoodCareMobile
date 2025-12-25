package com.example.foodcare.domain.usecase

import com.example.foodcare.domain.entity.User
import com.example.foodcare.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository)
{
    suspend operator fun invoke(userId: String): User = userRepository.getUser(userId = userId)
}