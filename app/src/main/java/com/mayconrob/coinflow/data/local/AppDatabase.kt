/*
 * CoinFlow - Gestão Financeira Pessoal
 * Copyright (C) 2026 Maycon Roberto - GitHub: @mayconrob
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package com.mayconrob.coinflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mayconrob.coinflow.R
import com.mayconrob.coinflow.domain.enums.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Database(
    entities = [CategoryEntity::class, TransactionEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coinflow_database"
                )
                    .addCallback(DatabaseCallback(context.applicationContext))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val context: Context
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialCategories(database.categoryDao())
                    }
                }
            }

            suspend fun populateInitialCategories(categoryDao: CategoryDao) {
                val defaultCategories = listOf(
                    CategoryEntity(
                        name = context.getString(R.string.text_category_salary),
                        type = TransactionType.ENTRY,
                        colorHex = "#4CAF50"
                    ),
                    CategoryEntity(
                        name = context.getString(R.string.text_category_freelance),
                        type = TransactionType.ENTRY,
                        colorHex = "#00BCD4"
                    ),
                    CategoryEntity(
                        name = context.getString(R.string.text_category_food),
                        type = TransactionType.EXIT,
                        budgetLimit = BigDecimal("800.00"),
                        colorHex = "#FF9800"
                    ),
                    CategoryEntity(
                        name = context.getString(R.string.text_category_transport),
                        type = TransactionType.EXIT,
                        budgetLimit = BigDecimal("350.00"),
                        colorHex = "#3F51B5"
                    ),
                    CategoryEntity(
                        name = context.getString(R.string.text_category_housing),
                        type = TransactionType.EXIT,
                        budgetLimit = BigDecimal("1500.00"),
                        colorHex = "#9C27B0"
                    ),
                    CategoryEntity(
                        name = context.getString(R.string.text_category_leisure),
                        type = TransactionType.EXIT,
                        budgetLimit = BigDecimal("400.00"),
                        colorHex = "#E91E63"
                    ),
                    CategoryEntity(
                        name = context.getString(R.string.text_category_health),
                        type = TransactionType.EXIT,
                        budgetLimit = BigDecimal("300.00"),
                        colorHex = "#F44336"
                    )
                )
                categoryDao.insert(defaultCategories)
            }
        }
    }
}
