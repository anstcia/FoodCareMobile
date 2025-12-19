package com.example.foodcare.presentation.screen

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodcare.R
import com.example.foodcare.domain.entity.UserProduct
import com.example.foodcare.presentation.viewmodel.AuthViewModel
import com.example.foodcare.presentation.viewmodel.ProductState
import com.example.foodcare.presentation.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onFridgeClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onLogout: () -> Unit
) {
    val productViewModel:  ProductViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val state by productViewModel.state.collectAsState()

    val uiState by authViewModel.uiState.collectAsState()
    val userName = uiState.userData?.name
    val email = uiState.userData?.login

    when(state){
        is ProductState.Loading -> {
            Column(Modifier.fillMaxSize().padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Загружаем профиль...",
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
            Text("Ошибка: ${(state as ProductState.Error).message}")
        }
        is ProductState.Success -> {
            val products = (state as ProductState.Success).products
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF7F8F9)),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Top bar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "FoodCare",
                        style = MaterialTheme.typography.displaySmall.copy(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF2E8B57),
                                    Color(0xFF5A83DD))
                            )
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    Text(
                        text = "Управляйте своими продуктами",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                // профиль
                ProfileCard(
                    userName = userName,
                    email = email,
                    onLogoutClick = {
                        authViewModel.logout()
                        onLogout()}
                )
                // кнопки
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        title = "Сканировать продукт",
                        icon = R.drawable.ic_frame_inspect,
                        backgroundIconColor = Color(0xFF2E8B57),
                        onClick = onScanClick,
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        title = "Холодильник",
                        subtitle = "${products.size} ${getProductWordForm(products.size)}",
                        icon = R.drawable.ic_kitchen,
                        backgroundIconColor = Color(0xFF5A83DD),
                        onClick = onFridgeClick,
                        modifier = Modifier.weight(1f)
                    )
                }

                // блок "Скоро испортится"
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, end = 12.dp)
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
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        val expiringProducts = products.filter {
                            val days = calculateDaysUntilEnd(it.endDate)
                            days in 1..3
                        }

                        if (expiringProducts.isEmpty())  { EmptyExpiringBlock()
                        } else {
                            LazyColumn {
                                itemsIndexed(expiringProducts) { index, product ->
                                    ProductItemExpires(
                                        product = product,
                                    )
                                    if (index != expiringProducts.lastIndex) {
                                        Divider(
                                            thickness = 0.6.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant
                                        )
                                    }
                                }
                            }

                        }

                    }
                }
                /* кнопка перехода к календарю
                Button(
                    onClick = onCalendarClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .width(120.dp)
                        .align(Alignment.Start)
                ) {
                    Text("Календарь", color =  Color.Transparent)
                }*/
            }
        }
    }
}

@SuppressLint("ModifierParameter")
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
fun EmptyExpiringBlock(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Скоро портящихся продуктов нет",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ProductItemExpires(
    product: UserProduct,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            product.endDate?.let {
                ExpireBadge(days = calculateDaysUntilEnd(it))
            }
        }
    }
}

@Composable
fun ExpireBadge(days: Int) {
    val background = Color(0xFFE8B10C).copy(alpha = 0.15f)
    val textColor = Color(0xFFE8B10C)

    Box(
        modifier = Modifier
            .background(background, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "$days дн.",
            color = textColor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}



@Composable
fun ProfileCard(userName: String?, email: String?, onLogoutClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

    Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // аватар с градиентной рамкой
            Box(
                modifier = Modifier
                    .size(74.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF2E8B57),
                                Color(0xFF5A83DD)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName?.firstOrNull()?.uppercase() ?: "?",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5A83DD)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(18.dp))

            // информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = userName ?: "Пользователь",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 19.sp
                    )
                )
                Spacer(Modifier.height(2.dp))

                Text(
                    text = email ?: "",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF777777),
                        fontSize = 14.sp
                    )
                )
                Spacer(Modifier.height(2.dp))

                Text(
                    "Босс холодильника",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF3F3F3F),
                        fontSize = 15.sp
                    )
                )
            }

            // кнопка выхода
            TextButton(
                onClick = { onLogoutClick() },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFFD2554A)
                ),
                modifier = Modifier
                    .height(42.dp)
                    .background(
                        Color(0xFFFFF2EF),
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Text(
                    text = "Выйти",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

fun getProductWordForm(count: Int): String {
    val n = count % 100
    return when {
        n in 11..14 -> "продуктов"
        n % 10 == 1 -> "продукт"
        n % 10 in 2..4 -> "продукта"
        else -> "продуктов"
    }
}
