package com.example.financerepository.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

private val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context: Context) {

    companion object {
        val BUDGET_KEY = intPreferencesKey("monthly_budget")
    }

    val monthlyBudgetFlow: Flow<Int> = context.dataStore.data
        .map { preferences -> preferences[BUDGET_KEY] ?: 5000 }

    suspend fun saveMonthlyBudget(budget: Int) {
        context.dataStore.edit { preferences ->
            preferences[BUDGET_KEY] = budget
        }
    }
}
