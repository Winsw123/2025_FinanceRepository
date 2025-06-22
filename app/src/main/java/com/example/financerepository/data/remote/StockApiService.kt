package com.example.financerepository.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface StockApiService {
    @GET("query")
    suspend fun getStockQuote(
        @Query("function") function: String = "GLOBAL_QUOTE",
        @Query("symbol") symbol: String,
        @Query("apikey") apiKey: String
    ): StockResponse
}

data class StockResponse(
    @SerializedName("Global Quote") val quote: StockQuote
)

data class StockQuote(
    @SerializedName("01. symbol") val symbol: String,
    @SerializedName("05. price") val price: String,
    @SerializedName("10. change percent") val changePercent: String
)