package com.example.financerepository.ui.screen

import android.icu.text.NumberFormat
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import com.example.financerepository.data.model.Transaction
import com.example.financerepository.data.model.TransactionType
import com.example.financerepository.ui.components.CalendarPager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import java.text.DecimalFormat

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

            val spendByDate = monthlyTransactions
                .filter { it.type == TransactionType.EXPENSE }
                .groupBy {
                    Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
                }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            val overDates = spendByDate.filter { it.value > dailyBudget }.keys
            overBudgetDates.value = overDates
        } else {
            overBudgetDates.value = emptySet()
        }
    }
// test unit
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
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        CalendarPager(
            overBudgetDates = overBudgetDates.value,
            hasTransactionDates = monthlyTransactions.groupBy {
                Instant.ofEpochMilli(it.timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
            }.keys,
            selectedDate = selectedDate,
            onDateSelected = { date ->
                selectedDate = date
                viewModel.loadTransactionsByDate(date.year, date.monthValue, date.dayOfMonth)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        // 已選擇日期 - 加邊框和底色，增加辨識度
        selectedDate?.let { date ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "日期：${date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BudgetSummary(date, transactions, monthlyBudget)
                }
            }
        }

        Divider()

        // 显示交易列表 - 这里不需要LazyColumn，因为外层已经有滚动
        if (transactions.isEmpty()) {
            Text("沒有交易紀錄。", style = MaterialTheme.typography.bodyMedium)
        } else {
            Column {
                transactions.forEach { transaction ->
                    val color = when (transaction.type) {
                        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                        TransactionType.TRANSFER -> MaterialTheme.colorScheme.secondary
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {

                            // 第一行：Icon + 標題 + 金額
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val icon = when (transaction.type) {
                                        TransactionType.EXPENSE -> Icons.Default.ArrowDownward
                                        TransactionType.INCOME -> Icons.Default.ArrowUpward
                                        TransactionType.TRANSFER -> Icons.Default.SwapHoriz
                                    }

                                    val color = when (transaction.type) {
                                        TransactionType.EXPENSE -> MaterialTheme.colorScheme.error
                                        TransactionType.INCOME -> MaterialTheme.colorScheme.primary
                                        TransactionType.TRANSFER -> MaterialTheme.colorScheme.secondary
                                    }

                                    Icon(
                                        imageVector = icon,
                                        contentDescription = transaction.type.name,
                                        tint = color,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = transaction.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = color
                                    )
                                }

                                Text(
                                    text = "${transaction.amount}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = color
                                )
                            }

                            // 第二行：類型與分類
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "類型：${transaction.type.displayName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = "分類：${transaction.category.displayName}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            // 刪除按鈕
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { transactionToDelete = transaction }) {
                                    Text("刪除", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
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
    val totalSpentToday = transactions
        .filter { it.type == TransactionType.EXPENSE }
        .sumOf { it.amount }
    val overBudget = totalSpentToday > dailyBudget

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
            .background(
                color = if (overBudget) MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "今日花費：",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        val formatter = DecimalFormat("#,###")
        Text(
            text = formatter.format(totalSpentToday),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = if (overBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(end = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "預算：",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = formatter.format(dailyBudget),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
