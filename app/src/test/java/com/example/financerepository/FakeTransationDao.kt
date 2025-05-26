package com.example.financerepository

import com.example.financerepository.data.dao.TransactionDao
import com.example.financerepository.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeTransactionDao : TransactionDao {

    private val transactionList = mutableListOf<Transaction>()
    private val transactionsFlow = MutableStateFlow<List<Transaction>>(emptyList())

    private fun updateFlow() {
        // 按 timestamp 倒序排列（模擬真正 Room 行為）
        transactionsFlow.value = transactionList.sortedByDescending { it.timestamp }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionsFlow.asStateFlow()
    }

    override suspend fun insertTransaction(transaction: Transaction) {
        val index = transactionList.indexOfFirst { it.id == transaction.id }
        if (index >= 0) {
            transactionList[index] = transaction // REPLACE
        } else {
            transactionList.add(transaction)
        }
        updateFlow()
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        transactionList.removeIf { it.id == transaction.id }
        updateFlow()

    }

    override suspend fun deleteAll() {
        transactionList.clear()
        updateFlow()
    }

    override suspend fun updateTransaction(transaction: Transaction) {
        val index = transactionList.indexOfFirst { it.id == transaction.id }
        if (index >= 0) {
            transactionList[index] = transaction
            updateFlow()
        }
    }
}
