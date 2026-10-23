package com.mayconrob.coinflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mayconrob.coinflow.domain.model.Transaction
import com.mayconrob.coinflow.domain.model.TransactionWithCategory
import com.mayconrob.coinflow.domain.repository.ITransacaoRepository
import com.mayconrob.coinflow.domain.enums.TransactionType
import com.mayconrob.coinflow.common.Formatters
import com.mayconrob.coinflow.ui.viewmodel.enums.OrdemTransacao
import com.mayconrob.coinflow.ui.viewmodel.enums.TipoFiltroData
import com.mayconrob.coinflow.ui.viewmodel.enums.TipoFiltroTransacao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

private data class FiltrosExtratoParams(
    val query: String,
    val filterCatIds: Set<Long>,
    val tipoTransacao: TipoFiltroTransacao,
    val ordem: OrdemTransacao,
    val tipoData: TipoFiltroData,
    val inicio: Long?,
    val fim: Long?
)

@HiltViewModel
class ExtratoTransacoesViewModel @Inject constructor(
    private val transacaoRepository: ITransacaoRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategoryIds = MutableStateFlow<Set<Long>>(emptySet())

    private val _tipoFiltroData = MutableStateFlow(TipoFiltroData.HOJE)
    val tipoFiltroData = _tipoFiltroData.asStateFlow()

    private val _tipoFiltroTransacao = MutableStateFlow(TipoFiltroTransacao.TODAS)
    val tipoFiltroTransacao = _tipoFiltroTransacao.asStateFlow()

    private val _ordemTransacao = MutableStateFlow(OrdemTransacao.MAIS_RECENTES)
    val ordemTransacao = _ordemTransacao.asStateFlow()

    private val _dataInicio = MutableStateFlow<Long?>(null)
    val dataInicio = _dataInicio.asStateFlow()

    private val _dataFim = MutableStateFlow<Long?>(null)
    val dataFim = _dataFim.asStateFlow()

    private val _uiState = MutableStateFlow(ExtratoTransacoesUiState())
    val uiState: StateFlow<ExtratoTransacoesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val filtroDataFlow = combine(_tipoFiltroData, _dataInicio, _dataFim) { tipo, inicio, fim ->
                Triple(tipo, inicio, fim)
            }

            val filtrosParamsFlow = combine(
                _searchQuery,
                _selectedCategoryIds,
                _tipoFiltroTransacao,
                _ordemTransacao,
                filtroDataFlow
            ) { query, filterCatIds, tipoTransacao, ordem, filtroData ->
                val (tipoData, inicio, fim) = filtroData
                FiltrosExtratoParams(query, filterCatIds, tipoTransacao, ordem, tipoData, inicio, fim)
            }

            combine(
                transacaoRepository.all,
                filtrosParamsFlow
            ) { transactions, params ->
                val transacoesFiltradas = transactions.filter { item ->
                    val timestamp = item.transaction.dateTimestamp

                    // 1. Filtro por Data (Sem considerar horário)
                    val matchesDate = when (params.tipoData) {
                        TipoFiltroData.HOJE -> {
                            val (startToday, endToday) = Formatters.getTodayRange()
                            timestamp in startToday..endToday
                        }
                        TipoFiltroData.ULTIMOS_7_DIAS -> {
                            val (start7, end7) = Formatters.getLast7DaysRange()
                            timestamp in start7..end7
                        }
                        TipoFiltroData.PERIODO -> {
                            if (params.inicio != null && params.fim != null) {
                                val start = Formatters.getStartOfDay(params.inicio)
                                val end = Formatters.getEndOfDay(params.fim)
                                timestamp in start..end
                            } else true
                        }
                    }

                    // 2. Filtro por Busca de Texto
                    val matchesQuery = if (params.query.isNotBlank()) {
                        if (item.transaction.description.isBlank()) {
                            false
                        } else {
                            item.run { transaction.description.contains(params.query, ignoreCase = true) }
                        }
                    } else {
                        true
                    }

                    // 3. Filtro por Categorias Selecionadas (Cumulativo)
                    val matchesCategory = params.filterCatIds.isEmpty() || item.transaction.categoryId in params.filterCatIds

                    // 4. Filtro por Tipo de Transação (Todas / Receitas / Despesas)
                    val matchesType = when (params.tipoTransacao) {
                        TipoFiltroTransacao.TODAS -> true
                        TipoFiltroTransacao.ENTRADAS -> item.transaction.type == TransactionType.ENTRY
                        TipoFiltroTransacao.SAIDAS -> item.transaction.type == TransactionType.EXIT
                    }

                    matchesDate && matchesQuery && matchesCategory && matchesType
                }

                // 5. Aplicação da Ordenação dos Resultados
                val transacoesOrdenadas = when (params.ordem) {
                    OrdemTransacao.MAIS_RECENTES -> transacoesFiltradas.sortedByDescending { it.transaction.dateTimestamp }
                    OrdemTransacao.MAIS_ANTIGAS -> transacoesFiltradas.sortedBy { it.transaction.dateTimestamp }
                    OrdemTransacao.MAIOR_VALOR -> transacoesFiltradas.sortedByDescending { it.transaction.amount }
                    OrdemTransacao.MENOR_VALOR -> transacoesFiltradas.sortedBy { it.transaction.amount }
                }

                ExtratoTransacoesUiState(
                    transacoes = transacoesOrdenadas,
                    buscaQuery = params.query,
                    categoriaFiltroIds = params.filterCatIds,
                    tipoFiltroData = params.tipoData,
                    tipoFiltroTransacao = params.tipoTransacao,
                    ordemTransacao = params.ordem,
                    dataInicioTimestamp = params.inicio,
                    dataFimTimestamp = params.fim,
                    loading = false
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onCategoryFilterChanged(categoryId: Long?) {
        if (categoryId == null) {
            _selectedCategoryIds.value = emptySet()
        } else {
            val current = _selectedCategoryIds.value.toMutableSet()
            if (current.contains(categoryId)) {
                current.remove(categoryId)
            } else {
                current.add(categoryId)
            }
            _selectedCategoryIds.value = current
        }
    }

    fun onTipoFiltroDataChanged(tipo: TipoFiltroData) {
        _tipoFiltroData.value = tipo
    }

    fun onTipoFiltroTransacaoChanged(tipo: TipoFiltroTransacao) {
        _tipoFiltroTransacao.value = tipo
    }

    fun onOrdemTransacaoChanged(ordem: OrdemTransacao) {
        _ordemTransacao.value = ordem
    }

    fun onPeriodoDataChanged(dataInicio: Long?, dataFim: Long?) {
        _dataInicio.value = dataInicio
        _dataFim.value = dataFim
        _tipoFiltroData.value = TipoFiltroData.PERIODO
    }

    fun addTransaction(description: String, amount: BigDecimal, type: TransactionType, categoryId: Long) {
        viewModelScope.launch {
            val transaction = Transaction(
                description = description,
                amount = amount,
                type = type,
                categoryId = categoryId,
                dateTimestamp = System.currentTimeMillis()
            )
            transacaoRepository.insert(transaction)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transacaoRepository.update(transaction)
        }
    }

    fun deleteTransaction(item: TransactionWithCategory) {
        viewModelScope.launch {
            transacaoRepository.delete(item.transaction)
        }
    }
}
