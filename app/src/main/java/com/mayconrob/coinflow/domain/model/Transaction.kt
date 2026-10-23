package com.mayconrob.coinflow.domain.model

import com.mayconrob.coinflow.domain.enums.TransactionType
import java.math.BigDecimal

data class Transaction(
    val id: Long = 0,
    val description: String,
    val amount: BigDecimal, //quantia
    val type: TransactionType,
    val categoryId: Long,
    val dateTimestamp: Long
)
