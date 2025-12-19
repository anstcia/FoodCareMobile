package com.example.foodcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.foodcare.presentation.screen.FoodCareApp
import com.example.foodcare.presentation.screen.FoodCareBottomBar
import com.example.foodcare.ui.theme.FoodCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodCareTheme {

                val navController = rememberNavController()

                Scaffold(
                    bottomBar = {
                        FoodCareBottomBar(navController)
                    }
                ) { padding ->

                    Box(Modifier.padding(padding)) {
                        FoodCareApp(
                            navController = navController,
                        )
                    }
                }
            }
        }
    }
}

