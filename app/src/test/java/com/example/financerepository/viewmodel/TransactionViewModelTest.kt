package com.example.financerepository.viewmodel
//
//import kotlinx.coroutines.Dispatchers
//import com.example.financerepository.repository.TransactionRepositoryImpl
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.test.*
//import org.junit.After
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//
//@OptIn(ExperimentalCoroutinesApi::class)
//class TransactionViewModelTest {
//
//    private lateinit var viewModel: TransactionViewModel
//    private lateinit var repository: TransactionRepositoryImpl
//    private val testDispatcher = StandardTestDispatcher()
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//        val dao = FakeTransactionDao()
//        repository = TransactionRepositoryImpl(dao)
//        viewModel = TransactionViewModel(repository)
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//    }
//
//    @Test
//    fun `addTransaction adds to repository`() = runTest {
//        viewModel.addTransaction("Lunch", 100.0)
//        advanceUntilIdle()
//
//        val inserted = repository.getAll().first()
//        assertEquals(1, inserted.size)
//        assertEquals("Lunch", inserted[0].title)
//
//        val status = viewModel.insertResult.value
//        assertTrue(status is ResultStatus.Success)
//        if (status is ResultStatus.Success) {
//            assertEquals("新增成功", status.message)
//        }
//    }
//
//    @Test
//    fun `deleteTransaction removes from repository`() = runTest {
//        val transaction = TransactionEntity(title = "Coffee", amount = 80.0)
//        repository.insertTransaction(transaction)
//
//        viewModel.deleteTransaction(transaction)
//        advanceUntilIdle()
//
//        val inserted = repository.getAll().first()
//        assertEquals(0, inserted.size)
//
//        val status = viewModel.deleteResult.value
//        assertTrue(status is ResultStatus.Success)
//        if (status is ResultStatus.Success) {
//            assertEquals("刪除成功", status.message)
//        }
//    }
//
//    @Test
//    fun `transactions flow emits inserted transactions`() = runTest {
//        viewModel.addTransaction("Groceries", 250.0, type = TransactionType.EXPENSE, category = "other")
//        viewModel.addTransaction("Taxi", 150.0, type = TransactionType.EXPENSE, category = "transport")
//        advanceUntilIdle()
//
//        val transactionList = viewModel.transactions.first { it.size == 2 }
//
//        assertEquals(2, transactionList.size)
//        assertEquals("Groceries", transactionList[0].title)
//        assertEquals("Taxi", transactionList[1].title)
//    }
//}
//
//
//// Fake DAO implementation
//class FakeTransactionDao : TransactionDao {
//    private val transactions = MutableStateFlow<List<TransactionEntity>>(emptyList())
//
//    override suspend fun insertTransaction(transaction: TransactionEntity) {
//        transactions.value = transactions.value + transaction
//    }
//
//    override suspend fun deleteTransaction(transaction: TransactionEntity) {
//        transactions.value = transactions.value - transaction
//    }
//
//    override fun getAllTransactions(): Flow<List<TransactionEntity>> {
//        return transactions
//    }
//}
//
//// DAO interface
//interface TransactionDao {
//    suspend fun insertTransaction(transaction: TransactionEntity)
//    suspend fun deleteTransaction(transaction: TransactionEntity)
//    fun getAllTransactions(): Flow<List<TransactionEntity>>
//}
//
//// Transaction entity and enums
//data class TransactionEntity(
//    val id: Int = 0,
//    val title: String,
//    val amount: Double,
//    val type: TransactionType = TransactionType.EXPENSE,
//    val category: String = "Other",
//    val timestamp: Long = System.currentTimeMillis()
//)
//
//enum class TransactionType {
//    INCOME,
//    EXPENSE
//}
