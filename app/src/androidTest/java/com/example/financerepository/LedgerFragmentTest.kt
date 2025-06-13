import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.financerepository.data.db.AppDatabase
import com.example.financerepository.data.model.Account
import com.example.financerepository.data.model.Category
import com.example.financerepository.data.model.Transaction
import com.example.financerepository.data.model.TransactionType
import com.example.financerepository.repository.TransactionRepositoryImpl
import com.example.financerepository.ui.screen.LedgerFragment
import com.example.financerepository.viewmodel.TransactionViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LedgerFragmentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var viewModel: TransactionViewModel

    @Before
    fun setup() {
        // 建立假的 repository 和 viewModel
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val dao = db.transactionDao()
        val repo = TransactionRepositoryImpl(dao)

        viewModel = TransactionViewModel(repo)

        // 插入資料
        runBlocking {
            dao.insertTransaction(
                Transaction(
                    id = 1,
                    title = "Test",
                    amount = 12.5,
                    type = TransactionType.EXPENSE,
                    category = Category.FOOD,
                    timestamp = System.currentTimeMillis(),
                    account = Account.BANK
                )
            )
        }

        // 載入今天的交易資料
//        val today = Calendar.getInstance()
//        viewModel.loadTransactionsByDate(
//            today.get(Calendar.YEAR),
//            today.get(Calendar.MONTH) + 1,
//            today.get(Calendar.DAY_OF_MONTH)
//        )
    }

    @Test
    fun testLedgerFragmentDisplaysData() {
        // 啟動 Compose 畫面
        composeTestRule.setContent {
            LedgerFragment(viewModel = viewModel)
        }
        // 點擊「確定日期」按鈕
        composeTestRule.onNodeWithText("確定日期").performClick()

        // 等待 "Test" 出現 , 有成功出現
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Test").fetchSemanticsNodes().isNotEmpty()
        }

        // 最後斷言它顯示
        composeTestRule.onNodeWithText("Test").assertIsDisplayed()
    }
}

