package com.example.financerepository.ui.screen

import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.financerepository.viewmodel.TransactionViewModel

@Composable
fun BudgetSettingDialog(
    currentBudget: Int,
    onDismiss: () -> Unit,
    onBudgetSave: (Int) -> Unit
) {
    var budgetInput by remember { mutableStateOf(currentBudget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("設定預算") },
        text = {
            OutlinedTextField(
                value = budgetInput,
                onValueChange = { budgetInput = it },
                label = { Text("每月預算金額") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = {
                budgetInput.toIntOrNull()?.let {
                    onBudgetSave(it)
                    onDismiss()
                }
            }) {
                Text("儲存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun SettingFragment(viewModel: TransactionViewModel) {
    val currentBudget = viewModel.monthlyBudget.collectAsState().value
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        BudgetSettingDialog(
            currentBudget = currentBudget,
            onDismiss = { showDialog = false },
            onBudgetSave = {
                viewModel.setMonthlyBudget(it)
                showDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("設定", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        ListItem(
            headlineContent = { Text("設定每月金錢預算") },
            supportingContent = { Text("目前預算：$currentBudget") },
            leadingContent = {
                Icon(Icons.Default.AttachMoney, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true }
        )

        Divider()
    }
}


