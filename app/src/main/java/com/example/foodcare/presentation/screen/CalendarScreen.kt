package com.example.foodcare.presentation.screen

import android.annotation.SuppressLint
import android.os.Build
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.foodcare.ui.theme.FoodCareTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@SuppressLint("ConfigurationScreenWidthHeight")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpirationDateScreen() {
    val context = LocalContext.current
    val datePickerState = rememberDatePickerState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedDate: LocalDate? by remember { mutableStateOf<LocalDate?>(null) }
    var showOldPicker by remember { mutableStateOf(false) }

    if (showDialog) {
        val configuration = LocalConfiguration.current
        val dialogWidth = when (configuration.screenWidthDp) {
            in 0..400 -> Modifier.fillMaxWidth(0.95f)
            in 401..600 -> Modifier.fillMaxWidth(0.85f)
            else -> Modifier.fillMaxWidth(0.75f)
        }

        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = dialogWidth
                    .wrapContentHeight()
            ) {
                Column {

                    DatePicker(
                        state = datePickerState,
                        showModeToggle = true,
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Назад")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val millis = datePickerState.selectedDateMillis
                                if (millis != null) {
                                    selectedDate = Instant.ofEpochMilli(millis)
                                        .atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                showDialog = false
                            }
                        ) {
                            Text("Ок")
                        }
                    }
                }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Выберите дату окончания срока годности:") },
                navigationIcon = {
                    IconButton(onClick = { /* вернуться назад */ }) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Выбрать дату")
            }

            Spacer(modifier = Modifier.height(8.dp))

            selectedDate?.let {
                Text(
                    text = "Выбранная дата: ${it.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showOldPicker = !showOldPicker },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B90E6))
            ) {
                Text(if (showOldPicker) "Скрыть дополнительную дату" else "Добавить дату")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showOldPicker) {
                WheelDatePicker()
            }
        }
    }
}

@Composable
fun WheelDatePicker() {
    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    var selectedMonth by remember { mutableStateOf(10) }
    var selectedDay by remember { mutableStateOf(18) }
    var selectedYear by remember { mutableStateOf(2025) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Выберите дату", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                AndroidView(factory = {
                    NumberPicker(it).apply {
                        minValue = 0
                        maxValue = 11
                        displayedValues = months.toTypedArray()
                        value = selectedMonth
                        setOnValueChangedListener { _, _, newVal -> selectedMonth = newVal }
                    }
                })
                Spacer(modifier = Modifier.width(8.dp))
                AndroidView(factory = {
                    NumberPicker(it).apply {
                        minValue = 1
                        maxValue = 31
                        value = selectedDay
                        setOnValueChangedListener { _, _, newVal -> selectedDay = newVal }
                    }
                })
                Spacer(modifier = Modifier.width(8.dp))
                AndroidView(factory = {
                    NumberPicker(it).apply {
                        minValue = 2025
                        maxValue = 2027
                        value = selectedYear
                        setOnValueChangedListener { _, _, newVal -> selectedYear = newVal }
                    }
                })
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                TextButton(onClick = { /* подтверждение */ }) { Text("Добавить", color = Color(0xFF2E8B57)) }
                TextButton(onClick = { /* отмена */ }) { Text("Назад", color = Color(0xFFE91E63)) }

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ExpirationDateScreenPreview() {
    FoodCareTheme {
        ExpirationDateScreen()
    }
}