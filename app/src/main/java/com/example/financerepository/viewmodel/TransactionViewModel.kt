package com.example.financerepository.viewmodel


import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financerepository.data.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.example.financerepository.data.model.Transaction
import com.example.financerepository.data.model.TransactionType
import com.example.financerepository.data.model.isThisMonth
import com.example.financerepository.data.model.toDayOfMonth
import com.example.financerepository.repository.TransactionRepositoryImpl
import com.example.financerepository.data.model.Account
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.days

// adding or deleting Result
sealed class ResultStatus {
    object Idle : ResultStatus()
    data class Success(val message: String) : ResultStatus()
    data class Error(val error: String) : ResultStatus()
}

class TransactionViewModel(
    private val repository: TransactionRepositoryImpl
) : ViewModel() {

    //Data from DAO
    val transactions: StateFlow<List<Transaction>> =
        repository.getAllTransactions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // for TransactionFragment
    private val _addedTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val addedTransactions: StateFlow<List<Transaction>> = _addedTransactions

    //repository delete function
    private val _deleteResult = MutableStateFlow<ResultStatus>(ResultStatus.Idle)
    val deleteResult: StateFlow<ResultStatus> = _deleteResult
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
                _addedTransactions.value = _addedTransactions.value - transaction
                _deleteResult.value = ResultStatus.Success("刪除成功")
            } catch (e: Exception) {
                _deleteResult.value = ResultStatus.Error("刪除失敗：${e.message}")
            }
        }
    }
    //response message from here to UI
    private val _insertResult = MutableStateFlow<ResultStatus>(ResultStatus.Idle)
    val insertResult: StateFlow<ResultStatus> = _insertResult
    // function for adding transaction
    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: Category,
        account: Account,
        targetAccount: Account? = null,
        id: Int = 0,
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(
                    id = id,
                    title = title,
                    amount = amount,
                    type = type,
                    category = category,
                    account = account,
                    targetAccount = targetAccount,
                    timestamp = timestamp
                )
                repository.insertTransaction(transaction)
                _addedTransactions.value = _addedTransactions.value + transaction
                _insertResult.value = ResultStatus.Success("新增成功")
            } catch (e: Exception) {
                _insertResult.value = ResultStatus.Error("新增失敗：${e.message}")
            }
        }
    }


    //calculate month expense
    val monthExpense = transactions
        .map { list ->
            list.filter { it.type == TransactionType.EXPENSE && it.isThisMonth() }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    // calculate month income
    val monthIncome = transactions
        .map { list ->
            list.filter { it.type == TransactionType.INCOME && it.isThisMonth() }
                .groupBy { it.category }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    val monthExpenseGroupByDay = transactions
        .map { list ->
            list.filter { it.type == TransactionType.EXPENSE && it.isThisMonth() }
                .groupBy { it.timestamp.toDayOfMonth() }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
    val monthIncomeGroupByDay = transactions
        .map { list ->
            list.filter { it.type == TransactionType.INCOME && it.isThisMonth() }
                .groupBy { it.timestamp.toDayOfMonth() }
                .mapValues { entry -> entry.value.sumOf { it.amount } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())
}

