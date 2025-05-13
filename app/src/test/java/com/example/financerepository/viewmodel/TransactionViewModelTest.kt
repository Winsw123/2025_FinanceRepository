package com.example.financerepository.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionViewModelTest {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var repository: FakeTransactionRepository

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTransactionRepository()
        viewModel = TransactionViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addTransaction adds to repository`() = runTest {
        viewModel.addTransaction("Lunch", 100.0)
        advanceUntilIdle()

        assertEquals(1, repository.inserted.size)
        assertEquals("Lunch", repository.inserted[0].title)

        val status = viewModel.insertResult.value
        assertTrue(status is ResultStatus.Success)
        if (status is ResultStatus.Success) {
            assertEquals("新增成功", status.message)
        }
    }

    @Test
    fun `deleteTransaction removes from repository`() = runTest {
        val transaction = TransactionEntity(title = "Coffee", amount = 80.0)
        repository.insertTransaction(transaction)

        viewModel.deleteTransaction(transaction)
        advanceUntilIdle()

        assertEquals(0, repository.inserted.size)

        val status = viewModel.deleteResult.value
        assertTrue(status is ResultStatus.Success)
        if (status is ResultStatus.Success) {
            assertEquals("刪除成功", status.message)
        }
    }

    @Test
    fun `transactions flow emits inserted transactions`() = runTest {
        viewModel.addTransaction("Groceries", 250.0)
        viewModel.addTransaction("Taxi", 150.0)
        advanceUntilIdle()

        val transactionList = viewModel.transactions.first { it.size == 2 }

        assertEquals(2, transactionList.size)
        assertEquals("Groceries", transactionList[0].title)
        assertEquals("Taxi", transactionList[1].title)
    }
}

//Fake Repository
class FakeTransactionRepository : TransactionRepository {
    private val _transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
    override suspend fun insertTransaction(transaction: TransactionEntity) {
        _transactions.value = _transactions.value + transaction
    }
    override suspend fun delete(transaction: TransactionEntity) {
        _transactions.value = _transactions.value - transaction
    }
    override fun getAll(): Flow<List<TransactionEntity>> {
        return _transactions
    }
    val inserted: List<TransactionEntity>
        get() = _transactions.value
}
