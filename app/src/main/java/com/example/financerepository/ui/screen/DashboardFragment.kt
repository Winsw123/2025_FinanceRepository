package com.example.financerepository.ui.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.toArgb
import com.example.financerepository.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.compose.ui.graphics.Color as ComposeColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.compose.runtime.mutableStateOf

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DashboardFragment(viewModel: TransactionViewModel) {
//pie chart part
    val expense by viewModel.MonthExpense.collectAsState()
    val totalExpense =  expense.values.sum().toFloat()
    val income by viewModel.MonthIncome.collectAsState()
    val totalIncome = income.values.sum().toFloat()
    val pagerState = rememberPagerState() // Pager state to manage the current page
    val pages = listOf("Pie Chart", "Bar Chart", "Line Chart")

    Column(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            count = pages.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(630.dp)
        ) { pageIndex ->
            when(pageIndex) {
                0 ->{
                    //pie
                    AndroidView(
                        factory = { context ->
                            PieChart(context).apply {
                                // 設置 PieChart 數據
                                val entries = listOf(
                                    PieEntry(totalExpense, "Expense"),
                                    PieEntry(totalIncome, "Income")
                                )

                                val dataSet = PieDataSet(entries, "Expenses and Income").apply {
                                    // 設定每個 slice 顏色
                                    colors = listOf(ComposeColor.Red.toArgb(), ComposeColor.Green.toArgb())
                                    sliceSpace = 2f
                                    valueTextSize = 14f
                                    valueTextColor = Color.Black.toArgb()
                                }

                                val pieData = PieData(dataSet)
                                this.data = pieData

                                // setting pie
                                this.centerText = "Monthly"
                                this.setHoleColor(Color.Transparent.toArgb())
                                this.description.isEnabled = false
                                this.legend.isEnabled = false
                                this.setUsePercentValues(true)
                                this.animateY(1000)
                                this.invalidate() // 刷新 PieChart
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(top = 32.dp)
                            .offset(y = (-80).dp)
                    )
                    Text(
                        text = "Total Expense:${totalExpense}  Total Income:\$${totalIncome}",
                        modifier = Modifier.padding(top = 100.dp).offset(y = 130.dp).offset(10.dp),
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    )
                }
                1 -> {
                    Text(
                        text = "Total Expense: $${totalExpense}",
                        style = TextStyle(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(top = 32.dp)
                    )

                }
                2 -> {

                }
            }
        }
        // indicator
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp),
            activeColor = Color.Cyan
        )
    }
}


