package com.example.financerepository.repository

import com.example.financerepository.data.dao.TransactionDao
import com.example.financerepository.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository {

    // add
    override suspend fun insertTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction)
    }
:wq
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
}
