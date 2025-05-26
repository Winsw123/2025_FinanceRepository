package com.example.financerepository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.financerepository.data.dao.TransactionDao
import com.example.financerepository.data.db.AppDatabase
import com.example.financerepository.data.model.Transaction
import com.example.financerepository.data.model.TransactionType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class TransactionDaoTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var db: AppDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = db.transactionDao()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertAndRetrieveTransaction() = runBlocking {
        val transaction = Transaction(
            id = 1,
            title = "Sample",
            amount = 100.0,
            type = TransactionType.EXPENSE,
            category = "Test"
        )

        val transaction2 = Transaction(
            id = 2,
            title = "Sample1",
            amount = 100222.0,
            type = TransactionType.EXPENSE,
            category = "Test"
        )
        dao.insertTransaction(transaction)
        dao.insertTransaction(transaction2)
        val result = dao.getAllTransactions().first()
        assertEquals(2, result.size)
        assertEquals("Sample", result[0].title)
    }

    @Test
    fun deleteTransaction_removesFromDb() = runBlocking {
        val transaction = Transaction(
            id = 2,
            title = "DeleteMe",
            amount = 50.0,
            type = TransactionType.EXPENSE,
            category = "Test"
        )
        dao.insertTransaction(transaction)
        dao.deleteTransaction(transaction)
        val result = dao.getAllTransactions().first()
        assertEquals(0, result.size)
    }
}
