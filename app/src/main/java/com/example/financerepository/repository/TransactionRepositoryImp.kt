package com.example.financerepository.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Query
import com.example.financerepository.data.dao.TransactionDao
import com.example.financerepository.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    // add
    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction)
    }

    // delete
    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction)
    }

    // update
    override suspend fun updateTransaction(transaction: Transaction) {
        dao.updateTransaction(transaction)
    }

    // get transactions
    override fun getAllTransactions(): Flow<List<Transaction>> {
        return dao.getAllTransactions()
    }

    override fun getTransactionsByDate(year: Int, month: Int, day: Int): Flow<List<Transaction>> {
        val startCal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1) // Calendar.MONTH 是從 0 開始
            set(Calendar.DAY_OF_MONTH, day)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val endCal = startCal.clone() as Calendar
        endCal.add(Calendar.DAY_OF_MONTH, 1)

        val startMillis = startCal.timeInMillis
        val endMillis = endCal.timeInMillis - 1

        return dao.getTransactionsByDate(startMillis, endMillis)
    }

    fun getTransactionsByMonth(year: Int, month: Int): Flow<List<Transaction>> {
        val startTimestamp = LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
        val endTimestamp = LocalDate.of(year, month, 1)
            .plusMonths(1)
            .minusDays(1)
            .atTime(23, 59, 59, 999_999_999)
            .atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
        return dao.getTransactionsByDate(startTimestamp, endTimestamp)
    }
    fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>> {
        return dao.getTransactionsByDate(start, end)
    }

}
