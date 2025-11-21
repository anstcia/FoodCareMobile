@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.foodcare.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.identity.util.UUID
import com.example.foodcare.presentation.viewmodel.*

@Composable
fun RecipeScreen(
    onBackClick: () -> Unit,
    authViewModel: AuthViewModel? = null,
    recipesViewModel: RecipesViewModel? = null
) {
    val context = LocalContext.current
    val userPrefs = UserPreferences(context)
    val actualAuthViewModel = authViewModel ?: viewModel(
        factory = AuthViewModelFactory(userPreferences = userPrefs)
    )

    val actualRecipesViewModel = recipesViewModel ?: viewModel(
        factory = RecipesViewModelFactory(userPreferences = userPrefs)
    )

    val recipesState by actualRecipesViewModel.recipesState.collectAsState()

    // id из UserPreferences
    val savedId = userPrefs.getUserId()

    // id из AuthViewModel (когда только что залогинились)
    val userIdFromViewModel: String? by actualAuthViewModel.userId.collectAsState()


    // итоговый id
    val finalId = userIdFromViewModel ?: savedId

    // Сохранение id после успешного логина
    LaunchedEffect(userIdFromViewModel) {
        if (userIdFromViewModel != null) {
            userPrefs.saveUserId(userIdFromViewModel!!)
        }
    }

    // Запрос рецептов
    LaunchedEffect(finalId) {
        if (finalId != null) {
            try {
                val userUuid = UUID.fromString(finalId)
                actualRecipesViewModel.generateRecipes(userUuid)
            } catch (e: Exception) {
                // Обработка ошибки конвертации UUID
                android.util.Log.e("RecipeScreen", "Ошибка конвертации ID в UUID: ${e.message}")
            }
        }
    }

    RecipeScreenContent(
        onBackClick = onBackClick,
        userId = finalId,
        recipesState = recipesState,
        onGenerateRecipes = { id ->
            id?.let { 
                try {
                    val userUuid = UUID.fromString(it)
                    actualRecipesViewModel.generateRecipes(userUuid)
                } catch (e: Exception) {
                    android.util.Log.e("RecipeScreen", "Ошибка конвертации ID в UUID: ${e.message}")
                }
            }
        },
        cachedRecipes = actualRecipesViewModel.getCachedRecipes()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreenContent(
    onBackClick: () -> Unit,
    userId: String?,
    recipesState: RecipesState,
    onGenerateRecipes: (String?) -> Unit,
    cachedRecipes: List<String>
) {
    var searchText by remember { mutableStateOf("") }

    val recipes = when (recipesState) {
        is RecipesState.Success -> recipesState.recipe
        else -> cachedRecipes
    }

    val filtered = recipes.filter { it.contains(searchText, ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxWidth().background(Color.White)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick, modifier = Modifier.size(48.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        tint = Color.Gray,
                        contentDescription = "Назад"
                    )
                }
                Text(
                    text = "Рецепты",
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF2E8B57), Color(0xFF5A83DD))
                        )
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                userId?.let { id ->
                    Text(
                        text = "ID: ${id.take(8)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = { Text("Поиск рецептов") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(22.dp))
                },
                shape = RoundedCornerShape(25.dp),
                singleLine = true
            )
        }

        when (recipesState) {
            is RecipesState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is RecipesState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Ошибка загрузки рецептов", color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { onGenerateRecipes(userId) }) {
                            Text("Попробовать снова")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filtered) { recipeText ->
                        ElevatedCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = recipeText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
