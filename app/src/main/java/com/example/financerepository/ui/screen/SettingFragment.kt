package com.example.financerepository.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment

@Composable
fun SettingsFragment(
    currentBudget: Int? = null,               // 預設目前的預算（可從ViewModel帶入）
    onBudgetChange: (Int) -> Unit = {},      // 用來回傳輸入的預算給ViewModel
    onSaveClick: () -> Unit = {}              // 按下儲存後觸發的事件
) {
    var budgetInput by remember { mutableStateOf(currentBudget?.toString() ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "設定每月金錢預算",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        OutlinedTextField(
            value = budgetInput,
            onValueChange = { newValue ->
                // 只允許輸入數字
                if (newValue.all { it.isDigit() }) {
                    budgetInput = newValue
                    errorMessage = null
                } else {
                    errorMessage = "請輸入有效數字"
                }
            },
            label = { Text("每月預算 (元)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (budgetInput.isNotEmpty()) {
                    onBudgetChange(budgetInput.toInt())
                    onSaveClick()
                } else {
                    errorMessage = "請輸入預算金額"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(text = "儲存")
        }
    }
}


