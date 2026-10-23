package com.mayconrob.coinflow.domain.repository

import com.mayconrob.coinflow.domain.model.Transaction
import com.mayconrob.coinflow.domain.model.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface ITransacaoRepository {
    val all: Flow<List<TransactionWithCategory>>
    val totalIncome: Flow<BigDecimal?>
    val totalExpenses: Flow<BigDecimal?>
    fun totalExpensesForCategory(categoryId: Long): Flow<BigDecimal?>
    suspend fun insert(transaction: Transaction): Long
    suspend fun update(transaction: Transaction)
    suspend fun delete(transaction: Transaction)
    suspend fun hasTransactionsForCategory(categoryId: Long): Boolean
}
