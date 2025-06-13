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
import java.util.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerFragment(viewModel: TransactionViewModel) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis()
    )

    val transactions by viewModel.selectedDateTransactions.collectAsState()

    // 使用垂直滚动修饰符包裹整个内容
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 4.dp)
            .verticalScroll(rememberScrollState()) // 添加垂直滚动
    ) {
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
            val selectedMillis = datePickerState.selectedDateMillis
            if (selectedMillis != null) {
                val calendar = Calendar.getInstance().apply {
                    timeInMillis = selectedMillis
                }
                selectedDate = Instant.ofEpochMilli(selectedMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                viewModel.loadTransactionsByDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        }) {
            Text("確定日期")
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedDate?.let { date ->
            Text("已選擇日期：${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider()

        // 显示交易列表 - 这里不需要LazyColumn，因为外层已经有滚动
        if (transactions.isEmpty()) {
            Text("沒有交易紀錄。", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column { // 改为普通Column
                transactions.forEach { transaction ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("標題：${transaction.title}")
                        Text("金額：${transaction.amount}")
                        Text("類型：${transaction.type}")
                        Text("分類：${transaction.category}")
                    }
                    Divider()
                }
            }
        }
    }
}
