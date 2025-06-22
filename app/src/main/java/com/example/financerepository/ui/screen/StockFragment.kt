package com.example.financerepository.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financerepository.viewmodel.StockViewModel

@Composable
fun StockFragment(viewModel: StockViewModel) {
    var symbol by remember { mutableStateOf("AAPL") }
    val quote by viewModel.stockQuote.collectAsState()
    val error by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = symbol,
            onValueChange = { symbol = it },
            label = { Text("輸入股票代碼") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { viewModel.loadStock(symbol) }) {
            Text("查詢")
        }

        Spacer(modifier = Modifier.height(16.dp))
        when {
            quote != null -> {
                Text("代碼：${quote!!.symbol}")
                Text("價格：$${quote!!.price}")
                Text("漲跌：${quote!!.changePercent}")
            }
            error != null -> {
                Text(error!!, color = MaterialTheme.colorScheme.error)
            }
            else -> {
                Text("請輸入股票代碼並查詢")
            }
        }
    }
}
