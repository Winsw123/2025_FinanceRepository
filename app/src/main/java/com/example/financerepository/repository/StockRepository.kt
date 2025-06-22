package com.example.financerepository.repository

import com.example.financerepository.data.remote.StockApiService
import com.example.financerepository.data.remote.StockQuote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class StockRepository(private val apiService: StockApiService) {

    suspend fun getStockQuote(symbol: String): StockQuote? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getStockQuote(
                    symbol = symbol,
                    apiKey = "C3FU978LGLKG5ODA"
                )
                //
                response.quote.takeIf { it.symbol.isNotEmpty() }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
