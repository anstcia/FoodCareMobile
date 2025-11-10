package com.example.foodcare.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodcare.navigation.Screen

@Composable
fun FoodCareApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination?.route

                listOf(Screen.Home, Screen.Fridge, Screen.Recipe).forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination == screen.route,
                        onClick = { navController.navigate(screen.route) },
                        icon = {
                            Icon(
                                painter = painterResource(screen.icon),
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(
                onScanClick = { navController.navigate("barcode")},
                onFridgeClick = { navController.navigate(Screen.Fridge.route) },
                onCalendarClick = { navController.navigate(Screen.ExpirationDate.route) }
            ) }
            composable("barcode") {
                BarcodeScannerScreen(
                    onBarcodeScanned = { code ->
                        println("Отсканировано: $code")
                        navController.popBackStack()
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Fridge.route) { FridgeScreen(
                onBackClick = { navController.popBackStack() }
            ) }

            composable(Screen.Recipe.route) { RecipeScreen(
                onBackClick = { navController.popBackStack() }
            ) }

            composable(Screen.ExpirationDate.route) {
                ExpirationDateScreen(
                    onBackClick = { navController.popBackStack() },
                    onNavigateToFridge = {navController.navigate(Screen.Fridge.route)}
                )
            }
        }
    }
}
