package com.example.foodcare.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.foodcare.R
import com.example.foodcare.domain.entity.Product
import com.example.foodcare.ui.theme.FoodCareTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onFridgeClick: () -> Unit,
    onCalendarClick: () -> Unit
) {
    val sampleProducts = listOf(
        Product("Молоко", "Молочные",
            "19-09-2025",
            "1 день",
            R.drawable.milk),
        Product("Творог", "Молочные",
            "22-09-2025",
            "2 дня",
            R.drawable.apple)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FoodCare",
                            style = MaterialTheme.typography.titleLarge.copy(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF2E8B57),
                                        Color(0xFF5A83DD)
                                    )
                                ),
                            ),
                        )
                        Text(
                            text = "Управляйте своими продуктами",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF4F5F6))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    title = "Сканировать продукт",
                    subtitle = null,
                    icon = R.drawable.ic_frame_inspect,
                    backgroundIconColor = Color(0xFF2E8B57),
                    onClick = onScanClick,
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    title = "Холодильник",
                    subtitle = "12 продуктов",
                    icon = R.drawable.ic_kitchen,
                    backgroundIconColor = Color(0xFF5A83DD),
                    onClick = onFridgeClick,
                    modifier = Modifier.weight(1f)
                )
            }

            ElevatedCard(
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.ic_shedule),
                            contentDescription = null,
                            tint = Color(0xFFFFD350),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Скоро испортится",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.heightIn(max = 180.dp)
                    ) {
                        items(sampleProducts) { product ->
                            ProductItemExpires(product)
                        }
                    }
                }
            }

            Button(
                onClick = onCalendarClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF4F5F6)),
                modifier = Modifier
                    .align(Alignment.Start)
                    .width(100.dp)
            ) {
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    subtitle: String? = null,
    @DrawableRes icon: Int,
    backgroundIconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(backgroundIconColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Light,
                        color = Color.Gray
                    )
                )
            }
        }
    }
}

@Composable
fun ProductItemExpires(product: Product) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = product.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier
                .background(
                    color = Color(0xFFE8B10C).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .width(40.dp)
            , Alignment.Center
        ) {
            Text(
                text = product.expiresIn,
                color = Color(0xFFE8B10C),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FoodCareTheme {
        HomeScreen({}, {}, {})
    }
}
