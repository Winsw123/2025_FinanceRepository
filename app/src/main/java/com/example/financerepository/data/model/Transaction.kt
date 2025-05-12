package com.example.financerepository.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val amount: Double,
    val type: TransactionType, // INCOME or EXPENSE
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class TransactionType {
    INCOME, EXPENSE
}
