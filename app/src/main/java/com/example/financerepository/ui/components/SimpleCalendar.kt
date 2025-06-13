package com.example.financerepository.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import androidx.compose.ui.text.style.TextAlign

@Composable
fun SimpleCalendar(
    year: Int,
    month: Int,
    overBudgetDates: Set<LocalDate>,
    selectedDate: LocalDate?,            // 新增由外層傳入
    onDateSelected: (LocalDate) -> Unit
) {
    val yearMonth = YearMonth.of(year, month)
    val firstDayOfMonth = yearMonth.atDay(1)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstWeekday = firstDayOfMonth.dayOfWeek.value % 7

    val totalCells = daysInMonth + firstWeekday

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            //horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("日","一","二","三","四","五","六").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(300.dp)
        ) {
            items(totalCells) { index ->
                if (index < firstWeekday) {
                    Box(modifier = Modifier.size(40.dp))
                } else {
                    val day = index - firstWeekday + 1
                    val date = yearMonth.atDay(day)
                    val isOverBudget = overBudgetDates.contains(date)
                    val isSelected = selectedDate == date

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(4.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                            .clickable {
                                onDateSelected(date)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.toString(),
                            color = if (isOverBudget) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}
