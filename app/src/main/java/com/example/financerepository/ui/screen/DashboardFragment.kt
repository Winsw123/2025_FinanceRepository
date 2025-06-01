package com.example.financerepository.ui.screen

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.compose.ui.graphics.Color as ComposeColor
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun DashboardFragment(viewModel: TransactionViewModel) {
//pie chart part
    val expense by viewModel.monthExpense.collectAsState()
    val expenseForLineChart by viewModel.monthExpenseGroupByDay.collectAsState()
    val totalExpense = expense.values.sum().toFloat()
    val income by viewModel.monthIncome.collectAsState()
    val incomeForLineChart by viewModel.monthIncomeGroupByDay.collectAsState()
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

                    val incomeEntries = incomeForLineChart.entries.map { (day, amount) ->
                        Entry(day.toFloat(), amount.toFloat())
                    }.sortedBy { it.x }
                    val expenseEntries = expenseForLineChart.entries.map { (day, amount) ->
                        Entry(day.toFloat(), amount.toFloat())
                    }.sortedBy { it.x }

                    val incomeDataSet = LineDataSet( incomeEntries, "Daily Income").apply {
                        color = Color.Blue.toArgb()
                        valueTextSize = 12f
                        lineWidth = 2f
                        circleRadius = 4f
                        setCircleColor(Color.Blue.toArgb())
                        setDrawValues(false)
                    }
                    val expenseDataSet = LineDataSet(expenseEntries, "Daily Expense").apply {
                        color = Color.Red.toArgb()
                        valueTextSize = 12f
                        lineWidth = 2f
                        circleRadius = 4f
                        setCircleColor(Color.Red.toArgb())
                        setDrawValues(false)
                    }
                    val lineData = LineData(incomeDataSet,expenseDataSet)
                    AndroidView(
                        factory = { context ->
                            LineChart(context).apply {
                                // 設置 Line 數據
                                data = lineData
                                description.isEnabled = false
                                legend.isEnabled = false
                                animateX(1000)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(top = 32.dp)
                            .offset(y = (-80).dp)
                    )

                }
                2 -> {
                    val expenseEntries = expense.entries.mapIndexed { index, entry ->
                        BarEntry(index.toFloat(), entry.value.toFloat())
                    }
                    val expenseLabels = expense.keys.toList()

                    val expenseBarDataSet = BarDataSet(expenseEntries, "Expense by Category").apply {
                        color = Color.Blue.toArgb()
                        valueTextSize = 12f
                    }

                    val barData = BarData(expenseBarDataSet)

                    AndroidView(factory = { context ->
                        BarChart(context).apply {
                            data = barData
                            val labelStrings = expenseLabels.map { it.name }
                            // X 軸 label 設定
                            xAxis.valueFormatter = IndexAxisValueFormatter(labelStrings)
                            xAxis.position = XAxis.XAxisPosition.BOTTOM
                            xAxis.granularity = 1f
                            xAxis.setDrawGridLines(false)

                            axisLeft.axisMinimum = 0f
                            axisRight.isEnabled = false
                            description.isEnabled = false
                            legend.isEnabled = false
                            animateY(1000)
                        }
                    }, modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp))

                }
            }
        }
        // indicator
        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .offset(y = (-10).dp),
            activeColor = Color.Cyan
        )
    }
}


