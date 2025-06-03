package com.example.financerepository.data.model

import androidx.room.TypeConverter
import com.example.financerepository.data.model.Account
import com.example.financerepository.data.model.Category
import com.example.financerepository.data.model.TransactionType

class Converters {

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(type: String): TransactionType = TransactionType.valueOf(type)

    @TypeConverter
    fun fromAccount(account: Account): String = account.name

    @TypeConverter
    fun toAccount(name: String): Account = Account.valueOf(name)

    @TypeConverter
    fun fromCategory(category: Category): String = category.name

    @TypeConverter
    fun toCategory(name: String): Category = Category.valueOf(name)
}
