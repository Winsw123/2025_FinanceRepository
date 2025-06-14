package com.example.financerepository.ui.screen

import android.icu.text.NumberFormat
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
import com.example.financerepository.data.model.Transaction
import com.example.financerepository.ui.components.CalendarPager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerFragment(viewModel: TransactionViewModel) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val monthlyTransactions by viewModel.monthlyTransactions.collectAsState()
    val transactions by viewModel.selectedDateTransactions.collectAsState()
    val monthlyBudget by viewModel.monthlyBudget.collectAsState()
    var transactionToDelete by remember { mutableStateOf<Transaction?>(null) }

    val overBudgetDates = remember { mutableStateOf(setOf<LocalDate>()) }


// 監聽 selectedDate 變化時載入該月資料
    LaunchedEffect(selectedDate) {
        selectedDate?.let {
            viewModel.loadTransactionsByMonth(it.year, it.monthValue)
        }
    }

// 監聽 monthlyTransactions 或 monthlyBudget 變化時，計算超預算日期
    LaunchedEffect(monthlyTransactions, monthlyBudget) {
        if (monthlyTransactions.isNotEmpty()) {
            val anyDate = monthlyTransactions.firstOrNull()?.let {
                Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            } ?: LocalDate.now()

            val daysInMonth = anyDate.lengthOfMonth()
            val dailyBudget = monthlyBudget.toDouble() / daysInMonth

            val spendByDate = monthlyTransactions.groupBy {
                Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            }.mapValues { entry -> entry.value.sumOf { it.amount } }

            val overDates = spendByDate.filter { it.value > dailyBudget }.keys
            overBudgetDates.value = overDates
        } else {
            overBudgetDates.value = emptySet()
        }
    }

//    LaunchedEffect(Unit) {
//        val testDate = LocalDate.of(2025, 6, 18)
//        val zoneId = ZoneId.systemDefault()
//        val timestamp = testDate.atStartOfDay(zoneId).toEpochSecond() * 1000 // 毫秒
//
//        viewModel.addTransaction(
//            title = "測試交易",
//            amount = 100.00,
//            type = TransactionType.EXPENSE,
//            category = Category.FOOD,
//            timestamp = timestamp
//        )
//    }

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

        CalendarPager(
            overBudgetDates = overBudgetDates.value,
            hasTransactionDates = monthlyTransactions.groupBy {
                Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            }.keys,
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                // 讀取該日資料
                viewModel.loadTransactionsByDate(date.year, date.monthValue, date.dayOfMonth)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 顯示當月總預算
        val formattedBudget = NumberFormat.getNumberInstance(Locale.getDefault()).format(monthlyBudget)
        Text(
            text = "本月總預算：$formattedBudget 元",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        selectedDate?.let { date ->
            Text("已選擇日期：${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}")
            BudgetSummary(date, transactions, monthlyBudget)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Divider()

        // 显示交易列表 - 这里不需要LazyColumn，因为外层已经有滚动
        if (transactions.isEmpty()) {
            Text("沒有交易紀錄。", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column {
                transactions.forEach { transaction ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text("標題：${transaction.title}")
                        Text("金額：${transaction.amount}")
                        Text("類型：${transaction.type}")
                        Text("分類：${transaction.category}")

                        Button(
                            onClick = { transactionToDelete = transaction },
                            modifier = Modifier.padding(top = 4.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("刪除")
                        }
                    }
                    Divider()
                }
            }
        }
    }
    if (transactionToDelete != null) {
        AlertDialog(
            onDismissRequest = { transactionToDelete = null }, // 點空白或返回鍵關閉
            title = { Text("確認刪除") },
            text = { Text("確定要刪除「${transactionToDelete?.title}」這筆交易嗎？") },
            confirmButton = {
                TextButton(onClick = {
                    transactionToDelete?.let { viewModel.deleteTransaction(it) }
                    transactionToDelete = null
                }) {
                    Text("確定")
                }
            },
            dismissButton = {
                TextButton(onClick = { transactionToDelete = null }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
fun BudgetSummary(date: LocalDate, transactions: List<Transaction>, monthlyBudget: Int) {
    val daysInMonth = date.lengthOfMonth()
    val dailyBudget = monthlyBudget.toDouble() / daysInMonth
    val totalSpentToday = transactions.sumOf { it.amount }
    val overBudget = totalSpentToday > dailyBudget

    Text(
        text = "今日花費：$totalSpentToday / 預算：${dailyBudget.toInt()}",
        color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
