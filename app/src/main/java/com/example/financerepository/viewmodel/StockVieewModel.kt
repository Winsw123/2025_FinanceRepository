package com.example.financerepository.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financerepository.data.remote.StockQuote
import com.example.financerepository.repository.StockRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StockViewModel(private val repository: StockRepository) : ViewModel() {

    private val _stockQuote = MutableStateFlow<StockQuote?>(null)
    val stockQuote: StateFlow<StockQuote?> = _stockQuote

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun loadStock(symbol: String) {
        viewModelScope.launch {
            val result = repository.getStockQuote(symbol)
            if (result != null) {
                _stockQuote.value = result
                _errorMessage.value = null
            } else {
                _stockQuote.value = null
                _errorMessage.value = "查無資料，請確認代碼是否正確或 API 配額是否用盡"
            }
        }
    }
}
