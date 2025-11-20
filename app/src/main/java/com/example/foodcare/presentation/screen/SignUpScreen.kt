package com.example.foodcare.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodcare.R
import com.example.foodcare.presentation.viewmodel.AuthState
import com.example.foodcare.presentation.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel(),

    ) {
    var email by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var confirmPasswordVisible by rememberSaveable { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState(initial = AuthState.Idle)
    val passwordsMatch = password == confirmPassword
    val canRegister = email.isNotBlank() && password.length >= 4 && confirmPassword.isNotBlank() && passwordsMatch

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(screenHeight * 0.95f)
                .align(Alignment.TopCenter)
                .padding(top = screenHeight * 0.05f)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(32.dp))
                .background(Color.White, shape = RoundedCornerShape(32.dp))
                .border(width = 1.dp, color = Color(0xFFC4C4C4), shape = RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = screenWidth * 0.07f, vertical = screenHeight * 0.03f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {


                Text(
                    text = "FoodCare",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF2E8B57),
                                Color(0xFF5A83DD)
                            )
                        ),
                    ),
                    fontWeight = FontWeight.Bold,
                    fontSize = (screenWidth.value * 0.08).sp,
                    modifier = Modifier.padding(bottom = screenHeight * 0.04f)
                )

                @Composable
                fun labelText(text: String) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        textAlign = TextAlign.Start
                    )
                }

                labelText("Введите почтовый адрес")
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                labelText("Введите имя")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                labelText("Введите пароль")
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Пароль") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible) R.drawable.ic_hide_password else R.drawable.ic_show_password
                                ),
                                contentDescription = if (passwordVisible) "Скрыть пароль" else "Показать пароль"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                labelText("Повторите пароль")
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Пароль") },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter = painterResource(
                                    id = if (confirmPasswordVisible) R.drawable.ic_hide_password else R.drawable.ic_show_password
                                ),
                                contentDescription = if (confirmPasswordVisible) "Скрыть пароль" else "Показать пароль"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )

                if (!passwordsMatch && confirmPassword.isNotEmpty()) {
                    Text(
                        text = "Пароли не совпадают",
                        color = Color.Red,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.03f))

                Button(
                    onClick = { viewModel.register(email.trim(), name.trim(), password, confirmPassword) },
                    enabled = canRegister,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canRegister) Color(0xFF5A83DD) else Color.Gray,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight * 0.06f)
                ) {
                    Text("Зарегистрироваться", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.02f))

                when (registerState) {
                    is AuthState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                    is AuthState.Success -> Text(
                        text = (registerState as AuthState.Success).message,
                        color = Color(0xFF2E8B57),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    is AuthState.Error -> Text(
                        text = (registerState as AuthState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    else -> {}
                }

                Spacer(modifier = Modifier.height(screenHeight * 0.03f))


                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Уже есть аккаунт? ", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Войти",
                        modifier = Modifier.clickable { onLoginClick() },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
