package com.example.foodcare.presentation.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodcare.api.LoginRequest
import com.example.foodcare.api.RegisterRequest
import com.example.foodcare.api.RetrofitClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AuthViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _authResult = MutableStateFlow<String?>(null)
    val authResult: StateFlow<String?> = _authResult

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _registerState = MutableStateFlow<AuthState>(AuthState.Idle)
    val registerState: StateFlow<AuthState> = _registerState


    fun register(userLogin: String, userName: String, password: String, confirmPassword: String) {

        if (password != confirmPassword) {
            _registerState.value = AuthState.Error("Пароли не совпадают")
            return
        }

        viewModelScope.launch {
            _registerState.value = AuthState.Loading
            try {
                val request = RegisterRequest(
                    user_login = userLogin, // используем аргументы функции
                    password = password,
                    user_name = userName
                )
                val response = RetrofitClient.api.register(request)
                _registerState.value = AuthState.Success(response.message)
            } catch (e: Exception) {
                _registerState.value = AuthState.Error("Ошибка: ${e.message}")
            }
        }
    }


    fun login(userLogin: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val request = LoginRequest(user_login = userLogin, password = password)
                val response = RetrofitClient.api.login(request)
                _authResult.value = response.token ?: "Успешный вход"
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Ошибка входа: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _authResult.value = null
        _error.value = null
    }
}