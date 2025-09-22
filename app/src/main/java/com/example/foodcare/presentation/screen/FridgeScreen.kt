package com.example.foodcare.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.foodcare.domain.entity.Product
import com.example.foodcare.ui.theme.FoodCareTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeScreen() {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Свежие") }

    val filters = listOf(
        "Свежие" to 3,
        "Скоро" to 4,
        "Истекли" to 1
    )

    val sampleProducts = listOf(
        Product(
            name = "Молоко Простоквашино",
            category = "Молочные",
            date = "19-09-2025",
            expiresIn = "2 дня"
        ),
        Product(
            name = "Яблоки",
            category = "Фрукты",
            date = "22-09-2025",
            expiresIn = "5 дней"
        ),
        Product(
            name = "Курица Петелинка",
            category = "Мясо",
            date = "18-09-2025",
            expiresIn = "1 день"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Мой холодильник",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2E8B57), // зелёный
                                    Color(0xFF5A83DD)  // синий
                                )
                            ),
                        ),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            // поиск
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Поиск продуктов...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                shape = RoundedCornerShape(50),
                singleLine = true
            )

            // фильтры
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                filters.forEach { (title, count) ->
                    FilterChip(
                        selected = selectedFilter == title,
                        onClick = { selectedFilter = title },
                        label = { Text("$title ($count)") },
                        shape = RoundedCornerShape(50)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // продукты
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sampleProducts) { product ->
                    ProductItem(product)
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: Product) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    product.category,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Text(
                    product.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            // бейдж "2 дня"
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF2E8B57).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = product.expiresIn,
                    color = Color(0xFF2E8B57),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun FridgeScreenPreview() {
    FoodCareTheme {
        FridgeScreen(
        )
    }
}
