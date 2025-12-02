package com.example.foodcare.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.identity.util.UUID
import com.example.foodcare.api.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel @Inject constructor(
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _barcodeState = MutableStateFlow<BarcodeState>(BarcodeState.Idle)
    val barcodeState: StateFlow<BarcodeState> = _barcodeState.asStateFlow()

    fun getUserId(): String? = userPreferences.getUserId()

    fun scanBarcodeAndCreateOrderProduct(
        userId: UUID,
        barcode: String,
        expirationDate: Date?
    ) {
        viewModelScope.launch {
            _barcodeState.value = BarcodeState.Loading

            try {
                val startDate = Date() // текущая дата как дата добавления
                val startDateStr = SimpleDateFormat("yyyy-MM-dd").format(startDate)
                val endDateStr = expirationDate?.let {
                    SimpleDateFormat("yyyy-MM-dd").format(it)
                } ?: startDateStr

                val response = apiService.scanBarcode(
                    userId = userId,
                    barcode = barcode,
                    startDate = startDateStr,
                    endDate = endDateStr
                )

                if (response.isSuccessful) {
                    _barcodeState.value = BarcodeState.Success("Товар добавлен в холодильник!")
                } else {
                    _barcodeState.value = BarcodeState.Error("Ошибка: ${response.code()}")
                }

            } catch (e: Exception) {
                _barcodeState.value =
                    BarcodeState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }
}
