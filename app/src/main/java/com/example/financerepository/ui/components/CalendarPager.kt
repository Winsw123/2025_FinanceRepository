package com.example.financerepository.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarPager(
    overBudgetDates: Set<LocalDate>,
    hasTransactionDates: Set<LocalDate>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var internalSelectedDate by remember { mutableStateOf(selectedDate) }

    var showYearMonthPicker by remember { mutableStateOf(false) }

    val daysInMonth = currentYearMonth.lengthOfMonth()
    val firstDayOfMonth = currentYearMonth.atDay(1)
    val firstWeekday = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0

    val dayList = buildList {
        repeat(firstWeekday) { add(null) }
        for (day in 1..daysInMonth) {
            add(currentYearMonth.atDay(day))
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        // 年月顯示與左右按鈕
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        ) {
            IconButton(onClick = {
                currentYearMonth = currentYearMonth.minusMonths(1)
            }) {
                Text("<")
            }

            Text(
                text = "${currentYearMonth.year} 年 ${currentYearMonth.monthValue} 月",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .clickable { showYearMonthPicker = true },
                textAlign = TextAlign.Center
            )

            IconButton(onClick = {
                currentYearMonth = currentYearMonth.plusMonths(1)
            }) {
                Text(">")
            }
        }

        // 星期顯示
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("日", "一", "二", "三", "四", "五", "六").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        // 日期格子
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            userScrollEnabled = false
        ) {
            items(dayList) { date ->
                if (date == null) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    val isOverBudget = overBudgetDates.contains(date)
                    val isSelected = internalSelectedDate == date
                    val isHasTransaction = hasTransactionDates.contains(date)

                    val textStyle = if (isHasTransaction) {
                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                    } else {
                        MaterialTheme.typography.bodyMedium
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(2.dp)
                            .background(
                                when {
                                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                    isOverBudget -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                    isHasTransaction && !isOverBudget -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else -> Color.Transparent
                                }
                            )
                            .clickable {
                                internalSelectedDate = date
                                onDateSelected(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            textAlign = TextAlign.Center,
                            color = when {
                                isOverBudget -> MaterialTheme.colorScheme.error
                                isHasTransaction && !isOverBudget -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onBackground
                            },
                            style = if (isHasTransaction)
                                MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                            else
                                MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
    if (overBudgetDates.isNotEmpty()) {
        Text(
            text = "⚠ 超過預算的日期：",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )

        // 列出所有超過預算的日子，按時間排序
        Text(
            text = overBudgetDates.sorted().joinToString(", ") { date ->
                "${date.monthValue}/${date.dayOfMonth}"
            },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        )
    }

    // 彈出選擇年月對話框
    if (showYearMonthPicker) {
        YearMonthPickerDialog(
            current = currentYearMonth,
            onDismiss = { showYearMonthPicker = false },
            onYearMonthSelected = {
                currentYearMonth = it
                showYearMonthPicker = false
            }
        )
    }
}
