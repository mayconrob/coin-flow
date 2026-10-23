package com.mayconrob.coinflow.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mayconrob.coinflow.domain.enums.TransactionType
import java.math.BigDecimal

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["categoryId"])]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val amount: BigDecimal, // Valor financeiro em java.math.BigDecimal
    val type: TransactionType,
    val categoryId: Long,
    val note: String? = null,
    val dateTimestamp: Long //Timestamp em Long
)
