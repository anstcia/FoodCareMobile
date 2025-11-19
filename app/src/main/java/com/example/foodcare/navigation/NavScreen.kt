package com.example.foodcare.navigation

import com.example.foodcare.R

sealed class Screen(val route: String, val title: String, val icon: Int) {
    object Home : Screen("home", "Главная", R.drawable.ic_home)
    object Fridge : Screen("fridge", "Холодильник", R.drawable.ic_kitchen)

    object ExpirationDate : Screen("experationDate", "Календарь", R.drawable.ic_home)

    object Recipe : Screen("recipe", "Рецепты", R.drawable.ic_recipes)
}