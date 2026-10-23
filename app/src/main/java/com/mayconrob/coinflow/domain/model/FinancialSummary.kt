package com.mayconrob.coinflow.domain.model

import java.math.BigDecimal

data class FinancialSummary(
    val totalIncome: BigDecimal = BigDecimal.ZERO, // Entradas Totais (Renda total)
    val totalExpenses: BigDecimal = BigDecimal.ZERO, //saidas totais
    val currentBalance: BigDecimal = BigDecimal.ZERO //saldo Atual
)