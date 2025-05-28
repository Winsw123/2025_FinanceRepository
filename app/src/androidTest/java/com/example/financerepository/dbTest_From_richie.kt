package com.example.financerepository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.financerepository.data.dao.TransactionDao
import com.example.financerepository.data.db.AppDatabase
import com.example.financerepository.data.model.Category
import com.example.financerepository.data.model.Transaction
import com.example.financerepository.data.model.TransactionType
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // for testing only
            .build()
        dao = db.transactionDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertAndQueryTransaction() = runBlocking {
        val transaction = Transaction(
            id = 1,
            title = "Test Insert",
            amount = 123.0,
            type = TransactionType.EXPENSE,
            category = Category.OTHER
        )

        dao.insertTransaction(transaction)
        val result = dao.getAllTransactions().first()

        assertEquals(1, result.size)
        assertEquals("Test Insert", result[0].title)
    }
}
