package com.example.foodcare.presentation.screen
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.example.foodcare.R

@Composable
fun StartPage(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F0F0))
            .padding(horizontal = screenWidth * 0.05f), // 5% ширины
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = screenHeight * 0.1f), // 10% высоты экрана
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight * 0.35f), // 35% высоты
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bbarcode),
                        contentDescription = "Barcode",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.35f),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                Text(
                    text = "FoodCare",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2E8B57),
                                Color(0xFF5A83DD)
                            )
                        )
                    ),
                    fontSize = (screenWidth.value * 0.08).sp, // 8% ширины
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Приложение, призванное взять ваши заботы в свои руки",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = (screenWidth.value * 0.04).sp, // 4% ширины
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = screenWidth * 0.1f)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = screenHeight * 0.12f), // 6% высоты
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth(0.86f)
                        .height(screenHeight * 0.07f), // 7% высоты
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B90E6))
                ) {
                    Text(
                        text = "Зарегистрироваться",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                Button(
                    onClick = onLoginClick,
                    modifier = Modifier
                        .fillMaxWidth(0.86f)
                        .height(screenHeight * 0.07f),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF83A2E5))
                ) {
                    Text(
                        text = "У меня уже есть аккаунт",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}
