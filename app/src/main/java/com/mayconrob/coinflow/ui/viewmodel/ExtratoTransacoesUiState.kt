package com.mayconrob.coinflow.ui.viewmodel

import com.mayconrob.coinflow.domain.model.TransactionWithCategory
import com.mayconrob.coinflow.ui.viewmodel.enums.OrdemTransacao
import com.mayconrob.coinflow.ui.viewmodel.enums.TipoFiltroData
import com.mayconrob.coinflow.ui.viewmodel.enums.TipoFiltroTransacao

data class ExtratoTransacoesUiState(
    val transacoes: List<TransactionWithCategory> = emptyList(),
    val buscaQuery: String = "",
    val categoriaFiltroIds: Set<Long> = emptySet(),
    val tipoFiltroData: TipoFiltroData = TipoFiltroData.HOJE,
    val tipoFiltroTransacao: TipoFiltroTransacao = TipoFiltroTransacao.TODAS,
    val ordemTransacao: OrdemTransacao = OrdemTransacao.MAIS_RECENTES,
    val dataInicioTimestamp: Long? = null,
    val dataFimTimestamp: Long? = null,
    val loading: Boolean = false
)
