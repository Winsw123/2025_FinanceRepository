package com.example.financerepository.ui.screen

import androidx.compose.foundation.layout.*

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.financerepository.data.model.*
import com.example.financerepository.viewmodel.TransactionViewModel
import android.app.DatePickerDialog
import java.util.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun TransactionFragment(viewModel: TransactionViewModel) {
    val transactions by viewModel.transactions.collectAsState()

    var selectedAccount by remember { mutableStateOf(Account.CASH) }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedTargetAccount by remember { mutableStateOf<Account?>(null) }
    var amount by remember { mutableStateOf("") }

    val incomeCategories = Category.values().filter { it.type == TransactionType.INCOME }
    val expenseCategories = Category.values().filter { it.type == TransactionType.EXPENSE }

    val calendar = remember { Calendar.getInstance() }
    var selectedDate by remember { mutableStateOf(calendar.timeInMillis) }

    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(12.dp)) {

        Button(
            onClick = {
                val date = Calendar.getInstance()
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        val selected = Calendar.getInstance()
                        selected.set(year, month, dayOfMonth)
                        selectedDate = selected.timeInMillis
                    },
                    date.get(Calendar.YEAR),
                    date.get(Calendar.MONTH),
                    date.get(Calendar.DAY_OF_MONTH)
                ).show()
            }, modifier = Modifier.fillMaxWidth()
        ) {
            val formattedDate = remember(selectedDate) {
                val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                format.format(Date(selectedDate))
            }
            Text(text = formattedDate)

        }
        Spacer(modifier = Modifier.height(4.dp))

        Text("帳戶名稱")
        Spacer(modifier = Modifier.height(4.dp))
        AccountSelector(selectedAccount) { selectedAccount = it }
        Spacer(modifier = Modifier.height(4.dp))

        Text("交易類型")
        Spacer(modifier = Modifier.height(4.dp))
        TransactionTypeSelector(selectedType) {
            selectedType = it
            selectedCategory = null
            selectedTargetAccount = null
        }
        Spacer(modifier = Modifier.height(4.dp))

        when (selectedType) {
            TransactionType.INCOME -> {
                Text("收入類別")
                Spacer(modifier = Modifier.height(4.dp))
                CategorySelector(incomeCategories, selectedCategory) { selectedCategory = it }
            }
            TransactionType.EXPENSE -> {
                Text("支出類別")
                Spacer(modifier = Modifier.height(4.dp))
                CategorySelector(expenseCategories, selectedCategory) { selectedCategory = it }
            }
            TransactionType.TRANSFER -> {
                Text("目標帳戶")
                Spacer(modifier = Modifier.height(4.dp))
                TargetAccountSelector(selectedAccount, selectedTargetAccount) { selectedTargetAccount = it }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("金額") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                val amt = amount.toDoubleOrNull() ?: return@Button
                val cat = selectedCategory ?: Category.OTHERE
                val title = when (selectedType) {
                    TransactionType.TRANSFER -> "${selectedAccount.name} ➜ ${selectedTargetAccount?.name}"
                    else -> cat.displayName
                }

                viewModel.addTransaction(
                    title = title,
                    amount = amt,
                    type = selectedType,
                    category = cat,
                    account = selectedAccount,
                    targetAccount = selectedTargetAccount,
                    id = 0,
                    timestamp = selectedDate
                )



                amount = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("新增交易")
        }
        Spacer(modifier = Modifier.height(4.dp))

        Divider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.LightGray,
            thickness = 1.dp
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text("交易紀錄")
        Spacer(modifier = Modifier.height(4.dp))
        transactions.reversed().forEach { tx ->
            TransactionItem(transaction = tx, onDelete = {
                viewModel.deleteTransaction(it)
            })
        }
    }
}

@Composable
fun AccountSelector(
    selected: Account,
    onSelected: (Account) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Account.values().forEach { account ->
            OutlinedButton(
                onClick = { onSelected(account) },
                colors = if (account == selected)
                    ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(account.displayName)
            }
        }
    }
}

@Composable
fun TransactionTypeSelector(
    selected: TransactionType,
    onSelected: (TransactionType) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransactionType.values().forEach { type ->
            OutlinedButton(
                onClick = { onSelected(type) },
                colors = if (type == selected)
                    ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(type.displayName)
            }
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<Category>,
    selected: Category?,
    onSelected: (Category) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            OutlinedButton(
                onClick = { onSelected(category) },
                colors = if (category == selected)
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(category.displayName)
            }
        }
    }
}

@Composable
fun TargetAccountSelector(
    current: Account,
    selected: Account?,
    onSelected: (Account) -> Unit
) {
    val otherAccounts = Account.values().filter { it != current }

    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        otherAccounts.forEach { account ->
            OutlinedButton(
                onClick = { onSelected(account) },
                colors = if (account == selected)
                    ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                else ButtonDefaults.outlinedButtonColors()
            ) {
                Text(account.displayName)
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDelete: (Transaction) -> Unit) {
    val dateString = remember(transaction.timestamp) {
        val date = java.util.Date(transaction.timestamp)
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        format.format(date)
    }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = "${transaction.title} - \$${transaction.amount}")
                Text(text = "$dateString ${transaction.type.displayName}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { onDelete(transaction) }) {
                Icon(Icons.Default.Delete, contentDescription = "刪除")
            }
        }
    }
}
