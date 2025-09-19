package com.example.foodcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.foodcare.presentation.screen.HomeScreen
import com.example.foodcare.ui.theme.FoodCareTheme
import androidx.compose.runtime.*
import com.example.foodcare.presentation.screen.LoginScreen

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodCareTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    HomeScreen(
                        onScanClick = { /* TODO */ },
                        onProfileClick = { /* TODO */ }
                    )
                } else {
                    LoginScreen(
                        onLogin = { email, password ->
                            if (email.isNotBlank() && password.isNotBlank()) {
                                isLoggedIn = true
                            }
                        },
                        onRegisterClick = {
                            println("Регистрация (заглушка)")
                        },
                        onForgotPasswordClick = {
                            println("Забыли пароль (заглушка)")
                        }
                    )
                }
            }
            }
        }
    }

