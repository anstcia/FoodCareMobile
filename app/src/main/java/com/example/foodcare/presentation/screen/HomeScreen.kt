package com.example.foodcare.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.foodcare.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "FoodCare",
                            style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Управляйте своими продуктами",
                            style = MaterialTheme.typography.titleMedium)
                    }
                },
                Modifier.background(Color.White)
            )
        }
    ) { innerPadding ->
        // основной контент с учетом отступов
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color(0xFFF4F5F6))
        ) {
            // scan and fridge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .padding(12.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF64BA67)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_frame_inspect),
                                contentDescription = "Scan",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            "Сканировать продукт",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                ElevatedCard(
                    modifier = Modifier
                        .weight(1f)
                        .height(200.dp)
                        .padding(12.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF71BAFF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_kitchen),
                                contentDescription = "Fridge",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Text(
                            "Холодильник",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "N продуктов",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            // spoil
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(12.dp)
            ) {
                Row{
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shedule),
                        contentDescription = "Will soon spoil",
                        tint = Color(0xFFFFD350)
                    )
                    Text("Скоро испортится",
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                LazyColumn {
                    // TODO
                }
            }
            // rating
            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(12.dp)
            ) {
                Row{
                    Icon(
                        painter = painterResource(R.drawable.ic_trending_up),
                        contentDescription = "Rating",
                        tint = Color(0XFF64BA67)
                    )
                    Text("Популярные рейтинги",
                        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                LazyColumn {

                }
            }
        }
    }
}
