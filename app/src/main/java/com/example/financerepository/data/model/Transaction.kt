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
    val timestamp: Long = System.currentTimeMillis(),
    val account: Account,
    val targetAccount: Account? = null
)

enum class Account {
    CASH, BANK, CREDIT_CARD
}


enum class TransactionType {
    INCOME, EXPENSE, TRANSFER
}

enum class Category(val displayName: String, val type: TransactionType) {
    // 支出類別
    FOOD("飲食", TransactionType.EXPENSE),
    SHOPPING("購物", TransactionType.EXPENSE),
    DAILY_NECESSITIES("日用品", TransactionType.EXPENSE),
    MEDICAL("醫療", TransactionType.EXPENSE),
    SOCIAL("交際", TransactionType.EXPENSE),
    TRAFFIC("交通", TransactionType.EXPENSE),
    ENTERTAINMENT("娛樂", TransactionType.EXPENSE),
    STUDY("學習", TransactionType.EXPENSE),
    OTHERE("其他", TransactionType.EXPENSE),

    // 收入類別
    SALARY("薪水", TransactionType.INCOME),
    INTEREST("利息", TransactionType.INCOME),
    REWARD("獎金", TransactionType.INCOME),

    OTHERI("其他", TransactionType.INCOME)
}
