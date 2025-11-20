package com.example.foodcare.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodcare.api.ApiService
import com.example.foodcare.api.LoginRequest
import com.example.foodcare.api.RegisterRequest
import com.example.foodcare.api.RetrofitClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ApiService
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
                val response = api.register(request)
                _registerState.value = AuthState.Success(response.message)
            } catch (e: Exception) {
                _registerState.value = AuthState.Error("Ошибка: ${e.message}")
            }
        }
    }


    fun login(userLogin: String, password: String) {
        if (userLogin.isBlank() || password.isBlank()) {
            _loginState.value = AuthState.Error("Заполните все поля")
            return
        }

        viewModelScope.launch {
            _loginState.value = AuthState.Loading
            try {
                val response = api.login(
                    LoginRequest(user_login = userLogin, password = password)
                )
                _loginState.value = AuthState.Success(response.token ?: "Успешный вход")
            } catch (e: Exception) {
                _loginState.value = AuthState.Error("Ошибка входа: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = AuthState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = AuthState.Idle
    }
}
