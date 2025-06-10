package com.example.financerepository.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financerepository.data.model.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

import com.example.financerepository.data.model.Transaction
import com.example.financerepository.data.model.TransactionType
import com.example.financerepository.repository.TransactionRepositoryImpl

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
    //repository delete function
    private val _deleteResult = MutableStateFlow<ResultStatus>(ResultStatus.Idle)
    val deleteResult: StateFlow<ResultStatus> = _deleteResult
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            try {
                repository.deleteTransaction(transaction)
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
    fun addTransaction(title: String, amount: Double, type: TransactionType,category: Category, id: Int = 0) {
        viewModelScope.launch {
            try {
                val transaction = Transaction(id = id, title = title, amount = amount, type = type, category = category)
                repository.insertTransaction(transaction)
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
    fun loadTransactionsByDate(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            repository.getTransactionsByDate(year, month, day).collect {
                _selectedDateTransactions.value = it
            }
        }
    }

}

