package com.mayconrob.coinflow.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mayconrob.coinflow.ui.components.CategoryBudgetCard
import com.mayconrob.coinflow.ui.components.StatCard
import com.mayconrob.coinflow.ui.components.TransactionItem
import com.mayconrob.coinflow.ui.theme.AccentIndigo
import com.mayconrob.coinflow.ui.theme.ExpenseRed
import com.mayconrob.coinflow.ui.theme.IncomeGreen
import com.mayconrob.coinflow.ui.viewmodel.PainelFinanceiroUiState

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.mayconrob.coinflow.R
import com.mayconrob.coinflow.ui.components.MonthYearPickerDialog

@Composable
fun DashboardScreen(
    state: PainelFinanceiroUiState,
    onAddTransactionClick: () -> Unit,
    onToggleVisibility: () -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthYearSelected: (year: Int, month: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showMonthYearPicker by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransactionClick,
                containerColor = AccentIndigo,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.cd_text_add_transaction)
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Cabeçalho com Seletor Mês/Ano
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    // Seletor Mês/Ano com Navegação
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),

                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            IconButton(
                                onClick = onPreviousMonth,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = stringResource(R.string.cd_description_previous_month),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = state.currentMonthLabel,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier
                                    .clickable { showMonthYearPicker = true }
                                    .padding(horizontal = 4.dp)
                            )
                            IconButton(
                                onClick = onNextMonth,
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = stringResource(R.string.cd_description_next_month),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card Saldo Atual
                StatCard(
                    title = stringResource(R.string.description_total_balance),
                    amount = state.resumo.currentBalance,
                    icon = Icons.Default.AccountBalanceWallet,
                    iconTint = AccentIndigo,
                    showIcon = true,
                    isValuesVisible = state.isValuesVisible,
                    trailingContent = {
                        IconButton(
                            onClick = onToggleVisibility,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (state.isValuesVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (state.isValuesVisible) stringResource(R.string.cd_option_hide_values) else
                                    stringResource(R.string.cd_option_show_values),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    modifier = Modifier.semantics(mergeDescendants = true) {}
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Cards Lado a Lado: Entradas vs Saídas
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatCard(
                        title = stringResource(R.string.text_entries),
                        amount = state.resumo.totalIncome,
                        icon = Icons.Default.ArrowUpward,
                        iconTint = IncomeGreen,
                        dotColor = IncomeGreen,
                        isValuesVisible = state.isValuesVisible,
                        modifier = Modifier
                            .weight(1f)
                            .semantics(mergeDescendants = true) {}
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    StatCard(
                        title = stringResource(R.string.text_exits),
                        amount = state.resumo.totalExpenses,
                        icon = Icons.Default.ArrowDownward,
                        iconTint = ExpenseRed,
                        dotColor = ExpenseRed,
                        isValuesVisible = state.isValuesVisible,
                        modifier = Modifier
                            .weight(1f)
                            .semantics(mergeDescendants = true) {}
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Seção Orçamentos por Categoria
                if (state.consumosOrcamento.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics(mergeDescendants = true) {},
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = stringResource(R.string.title_budget_caps),
                            style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp),
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.semantics {
                                contentDescription =
                                    context.getString(R.string.title_budget_caps)
                            }
                        )
                        Text(
                            text = state.currentMonthLabel,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            items(state.consumosOrcamento) { consumption ->
                CategoryBudgetCard(consumption = consumption)
                Spacer(modifier = Modifier.height(10.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.title_latest_transactions),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 18.sp),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.ultimasTransacoes.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.text_no_transactions_recorded),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier
                            .padding(vertical = 16.dp)
                            .semantics {
                                contentDescription =
                                    context.getString(R.string.text_no_transactions_recorded)
                            }
                    )
                }
            } else {
                items(state.ultimasTransacoes) { item ->
                    TransactionItem(
                        item = item
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        if (showMonthYearPicker) {
            MonthYearPickerDialog(
                initialYear = state.selectedYear,
                initialMonth = state.selectedMonth,
                onDismiss = { showMonthYearPicker = false },
                onConfirm = { year, month ->
                    onMonthYearSelected(year, month)
                    showMonthYearPicker = false
                }
            )
        }
    }
}
