package com.mayconrob.coinflow.ui.viewmodel

import com.mayconrob.coinflow.domain.model.TransactionWithCategory
import com.mayconrob.coinflow.domain.model.FinancialSummary
import com.mayconrob.coinflow.common.Formatters

import java.util.Calendar

data class PainelFinanceiroUiState(
    val resumo: FinancialSummary = FinancialSummary(),
    val consumosOrcamento: List<OrcamentoCategoriaUiState> = emptyList(),
    val ultimasTransacoes: List<TransactionWithCategory> = emptyList(),
    val currentMonthLabel: String = Formatters.getCurrentMonthName(),
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val loading: Boolean = false,
    val isValuesVisible: Boolean = false,
)
