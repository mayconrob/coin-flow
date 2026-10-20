package com.mayconrob.coinflow.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mayconrob.coinflow.domain.enums.TransactionType
import java.math.BigDecimal

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: TransactionType,
    val budgetLimit: BigDecimal = BigDecimal.ZERO, // Teto de orçamento mensal em BigDecimal
    val colorHex: String = "#3F51B5"
)
