package com.mayconrob.coinflow.ui.viewmodel

import com.mayconrob.coinflow.domain.model.Category

data class GerenciamentoCategoriasUiState(
    val categorias: List<Category> = emptyList(),
    val errorMessage: String? = null,
    val Loading: Boolean = false
)
