package com.example.foodcare.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.foodcare.presentation.viewmodel.AuthViewModel
import com.example.foodcare.presentation.viewmodel.BarcodeViewModel
import com.example.foodcare.presentation.viewmodel.BarcodeState
import com.android.identity.util.UUID
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpirationDateScreen(
    barcode: String = "",
    onBackClick: () -> Unit,
    onNavigateToFridge: () -> Unit,
    barcodeViewModel: BarcodeViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val datePickerState = rememberDatePickerState()
    var selectedDate: Date? by remember { mutableStateOf(null) }

    val barcodeState by barcodeViewModel.barcodeState.collectAsState()
    val authUiState by authViewModel.uiState.collectAsState()

    val userId = authUiState.userData?.id

    // Обновляем selectedDate при выборе
    LaunchedEffect(datePickerState.selectedDateMillis) {
        selectedDate = datePickerState.selectedDateMillis
            ?.let { Date(it) }
    }

    // Навигация при успешном сохранении
    LaunchedEffect(barcodeState) {
        if (barcodeState is BarcodeState.Success) {
            onNavigateToFridge()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выберите дату истечения") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = true,
                modifier = Modifier.fillMaxWidth(),
                title = null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null && barcode.isNotEmpty() && userId != null) {
                        barcodeViewModel.scanBarcodeAndCreateOrderProduct(
                            userId = UUID.fromString(userId),
                            barcode = barcode,
                            expirationDate = selectedDate
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                enabled = selectedDate != null && barcode.isNotEmpty() && userId != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7B90E6)
                )
            ) {
                Text("Добавить в холодильник")
            }
        }
    }
}
