package com.example.financerepository.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financerepository.data.datastore.DataStoreManager
import com.example.financerepository.data.model.Category
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.YearMonth
import kotlinx.coroutines.flow.map
import com.example.financerepository.data.model.Account

// adding or deleting Result
sealed class ResultStatus {
    object Idle : ResultStatus()
    data class Success(val message: String) : ResultStatus()
    data class Error(val error: String) : ResultStatus()
}

class TransactionViewModel(
    private val repository: TransactionRepositoryImpl,
    private val dataStore: DataStoreManager // 用來存預算值
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
    // categorized transaction data by date
    private val _selectedDateTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val selectedDateTransactions: StateFlow<List<Transaction>> = _selectedDateTransactions

    // load data by date
//    fun loadTransactionsByDate(year: Int, month: Int, day: Int) {
//        viewModelScope.launch {
//            repository.getTransactionsByDate(year, month, day).collect {
//                _selectedDateTransactions.value = it
//            }
//        }
//    }

    val monthlyBudget: StateFlow<Int> = dataStore.monthlyBudgetFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 5000)

    fun setMonthlyBudget(newBudget: Int) {
        viewModelScope.launch {
            dataStore.saveMonthlyBudget(newBudget)
        }
    }
    // 新增：該月交易
    private val _monthlyTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val monthlyTransactions: StateFlow<List<Transaction>> = _monthlyTransactions

    // 讀取指定年月的交易
    fun loadTransactionsByMonth(year: Int, month: Int) {
        viewModelScope.launch {
            val startTimestamp = getStartOfMonthTimestamp(year, month)
            val endTimestamp = getEndOfMonthTimestamp(year, month)

            repository.getTransactionsBetween(startTimestamp, endTimestamp)
                .collect { list ->
                    _monthlyTransactions.value = list
                }
        }
    }


    fun loadTransactionsByDate(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            val startTimestamp = getStartOfDayTimestamp(year, month, day)
            val endTimestamp = getEndOfDayTimestamp(year, month, day)

            repository.getTransactionsBetween(startTimestamp, endTimestamp)
                .collect { list ->
                    _selectedDateTransactions.value = list
                }
        }
    }

    // 範例：取得當月開始時間戳（毫秒）
    private fun getStartOfMonthTimestamp(year: Int, month: Int): Long {
        val firstDay = LocalDate.of(year, month, 1).atStartOfDay(ZoneId.systemDefault())
        return firstDay.toInstant().toEpochMilli()
    }

    // 範例：取得當月結束時間戳（毫秒）
    private fun getEndOfMonthTimestamp(year: Int, month: Int): Long {
        val lastDay = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1)
            .atTime(23, 59, 59, 999_999_999).atZone(ZoneId.systemDefault())
        return lastDay.toInstant().toEpochMilli()
    }

    // 取得某日開始時間戳
    private fun getStartOfDayTimestamp(year: Int, month: Int, day: Int): Long {
        val dateTime = LocalDate.of(year, month, day).atStartOfDay(ZoneId.systemDefault())
        return dateTime.toInstant().toEpochMilli()
    }
    // 取得某日結束時間戳
    private fun getEndOfDayTimestamp(year: Int, month: Int, day: Int): Long {
        val dateTime = LocalDate.of(year, month, day).atTime(23, 59, 59, 999_999_999)
            .atZone(ZoneId.systemDefault())
        return dateTime.toInstant().toEpochMilli()
    }

    fun loadMonthlyData(currentMonth: YearMonth?) {

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

