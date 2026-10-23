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
import com.mayconrob.coinflow.domain.model.Category
import com.mayconrob.coinflow.domain.repository.ICategoriaRepository
import com.mayconrob.coinflow.domain.repository.ITransacaoRepository
import com.mayconrob.coinflow.domain.enums.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class GerenciamentoCategoriasViewModel @Inject constructor(
    private val categoriaRepository: ICategoriaRepository,
    private val transacaoRepository: ITransacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GerenciamentoCategoriasUiState())
    val uiState: StateFlow<GerenciamentoCategoriasUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            categoriaRepository.all.collect { categories ->
                _uiState.value = _uiState.value.copy(
                    categorias = categories
                )
            }
        }
    }

    fun addCategory(name: String, type: TransactionType, budgetLimit: BigDecimal, colorHex: String) {
        viewModelScope.launch {
            val category = Category(
                name = name,
                type = type,
                budgetLimit = budgetLimit,
                colorHex = colorHex
            )
            categoriaRepository.insert(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch {
            categoriaRepository.update(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            val hasTransactions = transacaoRepository.hasTransactionsForCategory(category.id)
            if (hasTransactions) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Não é possível excluir a categoria \"${category.name}\" pois existem transações vinculadas a ela."
                )
            } else {
                categoriaRepository.delete(category)
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
