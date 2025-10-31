package com.example.foodcare.presentation.screen

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodcare.R
import com.example.foodcare.domain.entity.Product
import com.example.foodcare.ui.theme.FoodCareTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeScreen(onBackClick: () -> Unit) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    val sampleProducts = listOf(
        Product("Молоко Простоквашино", "Молочные", "19-09-2025", "2 дня", R.drawable.milk),
        Product("Яблоки", "Фрукты", "22-09-2025", "5 дней", R.drawable.apple),
        Product("Курица Петелинка", "Мясо", "18-09-2025", "1 день", R.drawable.chicken),
        Product("Греческий йогурт", "Молочные", "18-09-2025", "7 дней", R.drawable.yogurt),
        Product("Хлеб зерновой", "Выпечка", "18-09-2025", "3 дня", R.drawable.domashniyzernovoyhleb),
        Product("Батончик Twix", "Конфеты", "20-09-2025", "1 день", R.drawable.twix),
        Product("Яйца куриные Роскар", "Яйца", "21-09-2025", "10 дней", R.drawable.egg)
    )

    // фильтрация
    val filteredProducts = sampleProducts.filter { product ->
        val daysLeft = product.expiresIn.filter { it.isDigit() }.toIntOrNull() ?: 0
        val matchesSearch = product.name.contains(searchText, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Свежие" -> daysLeft >= 3
            "Скоро" -> daysLeft in 1..2
            "Истекли" -> daysLeft <= 0
            else -> true
        }
        matchesSearch && matchesFilter
    }

    val freshCount = sampleProducts.count {
        val days = it.expiresIn.filter { c -> c.isDigit() }.toIntOrNull() ?: 0
        days > 2
    }
    val soonCount = sampleProducts.count {
        val days = it.expiresIn.filter { c -> c.isDigit() }.toIntOrNull() ?: 0
        days in 1..2
    }
    val expiredCount = sampleProducts.count {
        val days = it.expiresIn.filter { c -> c.isDigit() }.toIntOrNull() ?: 0
        days <= 0
    }

    val filters = listOf(
        "Свежие" to freshCount,
        "Скоро" to soonCount,
        "Истекли" to expiredCount
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // topBar
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
                    text = "Мой холодильник",
                    style = MaterialTheme.typography.titleLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF2E8B57), Color(0xFF5A83DD))
                        )
                    ),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                placeholder = {
                    Text(
                        "Поиск продуктов",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp)
                    )
                },
                shape = RoundedCornerShape(25.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    disabledContainerColor = Color(0xFFF5F5F5),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        // фильтры
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            shape = RoundedCornerShape(50),
            color = Color(0xFFEDEDEE)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                filters.forEach { (title, count) ->
                    val isSelected = selectedFilter == title
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = if (isSelected) null else title },
                        label = {
                            Text(
                                "$title ($count)",
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color.White,
                            selectedContainerColor = Color(0xFF2E8B57).copy(alpha = 0.15f),
                            labelColor = if (isSelected) Color(0xFF2E8B57) else Color.Black
                        ),
                        border = null
                    )
                }
            }
        }

        // список продуктов
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { product ->
                ProductItem(product)
            }
        }
    }
}


@Composable
fun ProductItem(product: Product) {
    val daysLeft = product.expiresIn.filter { it.isDigit() }.toIntOrNull() ?: 0
    val isExpiringSoon = daysLeft <= 2

    ElevatedCard(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(product.iconRes),
                        contentDescription = "Иконка ${product.category}"
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                    Text(
                        product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Осталось:",
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                        )
                        Spacer(modifier = Modifier.width(6.dp))

                        // бейдж с динамическим цветом
                        val badgeColor = if (isExpiringSoon)
                            Color(0xFFE8B10C).copy(alpha = 0.15f)
                        else
                            Color(0xFF2E8B57).copy(alpha = 0.15f)

                        val textColor = if (isExpiringSoon) Color(0xFFE8B10C) else Color(0xFF2E8B57)

                        Box(
                            modifier = Modifier
                                .background(color = badgeColor, shape = RoundedCornerShape(50))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = product.expiresIn,
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Text(
                    product.date,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }

            IconButton(
                onClick = { /* удалить */ },
                modifier = Modifier.size(32.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Удалить", tint = Color.Gray.copy(alpha = 0.7f))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FridgeScreenPreview() {
    FoodCareTheme {
        FridgeScreen({ })
    }
}
