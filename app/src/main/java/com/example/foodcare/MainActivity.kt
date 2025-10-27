package com.example.foodcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import com.example.foodcare.presentation.screen.*

import com.example.foodcare.ui.theme.FoodCareTheme

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodCareTheme {
                var currentScreen by remember { mutableStateOf("start") }
                var isLoggedIn by remember { mutableStateOf(false) }

                when (currentScreen) {

                    "start" -> {
                        StartPage(
                            onRegisterClick = { currentScreen = "signup" },
                            onLoginClick = { currentScreen = "login" }
                        )
                    }

                    "signup" -> {
                        SignUpScreen(
                            onLoginClick = {
                                currentScreen = "login"
                            }                        )
                    }

                    "login" -> {
                        LoginScreen(
                            onLogin = { email, password ->
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    isLoggedIn = true
                                    currentScreen = "home"
                                }
                            },
                            onRegisterClick = { currentScreen = "signup" },
                            onForgotPasswordClick = { println("Забыли пароль (заглушка)") }
                        )
                    }

                    "home" -> {
                        FoodCareApp()
                    }
                }
            }
        }
    }
}
