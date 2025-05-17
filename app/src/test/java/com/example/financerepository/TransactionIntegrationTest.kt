package com.example.financerepository

import com.example.financerepository.viewmodel.TransactionViewModel
import com.example.financerepository.repository.TransactionRepositoryImpl
import com.example.financerepository.data.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionIntegrationTest {

    private lateinit var dao: FakeTransactionDao
    private lateinit var repository: TransactionRepositoryImpl
    private lateinit var viewModel: TransactionViewModel

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        dao = FakeTransactionDao()
        repository = TransactionRepositoryImpl(dao)
        viewModel = TransactionViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addTransaction goes through repository and reaches DAO`() = runTest {
        // Launch a collector to keep the StateFlow active
        val collectJob = launch {
            viewModel.transactions.collect { /* No-op */ }
        }

        viewModel.addTransaction(
            title = "Test Item",
            amount = 999.0,
            type = TransactionType.EXPENSE,
            category = "Other",
            id = 1
        )
        viewModel.addTransaction(
            title = "Test Item2",
            amount = 999.0,
            type = TransactionType.EXPENSE,
            category = "Other",
            id = 2
        )

        advanceUntilIdle()

        // 驗證 DAO 真的收到資料
        val daoList = dao.getAllTransactions().first()
        assertEquals(2, daoList.size)

        assertEquals(setOf("Test Item", "Test Item2"), daoList.map { it.title }.toSet())

        // 驗證 ViewModel 的 Flow 有資料
        val vmList = viewModel.transactions.first { it.isNotEmpty() }
        assertEquals(2, vmList.size)

        collectJob.cancel()
    }
    @Test
    fun `deleteTransaction removes from DAO and updates ViewModel`() = runTest {
        val collectJob = launch {
            viewModel.transactions.collect { /* keep StateFlow active */ }
        }

        // 加入兩筆
        viewModel.addTransaction("Delete Me", 500.0, TransactionType.EXPENSE, "Temp", id = 10)
        viewModel.addTransaction("Keep Me", 1000.0, TransactionType.EXPENSE, "Important", id = 20)
        advanceUntilIdle()

        // 取得並刪除 id = 10
        val toDelete = dao.getAllTransactions().first().find { it.id == 10 }
        if (toDelete != null) {
            viewModel.deleteTransaction(toDelete)
        }
        advanceUntilIdle()

        // 驗證 DAO 只剩一筆
        val daoListAfterDelete = dao.getAllTransactions().first()
        assertEquals(1, daoListAfterDelete.size)
        assertEquals("Keep Me", daoListAfterDelete[0].title)

        // 驗證 ViewModel Flow 也更新
        val vmList = viewModel.transactions.first()
        assertEquals(1, vmList.size)
        assertEquals("Keep Me", vmList[0].title)

        collectJob.cancel()
    }
}
