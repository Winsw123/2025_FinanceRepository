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
    val category: Category,
    val timestamp: Long = System.currentTimeMillis()

)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category{
    FOOD, SHOPPING, DAILY_NECESSITIES, MEDICAL, SOCIAL, TRAFFIC, ENTERTAINMENT, STUDY, OTHER
}

fun Transaction.isThisMonth(): Boolean {
    val calendar = Calendar.getInstance()
    val nowYear = calendar.get(Calendar.YEAR)
    val nowMonth = calendar.get(Calendar.MONTH)

    calendar.timeInMillis = this.timestamp
    val transYear = calendar.get(Calendar.YEAR)
    val transMonth = calendar.get(Calendar.MONTH)

    return nowYear == transYear && nowMonth == transMonth
}