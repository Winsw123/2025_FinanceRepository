package com.example.financerepository.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financerepository.viewmodel.TransactionViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerFragment(viewModel: TransactionViewModel) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val datePickerState = rememberDatePickerState()

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = "選擇日期",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        DatePicker(
            state = datePickerState,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            selectedDate = datePickerState.selectedDateMillis?.let { millis ->
                Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }
            // viewModel.loadTransactionsByDate(selectedDate) 可選
        }) {
            Text("確定日期")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedDate?.let { date ->
            Text("已選擇日期：${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
        }
    }
}
