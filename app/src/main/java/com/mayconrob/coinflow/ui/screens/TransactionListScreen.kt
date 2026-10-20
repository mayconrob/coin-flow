package com.mayconrob.coinflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mayconrob.coinflow.domain.model.Category
import com.mayconrob.coinflow.domain.model.TransactionWithCategory
import com.mayconrob.coinflow.ui.components.DateRangePickerDialog
import com.mayconrob.coinflow.ui.components.TransactionItem
import com.mayconrob.coinflow.ui.viewmodel.ExtratoTransacoesUiState
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.ui.res.stringResource
import com.mayconrob.coinflow.R
import com.mayconrob.coinflow.ui.viewmodel.enums.OrdemTransacao
import com.mayconrob.coinflow.ui.viewmodel.enums.TipoFiltroData
import com.mayconrob.coinflow.ui.viewmodel.enums.TipoFiltroTransacao
import com.mayconrob.coinflow.common.Formatters
import com.mayconrob.coinflow.ui.theme.ExpenseRed

@Composable
fun TransactionListScreen(
    state: ExtratoTransacoesUiState,
    categories: List<Category>,
    onSearchQueryChanged: (String) -> Unit,
    onCategoryFilterChanged: (Long?) -> Unit,
    onTipoFiltroDataChanged: (TipoFiltroData) -> Unit,
    onTipoFiltroTransacaoChanged: (TipoFiltroTransacao) -> Unit,
    onOrdemTransacaoChanged: (OrdemTransacao) -> Unit,
    onPeriodoDataChanged: (Long?, Long?) -> Unit,
    onEditTransactionClick: (TransactionWithCategory) -> Unit,
    onDeleteTransactionClick: (TransactionWithCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateRangePicker by remember { mutableStateOf(false) }
    var transactionToDelete by remember { mutableStateOf<TransactionWithCategory?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.title_financial_statement),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = state.buscaQuery,
            onValueChange = onSearchQueryChanged,
            label = { Text(stringResource(R.string.text_search_transaction)) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow {
            item {
                FilterChip(
                    selected = state.tipoFiltroData == TipoFiltroData.HOJE,
                    onClick = { onTipoFiltroDataChanged(TipoFiltroData.HOJE) },
                    label = { Text(stringResource(R.string.text_today)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            item {
                FilterChip(
                    selected = state.tipoFiltroData == TipoFiltroData.ULTIMOS_7_DIAS,
                    onClick = { onTipoFiltroDataChanged(TipoFiltroData.ULTIMOS_7_DIAS) },
                    label = { Text(stringResource(R.string.text_last_seven_days)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            item {
                val isPeriodoActive = state.tipoFiltroData == TipoFiltroData.PERIODO
                val periodoLabel = if (isPeriodoActive && state.dataInicioTimestamp != null && state.dataFimTimestamp != null) {
                    stringResource(
                        R.string.text_from_to_date,
                        Formatters.formatShortDate(state.dataInicioTimestamp),
                        Formatters.formatShortDate(state.dataFimTimestamp)
                    )
                } else {
                    stringResource(R.string.text_period_by_date)
                }

                FilterChip(
                    selected = isPeriodoActive,
                    onClick = { showDateRangePicker = true },
                    leadingIcon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                    label = { Text(periodoLabel) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow {
            item {
                FilterChip(
                    selected = state.tipoFiltroTransacao == TipoFiltroTransacao.TODAS,
                    onClick = { onTipoFiltroTransacaoChanged(TipoFiltroTransacao.TODAS) },
                    label = { Text(stringResource(R.string.text_all_types_transaction)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            item {
                FilterChip(
                    selected = state.tipoFiltroTransacao == TipoFiltroTransacao.
                    ENTRADAS,
                    onClick = { onTipoFiltroTransacaoChanged(TipoFiltroTransacao.ENTRADAS) },
                    label = { Text(stringResource(R.string.text_entries)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            item {
                FilterChip(
                    selected = state.tipoFiltroTransacao == TipoFiltroTransacao.SAIDAS,
                    onClick = { onTipoFiltroTransacaoChanged(TipoFiltroTransacao.SAIDAS) },
                    label = { Text(stringResource(R.string.text_exits)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        LazyRow {
            item {
                FilterChip(
                    selected = state.categoriaFiltroIds.isEmpty(),
                    onClick = { onCategoryFilterChanged(null) },
                    label = { Text(stringResource(R.string.text_all_categories)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = cat.id in state.categoriaFiltroIds,
                    onClick = { onCategoryFilterChanged(cat.id) },
                    label = { Text(cat.name) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.label_sort_by),
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LazyRow {
            item {
                FilterChip(
                    selected = state.ordemTransacao == OrdemTransacao.MAIS_RECENTES,
                    onClick = { onOrdemTransacaoChanged(OrdemTransacao.MAIS_RECENTES) },
                    leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Filled.Sort, contentDescription = null) },
                    label = { Text(stringResource(R.string.most_recent)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            item {
                FilterChip(
                    selected = state.ordemTransacao == OrdemTransacao.MAIS_ANTIGAS,
                    onClick = { onOrdemTransacaoChanged(OrdemTransacao.MAIS_ANTIGAS) },
                    label = { Text(stringResource(R.string.text_older)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            item {
                FilterChip(
                    selected = state.ordemTransacao == OrdemTransacao.MAIOR_VALOR,
                    onClick = { onOrdemTransacaoChanged(OrdemTransacao.MAIOR_VALOR) },
                    label = { Text(stringResource(R.string.highest_value)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
            item {
                FilterChip(
                    selected = state.ordemTransacao == OrdemTransacao.MENOR_VALOR,
                    onClick = { onOrdemTransacaoChanged(OrdemTransacao.MENOR_VALOR) },
                    label = { Text(stringResource(R.string.lowest_value)) },
                    modifier = Modifier.padding(end = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (state.transacoes.isEmpty()) {
            Text(
                text = stringResource(R.string.text_nothing_found_for_the_filters),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.transacoes) { item ->
                    TransactionItem(
                        item = item,
                        onEditClick = onEditTransactionClick,
                        onDeleteClick = { transactionToDelete = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    if (showDateRangePicker) {
        DateRangePickerDialog(
            initialStartTimestamp = state.dataInicioTimestamp,
            initialEndTimestamp = state.dataFimTimestamp,
            onDismiss = { showDateRangePicker = false },
            onConfirm = { start, end ->
                onPeriodoDataChanged(start, end)
                showDateRangePicker = false
            }
        )
    }

    if (transactionToDelete != null) {
        val targetTransaction = transactionToDelete!!
        val categoryName = targetTransaction.transaction.description.ifBlank { targetTransaction.category.name }
        val amountFormatted = Formatters.formatCurrency(targetTransaction.transaction.amount)

        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = {
                Text(
                    text = stringResource(R.string.text_delete_transaction),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.text_question_delete_transaction,
                        categoryName,
                        amountFormatted
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTransactionClick(targetTransaction)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ExpenseRed
                    )
                ) {
                    Text(stringResource(R.string.text_delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { transactionToDelete = null }
                ) {
                    Text(stringResource(R.string.text_cancel))
                }
            }
        )
    }
}
