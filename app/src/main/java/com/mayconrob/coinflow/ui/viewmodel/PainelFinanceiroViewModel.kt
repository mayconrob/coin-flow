/*
 * CoinFlow - Gestão Financeira Pessoal
 * Copyright (C) 2026 Maycon Roberto GitHub: @mayconrob
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.mayconrob.coinflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mayconrob.coinflow.domain.repository.ICategoriaRepository
import com.mayconrob.coinflow.domain.repository.ITransacaoRepository
import com.mayconrob.coinflow.domain.model.FinancialSummary
import com.mayconrob.coinflow.domain.enums.TransactionType
import com.mayconrob.coinflow.common.Formatters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

import java.util.Calendar

@HiltViewModel
class PainelFinanceiroViewModel @Inject constructor(
    private val categoriaRepository: ICategoriaRepository,
    private val transacaoRepository: ITransacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PainelFinanceiroUiState())
    val uiState: StateFlow<PainelFinanceiroUiState> = _uiState.asStateFlow()

    private val _isValuesVisible = MutableStateFlow(false)

    private val currentCal = Calendar.getInstance()
    private val _selectedYear = MutableStateFlow(currentCal.get(Calendar.YEAR))
    private val _selectedMonth = MutableStateFlow(currentCal.get(Calendar.MONTH))

    init {
        viewModelScope.launch {
            combine(
                transacaoRepository.all,
                categoriaRepository.all,
                _isValuesVisible,
                _selectedYear,
                _selectedMonth
            ) { transactions, categories, isVisible, year, month ->

                val (startMonth, endMonth) = Formatters.getMonthRange(year, month)

                // 1. Receitas e Despesas do Mês Selecionado (Primeiro ao Último Milissegundo do Mês)
                val incomeInMonth = transactions
                    .filter { 
                        it.transaction.type == TransactionType.ENTRY &&
                        it.transaction.dateTimestamp in startMonth..endMonth 
                    }
                    .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }

                val expensesInMonth = transactions
                    .filter { 
                        it.transaction.type == TransactionType.EXIT &&
                        it.transaction.dateTimestamp in startMonth..endMonth 
                    }
                    .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }

                // 2. Saldo Acumulado até o último milissegundo do mês selecionado (ignora transações futuras)
                val incomeUntilEndOfMonth = transactions
                    .filter { 
                        it.transaction.type == TransactionType.ENTRY &&
                        it.transaction.dateTimestamp <= endMonth 
                    }
                    .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }

                val expensesUntilEndOfMonth = transactions
                    .filter { 
                        it.transaction.type == TransactionType.EXIT &&
                        it.transaction.dateTimestamp <= endMonth 
                    }
                    .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }

                val balanceAtEndOfMonth = incomeUntilEndOfMonth.subtract(expensesUntilEndOfMonth)

                val summary = FinancialSummary(
                    totalIncome = incomeInMonth,
                    totalExpenses = expensesInMonth,
                    currentBalance = balanceAtEndOfMonth
                )

                val consumosOrcamento = categories
                    .filter { it.type == TransactionType.EXIT && it.budgetLimit > BigDecimal.ZERO }
                    .map { category ->
                        val spent = transactions
                            .filter {
                                (it.transaction.categoryId == category.id) &&
                                        (it.transaction.type == TransactionType.EXIT) &&
                                        (it.transaction.dateTimestamp in (startMonth..endMonth))
                            }
                            .fold(BigDecimal.ZERO) { acc, item -> acc.add(item.transaction.amount) }

                        val pct = if (category.budgetLimit > BigDecimal.ZERO) {
                            spent.multiply(BigDecimal(100))
                                .divide(category.budgetLimit, 2, RoundingMode.HALF_UP)
                        } else {
                            BigDecimal.ZERO
                        }

                        OrcamentoCategoriaUiState(
                            categoria = category,
                            totalGasto = spent,
                            porcentagemConsumida = pct
                        )
                    }

                val ultimasTransacoes = transactions
                    .filter { it.transaction.dateTimestamp <= endMonth }
                    .sortedByDescending { it.transaction.dateTimestamp }
                    .take(5)

                PainelFinanceiroUiState(
                    resumo = summary,
                    consumosOrcamento = consumosOrcamento,
                    ultimasTransacoes = ultimasTransacoes,
                    currentMonthLabel = Formatters.formatMonthYear(year, month),
                    selectedYear = year,
                    selectedMonth = month,
                    loading = false,
                    isValuesVisible = isVisible
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleValueVisibility() {
        _isValuesVisible.value = !_isValuesVisible.value
    }

    fun onPreviousMonth() {
        val currentMonth = _selectedMonth.value
        if (currentMonth == 0) {
            _selectedMonth.value = 11
            _selectedYear.value = _selectedYear.value - 1
        } else {
            _selectedMonth.value = currentMonth - 1
        }
    }

    fun onNextMonth() {
        val currentMonth = _selectedMonth.value
        if (currentMonth == 11) {
            _selectedMonth.value = 0
            _selectedYear.value = _selectedYear.value + 1
        } else {
            _selectedMonth.value = currentMonth + 1
        }
    }

    fun onMonthYearSelected(year: Int, month: Int) {
        _selectedYear.value = year
        _selectedMonth.value = month
    }
}
