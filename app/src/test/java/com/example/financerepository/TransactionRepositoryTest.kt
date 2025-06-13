package com.example.financerepository

import com.example.financerepository.data.dao.TransactionDao
import com.example.financerepository.data.model.Transaction
import com.example.financerepository.repository.TransactionRepositoryImpl
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionRepositoryTest {

    private lateinit var dao: TransactionDao
    private lateinit var repository: TransactionRepositoryImpl

    private val testTransactions = listOf(
        Transaction(id = 1, title = "Lunch", amount = 10.0, type = com.example.financerepository.data.model.TransactionType.EXPENSE, category = com.example.financerepository.data.model.Category.FOOD, timestamp = getMillis(2025, 6, 10, 12, 0)),
        Transaction(id = 2, title = "Dinner", amount = 20.0, type = com.example.financerepository.data.model.TransactionType.EXPENSE, category = com.example.financerepository.data.model.Category.FOOD, timestamp = getMillis(2025, 6, 10, 19, 0)),
        Transaction(id = 3, title = "Book", amount = 15.0, type = com.example.financerepository.data.model.TransactionType.EXPENSE, category = com.example.financerepository.data.model.Category.STUDY, timestamp = getMillis(2025, 6, 11, 14, 0))
    )

    @Before
    fun setup() {
        dao = mock(TransactionDao::class.java)
        repository = TransactionRepositoryImpl(dao)
    }

    @Test
    fun testGetTransactionsByDate_returnsOnlySelectedDate() = runTest {
        // Arrange: 模擬 DAO 的 getTransactionsByDate 回傳過濾後的資料
        val selectedDateMillisRange = getDateRangeMillis(2025, 6, 10)
        val expectedTransactions = testTransactions.filter {
            it.timestamp in selectedDateMillisRange.first..selectedDateMillisRange.second
        }

        `when`(dao.getTransactionsByDate(selectedDateMillisRange.first, selectedDateMillisRange.second))
            .thenReturn(flow { emit(expectedTransactions) })

        // Act: 呼叫 repository 的方法
        val flow: Flow<List<Transaction>> = repository.getTransactionsByDate(2025, 6, 10)

        // Assert: collect flow 並檢查資料
        flow.collect { result ->
            assertEquals(2, result.size)
            assertTrue(result.all { it.timestamp in selectedDateMillisRange.first..selectedDateMillisRange.second })
        }
    }

    // Helper function: 取得指定日期某個時間的毫秒數
    private fun getMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    // Helper function: 取得當天 00:00:00.000 和 23:59:59.999 的毫秒數範圍
    private fun getDateRangeMillis(year: Int, month: Int, day: Int): Pair<Long, Long> {
        val startCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val endCal = startCal.clone() as Calendar
        endCal.add(Calendar.DAY_OF_MONTH, 1)
        val start = startCal.timeInMillis
        val end = endCal.timeInMillis - 1
        return Pair(start, end)
    }
}
