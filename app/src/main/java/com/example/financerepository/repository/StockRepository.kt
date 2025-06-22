package com.example.financerepository.repository

import com.example.financerepository.data.remote.StockApiService
import com.example.financerepository.data.remote.StockQuote

class StockRepository(private val apiService: StockApiService) {
    suspend fun getStockQuote(symbol: String): StockQuote {
        val response = apiService.getStockQuote(symbol = symbol, apiKey = "C3FU978LGLKG5ODA")
        return response.quote
    }
}