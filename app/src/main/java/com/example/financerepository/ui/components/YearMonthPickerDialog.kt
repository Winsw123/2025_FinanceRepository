package com.example.financerepository.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.YearMonth
@Composable
fun YearMonthPickerDialog(
    current: YearMonth,
    onDismiss: () -> Unit,
    onYearMonthSelected: (YearMonth) -> Unit
) {
    var selectedYear by remember { mutableStateOf(current.year) }
    var selectedMonth by remember { mutableStateOf(current.monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onYearMonthSelected(YearMonth.of(selectedYear, selectedMonth))
            }) {
                Text("確定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
        title = { Text("選擇年份與月份") },
        text = {
            Column {
                // 年份選擇
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("年：", modifier = Modifier.width(40.dp))
                    DropdownMenuWithIntRange(
                        range = (2000..2100),
                        selected = selectedYear,
                        onSelected = { selectedYear = it }
                    )
                }
                Spacer(Modifier.height(8.dp))
                // 月份選擇
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("月：", modifier = Modifier.width(40.dp))
                    DropdownMenuWithIntRange(
                        range = (1..12),
                        selected = selectedMonth,
                        onSelected = { selectedMonth = it }
                    )
                }
            }
        }
    )
}
