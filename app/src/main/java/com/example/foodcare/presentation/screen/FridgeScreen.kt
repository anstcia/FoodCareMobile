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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.presentation.viewmodel.ProductState
import com.example.foodcare.presentation.viewmodel.ProductViewModel
import com.example.foodcare.ui.theme.FoodCareTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun calculateDaysUntilEnd(endDate: String?): Int {
    if (endDate == null) return 0

    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val end = LocalDate.parse(endDate, formatter)
        val today = LocalDate.now()

        ChronoUnit.DAYS.between(today, end).toInt()
    } catch (e: Exception) {
        0
    }
}
@Composable
fun FridgeScreen(
    onBackClick: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    // ViewModel
    val state by viewModel.state.collectAsState()
    when (state) {
        is ProductState.Loading -> {
            Text("Загрузка...")
        }

        is ProductState.Error -> {
            Text("Ошибка: ${(state as ProductState.Error).message}")
        }

        is ProductState.Success -> {
            val products = (state as ProductState.Success).products

            // фильтрация списка
            val filteredProducts = products.filter { product ->
                val daysLeft = calculateDaysUntilEnd(product.endDate)
                val matchesSearch = product.name.contains(searchText, ignoreCase = true)
                val matchesFilter = when (selectedFilter) {
                    "Свежие" -> daysLeft >= 3
                    "Скоро" -> daysLeft in 1..2
                    "Истекли" -> daysLeft <= 0
                    else -> true
                }
                matchesSearch && matchesFilter
            }

            // подсчёты для фильтров
            val freshCount = products.count { calculateDaysUntilEnd(it.endDate) > 2 }
            val soonCount = products.count { calculateDaysUntilEnd(it.endDate) in 1..2 }
            val expiredCount = products.count { calculateDaysUntilEnd(it.endDate) <= 0 }

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
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                ) {
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
                                contentDescription = "Назад",
                                tint = Color.Gray
                            )
                        }
                        Text(
                            text = "Мой холодильник",
                            style = MaterialTheme.typography.titleLarge.copy(),
                            modifier = Modifier.padding(start = 8.dp),
                            fontWeight = FontWeight.Bold
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
                        ProductItem(product, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductItem(
    product: UserProduct,
    viewModel: ProductViewModel
) {
    val daysLeft = calculateDaysUntilEnd(product.endDate)
    val isExpiringSoon = daysLeft <= 2
    val isExpire = daysLeft <= 0

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "До окончания:",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        if (isExpire) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color(0xFFFEA1A2),
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Просрочено",
                                    color = Color(0xFFD30000),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            val badgeColor =
                                if (isExpiringSoon)
                                    Color(0xFFE8B10C).copy(alpha = 0.15f)
                                else
                                    Color(0xFF2E8B57).copy(alpha = 0.15f)

                            val textColor =
                                if (isExpiringSoon)
                                    Color(0xFFE8B10C)
                                else
                                    Color(0xFF2E8B57)

                            Box(
                                modifier = Modifier
                                    .background(
                                        color = badgeColor,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = daysText(daysLeft),
                                    color = textColor,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            IconButton(
                onClick = { viewModel.deleteProduct(product) },
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Удалить",
                    tint = Color.Gray.copy(alpha = 0.7f)
                )
            }
        }
    }
}

fun daysText(days: Int): String =
    when {
        days <= 0 -> "0 дн."
        days == 1 -> "1 день"
        days in 2..4 -> "$days дня"
        else -> "$days дней"
    }

@Preview(showBackground = true)
@Composable
fun FridgeScreenPreview() {
    FoodCareTheme {
        FridgeScreen(onBackClick = {})
    }
}
