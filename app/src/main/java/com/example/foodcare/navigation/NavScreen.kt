package com.example.foodcare.navigation

import com.example.foodcare.R

sealed interface Screen {
    val route: String

    object Start : Screen { override val route = "start" }
    object Login : Screen { override val route = "login" }
    object Signup : Screen { override val route = "signup" }

    object Home : Screen { override val route = "home" }
    object Fridge : Screen { override val route = "fridge" }
    object Recipe : Screen { override val route = "recipe" }

    object Barcode : Screen { override val route = "barcode" }
    object ExpirationDate : Screen {
        override val route = "expiration_date/{barcode}"
        fun create(barcode: String) = "expiration_date/$barcode"
    }
}


sealed class BottomNavItem(
    val screen: Screen,
    val title: String,
    val icon: Int
) {
    object Home : BottomNavItem(
        screen = Screen.Home,
        title = "Главная",
        icon = R.drawable.ic_home
    )

    object Fridge : BottomNavItem(
        screen = Screen.Fridge,
        title = "Холодильник",
        icon = R.drawable.ic_kitchen
    )

    object Recipe : BottomNavItem(
        screen = Screen.Recipe,
        title = "Рецепты",
        icon = R.drawable.ic_recipes
    )
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Fridge,
    BottomNavItem.Recipe
)

