package com.example.foodcare.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodcare.api.ApiService
import com.example.foodcare.api.LoginRequest
import com.example.foodcare.api.RegisterRequest
import com.example.foodcare.domain.entity.User
import com.example.foodcare.domain.usecase.GetProductsUseCase
import com.example.foodcare.domain.usecase.GetUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject


// состояние ViewModel
data class AuthUiState(
    val isAuthenticated: Boolean = false,
    val userData: UserPreferences.UserData? = null,
    val loginState: AuthState = AuthState.Idle,
    val registerState: AuthState = AuthState.Idle
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val api: ApiService,
    private val userPreferences: UserPreferences,
    private val getUser: GetUserUseCase,
) : ViewModel() {

    // UI состояние
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // для отмены предыдущих запросов
    private var loginJob: Job? = null
    private var registerJob: Job? = null

    init {
        observeUserData()
        checkAuthStatus()
    }

    //Наблюдение за данными пользователя
    private fun observeUserData() {
        userPreferences.userData
            .onEach { userData ->
                _uiState.update { it.copy(userData = userData) }
            }
            .launchIn(viewModelScope)
    }

    //  Проверка авторизации при старте
    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val hasTokens = userPreferences.hasValidTokens()
                _uiState.update {
                    it.copy(isAuthenticated = hasTokens)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isAuthenticated = false)
                }
            }
        }
    }


    // Валидация
    private fun validateRegistration(
        userLogin: String,
        userName: String,
        password: String,
        confirmPassword: String
    ): String? {
        return when {
            userLogin.length < 3 -> "Логин должен быть не менее 3 символов"
            userName.isBlank() -> "Введите имя"
            password.length < 6 -> "Пароль должен быть не менее 6 символов"
            password != confirmPassword -> "Пароли не совпадают"
            else -> null
        }
    }

    private fun validateLogin(
        userLogin: String,
        password: String
    ): String? {
        return when {
            userLogin.isBlank() -> "Введите логин"
            password.isBlank() -> "Введите пароль"
            userLogin.length < 3 -> "Логин должен быть не менее 3 символов"
            password.length < 6 -> "Пароль должен быть не менее 6 символов"
            else -> null
        }
    }

    // регистрация
    fun register(
        userLogin: String,
        userName: String,
        password: String,
        confirmPassword: String
    ) {
        registerJob?.cancel()

        val error = validateRegistration(userLogin, userName, password, confirmPassword)
        if (error != null) {
            _uiState.update { it.copy(registerState = AuthState.Error(error)) }
            return
        }

        registerJob = viewModelScope.launch {
            _uiState.update {
                it.copy(registerState = AuthState.Loading)
            }

            try {
                api.register(
                    RegisterRequest(
                        user_login = userLogin,
                        password = password,
                        user_name = userName
                    )
                )

                _uiState.update {
                    it.copy(registerState = AuthState.Success("Регистрация успешна"))
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(registerState = AuthState.Error(handleNetworkError(e)))
                }
            }
        }
    }



    // логин
    fun login(userLogin: String, password: String) {
        loginJob?.cancel()

        val error = validateLogin(userLogin, password)
        if (error != null) {
            _uiState.update { it.copy(loginState = AuthState.Error(error)) }
            return
        }

        loginJob = viewModelScope.launch {
            _uiState.update {
                it.copy(loginState = AuthState.Loading)
            }

            try {
                val response = api.login(
                    LoginRequest(user_login = userLogin, password = password)
                )

                val userId = response.user_id ?: error("user_id is null")
                val access = response.access_token ?: error("access_token is null")
                val refresh = response.refresh_token ?: error("refresh_token is null")
                val user: User = getUser(userId)

                userPreferences.saveUser(
                    id = userId,
                    login = user.userLogin,
                    name = user.userName
                )

                userPreferences.saveTokens(access, refresh)

                _uiState.update {
                    it.copy(
                        loginState = AuthState.Success("Успешный вход"),
                        isAuthenticated = true
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(loginState = AuthState.Error(handleNetworkError(e)))
                }
            }
        }
    }


    // выход
    fun logout() {
        viewModelScope.launch {
            userPreferences.clearUser()
            _uiState.value = AuthUiState()
        }
    }


    /* обновление профиля
    fun updateUserName(newName: String) {
        viewModelScope.launch {
            val currentData = userPreferences.getUserDataOnce()
            if (currentData?.id != null && currentData.login != null) {
                userPreferences.saveUser(
                    id = currentData.id,
                    login = currentData.login,
                    name = newName
                )
            }
        }
    }
    */
    // обработка сетевых ошибок
    private fun handleNetworkError(e: Exception): String {
        return when (e) {
            is SocketTimeoutException -> "Таймаут соединения. Проверьте интернет"
            is ConnectException, is UnknownHostException -> "Нет соединения с интернетом"
            is HttpException -> {
                when (e.code()) {
                    400 -> "Неверные данные"
                    401 -> "Неверный логин или пароль"
                    403 -> "Доступ запрещен"
                    404 -> "Ресурс не найден"
                    409 -> "Пользователь уже существует"
                    500, 502, 503 -> "Ошибка сервера. Попробуйте позже"
                    else -> "Ошибка сети (${e.code()})"
                }
            }
            else -> e.message ?: "Неизвестная ошибка"
        }
    }

    // сброс состояний
    fun resetLoginState() {
        _uiState.update { it.copy(loginState = AuthState.Idle) }
    }
    /*
    fun resetRegisterState() {
        _uiState.update { it.copy(registerState = AuthState.Idle) }
    }

    // проверка авторизации
    fun checkAuthStatusManually() {
        checkAuthStatus()
    }*/
}