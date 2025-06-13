package com.example.financerepository.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.financerepository.data.dao.TransactionDao
import com.example.financerepository.data.model.Transaction
import kotlinx.coroutines.flow.Flow
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

}
