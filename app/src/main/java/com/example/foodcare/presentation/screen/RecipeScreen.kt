@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.foodcare.presentation.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.android.identity.util.UUID
import com.example.foodcare.presentation.viewmodel.*
import com.example.foodcare.api.RecipeResponse


@Composable
fun RecipeScreen(
    onBackClick: () -> Unit,
) {
    val productViewModel:  ProductViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val recipesViewModel: RecipesViewModel = hiltViewModel()

    val authState by authViewModel.uiState.collectAsState()
    val productState by productViewModel.state.collectAsState()
    val recipesState by recipesViewModel.recipesState.collectAsState()

    val userId = authState.userData?.id

    when(productState){
        is ProductState.Loading -> {
            Column(Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Осматриваем холодильник...",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 20.sp,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

            }
        }
        is ProductState.Error -> {
            Text("Ошибка: ${(productState as ProductState.Error).message}")
        }
        is ProductState.Success ->{
            val products = (productState as ProductState.Success).products
            if(products.isEmpty()){
                Column(Modifier.fillMaxSize().padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ваш холодильник пустой 😔",
                        style = MaterialTheme.typography.bodyMedium,
                        fontSize = 20.sp,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )

                }
            } else{
                LaunchedEffect(userId) {
                    if (userId != null) {
                        try {
                            recipesViewModel.generateRecipes(UUID.fromString(userId))
                        } catch (e: Exception) {
                            Log.e("RecipeScreen", "Invalid UUID: ${e.message}")
                        }
                    }
                }
                RecipeScreenContent(
                    onBackClick = onBackClick,
                    userId = userId,
                    recipesState = recipesState,
                    onGenerateRecipes = { id ->
                        id?.let {
                            try {
                                val userUuid = UUID.fromString(it)
                                recipesViewModel.generateRecipes(userUuid)
                            } catch (e: Exception) {
                                android.util.Log.e("RecipeScreen", "Ошибка конвертации ID в UUID: ${e.message}")
                            }
                        }
                    },
                    cachedRecipes = recipesViewModel.getCachedRecipes()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreenContent(
    onBackClick: () -> Unit,
    userId: String?,
    recipesState: RecipesState,
    onGenerateRecipes: (String?) -> Unit,
    cachedRecipes: List<RecipeResponse>
) {
    var searchText by remember { mutableStateOf("") }

    val recipes = when (recipesState) {
        is RecipesState.Success -> recipesState.recipes;

        else -> cachedRecipes
    }

    val filtered = recipes.filter {
        it.name.contains(searchText, ignoreCase = true) ||
        it.recipe.contains(searchText, ignoreCase = true)
    }

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
                Column(Modifier.fillMaxSize().padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                       text = "Готовим рецепты, пожалуйтся подождите!",
                       style = MaterialTheme.typography.bodyMedium,
                       fontSize = 20.sp,
                       color = Color(0xFF333333),
                       textAlign = TextAlign.Center,
                       modifier = Modifier
                           .wrapContentWidth(Alignment.CenterHorizontally)
                   )

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
                    items(filtered) { recipe ->
                        RecipeCard(recipe = recipe)
                    }
                }
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: RecipeResponse) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Название рецепта
            Text(
                text = recipe.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Время и сложность
            Row(
                modifier = Modifier.padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⏲️",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = recipe.time,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "⚙️",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = recipe.complexity,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF5A83DD)
                    )
                }
            }
            
            // Рецепт
            Text(
                text = recipe.recipe,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2
            )
        }
    }
}
