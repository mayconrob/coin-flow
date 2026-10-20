/*
 * CoinFlow - Gestão Financeira Pessoal
 * Copyright (C) 2026 Maycon Roberto - GitHub: @mayconrob
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.mayconrob.coinflow.data.repository

import com.mayconrob.coinflow.data.local.TransactionDao
import com.mayconrob.coinflow.data.mapper.toDomain
import com.mayconrob.coinflow.data.mapper.toEntity
import com.mayconrob.coinflow.domain.model.Transaction
import com.mayconrob.coinflow.domain.enums.TransactionType
import com.mayconrob.coinflow.domain.model.TransactionWithCategory
import com.mayconrob.coinflow.domain.repository.ITransacaoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransacaoRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : ITransacaoRepository {

    override val all: Flow<List<TransactionWithCategory>> = transactionDao.getAllWithCategory().map { list ->
        list.map { it.toDomain() }
    }

    override val totalIncome: Flow<BigDecimal?> = all.map { list ->
        list.filter { it.transaction.type == TransactionType.ENTRY }
            .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }
    }

    override val totalExpenses: Flow<BigDecimal?> = all.map { list ->
        list.filter { it.transaction.type == TransactionType.EXIT }
            .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }
    }

    override fun totalExpensesForCategory(categoryId: Long): Flow<BigDecimal?> = all.map { list ->
        list.filter { it.transaction.categoryId == categoryId && it.transaction.type == TransactionType.EXIT }
            .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }
    }

    override suspend fun insert(transaction: Transaction): Long {
        return transactionDao.insert(transaction.toEntity())
    }

    override suspend fun update(transaction: Transaction) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun delete(transaction: Transaction) {
        transactionDao.delete(transaction.toEntity())
    }

    override suspend fun hasTransactionsForCategory(categoryId: Long): Boolean {
        return transactionDao.countTransactionsForCategory(categoryId) > 0
    }
}
