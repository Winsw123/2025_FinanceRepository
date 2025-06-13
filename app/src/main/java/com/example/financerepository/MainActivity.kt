package com.example.financerepository

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Margin
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.datastore.dataStore
import androidx.lifecycle.ViewModelProvider
import com.example.financerepository.data.datastore.DataStoreManager
import com.example.financerepository.data.db.AppDatabase
import com.example.financerepository.repository.TransactionRepositoryImpl
import com.example.financerepository.ui.screen.DashboardFragment
import com.example.financerepository.ui.screen.LedgerFragment
import com.example.financerepository.ui.screen.SettingsFragment
import com.example.financerepository.ui.screen.StockFragment
import com.example.financerepository.ui.screen.TransactionFragment
import com.example.financerepository.ui.theme.FinanceRepositoryTheme
import com.example.financerepository.viewmodel.TransactionViewModel
import com.example.financerepository.viewmodel.TransactionViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDatabase.getDatabase(applicationContext).transactionDao()
        val repository = TransactionRepositoryImpl(dao)
        val dataStore = DataStoreManager(applicationContext)
        val viewModelFactory = TransactionViewModelFactory(repository, dataStore)
        val viewModel: TransactionViewModel = ViewModelProvider(this, viewModelFactory)[TransactionViewModel::class.java]

        setContent {
            FinanceRepositoryTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainScreen(viewModel: TransactionViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "總覽") },
                    label = { Text("總覽") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.AttachMoney, contentDescription = "帳本") },
                    label = { Text("帳本") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.AutoMirrored. Filled.List, contentDescription = "交易") },
                    label = { Text("交易") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Margin, contentDescription = "股票") },
                    label = { Text("股票") }
                )
                NavigationBarItem(
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "設定") },
                    label = { Text("設定") }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> DashboardFragment(viewModel)
                1 -> LedgerFragment(viewModel)
                2 -> TransactionFragment(viewModel)
                3 -> StockFragment()
                4 -> SettingsFragment(viewModel)
            }
        }
    }
}
