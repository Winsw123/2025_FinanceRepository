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

    fun loadStock(symbol: String) {
        viewModelScope.launch {
            try {
                val quote = repository.getStockQuote(symbol)
                _stockQuote.value = quote
            } catch (e: Exception) {
                // ...
            }
        }
    }
}
