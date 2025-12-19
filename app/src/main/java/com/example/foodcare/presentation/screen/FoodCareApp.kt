package com.example.foodcare.presentation.screen

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodcare.navigation.BottomNavItem
import com.example.foodcare.navigation.Screen
import com.example.foodcare.presentation.viewmodel.AuthViewModel

/* =========================
   NavHost
   ========================= */

@Composable
fun FoodCareApp(
    navController: NavHostController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()

    when{
        uiState.userData == null && !uiState.isAuthenticated -> {
            // Показываем SplashScreen пока проверяется авторизация
            SplashScreen {
                // ничего не делаем, состояние обновится автоматически
            }
        }
        uiState.isAuthenticated -> {
            FoodCareNavHost(navController, isAuthenticated = true)
        }
        else -> {
            FoodCareNavHost(navController, isAuthenticated = false)
        }
    }
}


@Composable
fun FoodCareNavHost(
    navController: NavHostController,
    isAuthenticated: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) {
            Screen.Home.route
        } else {
            Screen.Start.route
        }
    ) {
        composable(Screen.Start.route) {
            StartPage(
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Start.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Signup.route)
                }
            )
        }

        composable(Screen.Signup.route) {
            SignUpScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Signup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onScanClick = {
                    navController.navigate(Screen.Barcode.route)
                },
                onFridgeClick = {
                    navController.navigate(Screen.Fridge.route)
                },
                onCalendarClick = {
                    navController.navigate(Screen.ExpirationDate.route)
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Fridge.route) {
            FridgeScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Recipe.route) {
            RecipeScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Barcode.route) {
            BarcodeScannerScreen(
                onBarcodeScanned = { barcode ->
                    navController.navigate(Screen.ExpirationDate.create(barcode))
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ExpirationDate.route) { entry ->
            val barcode = entry.arguments?.getString("barcode").orEmpty()

            ExpirationDateScreen(
                barcode = barcode,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToFridge = {
                    navController.navigate(Screen.Fridge.route)
                }
            )
        }
    }
}

/* =========================
   Bottom Bar
   ========================= */

@Composable
fun FoodCareBottomBar(
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val shouldShowBottomBar = bottomNavItems.any {
        it.screen.route == currentRoute
    }

    if (!shouldShowBottomBar) return

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.screen.route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        painter = painterResource(item.icon),
                        contentDescription = item.title,
                        modifier = Modifier.size(
                            if (selected) 28.dp else 24.dp
                        )
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2E8B57),
                    selectedTextColor = Color(0xFF2E8B57),
                    indicatorColor = Color(0xFF2E8B57).copy(alpha = 0.12f),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}

/* =========================
   Bottom items
   ========================= */

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Fridge,
    BottomNavItem.Recipe
)
