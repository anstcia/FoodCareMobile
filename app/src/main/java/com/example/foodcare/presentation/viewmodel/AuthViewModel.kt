package com.example.foodcare.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodcare.api.LoginRequest
import com.example.foodcare.api.RegisterRequest
import com.example.foodcare.api.RetrofitClient
import com.example.foodcare.presentation.viewmodel.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _authResult = MutableStateFlow<String?>(null)
    val authResult: StateFlow<String?> = _authResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState

    private val _loginState = MutableStateFlow<AuthState>(AuthState.Idle)
    val loginState: StateFlow<AuthState> = _loginState

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    // ------------------ Регистрация ------------------
    fun register(userLogin: String, userName: String, password: String, confirmPassword: String) {

        if (password != confirmPassword) {
            _registerState.value = AuthState.Error("Пароли не совпадают")
            return
        }

        viewModelScope.launch {
            _registerState.value = AuthState.Loading

            try {
                val request = RegisterRequest(
                    user_login = userLogin,
                    password = password,
                    user_name = userName
                )

                val response = RetrofitClient.api.register(request)

                // user_id уже строка — сохраняем
                val id = response.user_id
                _userId.value = id

                // сохраняем в DataStore
                if (id != null) {
                    userPreferences.saveUserId(id)
                }

                _registerState.value = AuthState.Success("Регистрация успешна")

            } catch (e: Exception) {
                _registerState.value = AuthState.Error("Ошибка: ${e.message}")
            }
        }
    }

    // ------------------ Вход ------------------
    fun login(userLogin: String, password: String) {
        if (userLogin.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthState.Loading

            try {
                val response = RetrofitClient.api.login(
                    LoginRequest(user_login = userLogin, password = password)
                )

                val id = response.user_id
                _userId.value = id

                // сохраняем в DataStore
                if (id != null) {
                    userPreferences.saveUserId(id)
                }

                _loginState.value = AuthState.Success("Успешный вход")

            } catch (e: Exception) {
                _loginState.value = AuthState.Error("Ошибка входа: ${e.message}")
            }
        }
    }

    // ------------------ Остальные методы ------------------
    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = AuthState.Idle
    }

    fun logout() {
        viewModelScope.launch {
            userPreferences.saveUserId("") // очищаем
        }
        _userId.value = null
        _loginState.value = AuthState.Idle
        _registerState.value = AuthState.Idle
    }
}
