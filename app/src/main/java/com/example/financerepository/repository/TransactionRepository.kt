package com.example.financerepository.repository

import com.example.financerepository.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    suspend fun insertTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateTransaction(transaction: Transaction)
    fun getAllTransactions(): Flow<List<Transaction>>
    fun getTransactionsByDate(year: Int, month: Int, day: Int): Flow<List<Transaction>>
}
