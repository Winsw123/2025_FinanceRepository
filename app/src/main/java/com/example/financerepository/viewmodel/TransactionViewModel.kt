package com.example.financerepository.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted

// adding or deleting Result
sealed class ResultStatus {
    object Idle : ResultStatus()
    data class Success(val message: String) : ResultStatus()
    data class Error(val error: String) : ResultStatus()
}

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    //Data from DAO
    val transactions: StateFlow<List<TransactionEntity>> =
        repository.getAll()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    //repository delete function
    private val _deleteResult = MutableStateFlow<ResultStatus>(ResultStatus.Idle)
    val deleteResult: StateFlow<ResultStatus> = _deleteResult
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            try {
                repository.delete(transaction)
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
    fun addTransaction(title: String, amount: Double) {
        viewModelScope.launch {
            try {
                val transaction = TransactionEntity(title = title, amount = amount)
                repository.insertTransaction(transaction)
                _insertResult.value = ResultStatus.Success("新增成功")
            } catch (e: Exception) {
                _insertResult.value = ResultStatus.Error("新增失敗：${e.message}")
            }
        }
    }
}

// ---------- Fake Repository Interface ----------
interface TransactionRepository {
    suspend fun insertTransaction(transaction: TransactionEntity)
    suspend fun delete(transaction: TransactionEntity)
    fun getAll(): Flow<List<TransactionEntity>>
}

// ---------- Fake Repository for Testing ----------
class FakeTransactionRepository : TransactionRepository {
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    val inserted: List<TransactionEntity> get() = _transactions.value

    override suspend fun insertTransaction(transaction: TransactionEntity) {
        _transactions.value += transaction
    }

    override suspend fun delete(transaction: TransactionEntity) {
        _transactions.value -= transaction
    }

    override fun getAll(): Flow<List<TransactionEntity>> {
        return _transactions
    }
}


// ---------- Fake Model ------------
data class TransactionEntity(
    val id: Int = 0,
    val title: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)