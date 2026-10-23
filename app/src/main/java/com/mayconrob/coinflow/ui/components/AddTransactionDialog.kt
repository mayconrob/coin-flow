package com.mayconrob.coinflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mayconrob.coinflow.domain.model.Category
import com.mayconrob.coinflow.domain.model.TransactionWithCategory
import com.mayconrob.coinflow.domain.enums.TransactionType
import com.mayconrob.coinflow.ui.theme.ExpenseRed
import com.mayconrob.coinflow.ui.theme.IncomeGreen
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    categories: List<Category>,
    transactionToEdit: TransactionWithCategory? = null,
    onDismiss: () -> Unit,
    onConfirm: (description: String, amount: BigDecimal, type: TransactionType, categoryId: Long) -> Unit
) {
    val isEditing = transactionToEdit != null

    var description by remember {
        mutableStateOf(
            transactionToEdit?.transaction?.description ?: ""
        )
    }
    var amountText by remember {
        mutableStateOf(
            transactionToEdit?.transaction?.amount?.toPlainString() ?: ""
        )
    }
    var selectedType by remember {
        mutableStateOf<TransactionType?>(
            transactionToEdit?.transaction?.type
        )
    }

    val filteredCategories = if (selectedType != null) categories.filter { it.type == selectedType } else emptyList()
    var selectedCategory by remember(selectedType, categories) {
        mutableStateOf(
            if (isEditing && selectedType == transactionToEdit?.transaction?.type) {
                categories.find { id -> id.id == transactionToEdit?.transaction?.categoryId }
            } else {
                null
            }
        )
    }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Editar Transação" else "Nova Transação",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedType = TransactionType.EXIT
                            selectedCategory = null
                        }
                    ) {
                        RadioButton(
                            selected = selectedType == TransactionType.EXIT,
                            onClick = {
                                selectedType = TransactionType.EXIT
                                selectedCategory = null
                            }
                        )
                        Text("Saída", color = ExpenseRed)
                    }

                    Spacer(modifier = Modifier.padding(horizontal = 12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedType = TransactionType.ENTRY
                            selectedCategory = null
                        }
                    ) {
                        RadioButton(
                            selected = selectedType == TransactionType.ENTRY,
                            onClick = {
                                selectedType = TransactionType.ENTRY
                                selectedCategory = null
                            }
                        )
                        Text("Entrada", color = IncomeGreen)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição (ex: Almoço, Salário)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Valor R$
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor (R$) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Seletor de Categoria com Barra de Rolagem Visível
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded && selectedType != null,
                    onExpandedChange = { if (selectedType != null) dropdownExpanded = !dropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = if (selectedType == null) "Selecione o tipo primeiro" else (selectedCategory?.name ?: "Selecione"),
                        onValueChange = {},
                        readOnly = true,
                        enabled = selectedType != null,
                        label = { Text("Categoria *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    if (selectedType != null) {
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false },
                            modifier = Modifier
                                .heightIn(max = 220.dp)
                                .drawWithContent {
                                    drawContent()
                                    val needDrawScrollbar = scrollState.maxValue > 0
                                    if (needDrawScrollbar) {
                                        val totalHeight = size.height
                                        val progress =
                                            scrollState.value.toFloat() / scrollState.maxValue
                                        val scrollbarHeight =
                                            (totalHeight * (totalHeight / (totalHeight + scrollState.maxValue))).coerceAtLeast(
                                                30f
                                            )
                                        val scrollbarTop = progress * (totalHeight - scrollbarHeight)
                                        drawRect(
                                            color = Color.Gray.copy(alpha = 0.7f),
                                            topLeft = Offset(size.width - 8.dp.toPx(), scrollbarTop),
                                            size = Size(4.dp.toPx(), scrollbarHeight)
                                        )
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .heightIn(max = 220.dp)
                                    .verticalScroll(scrollState)
                            ) {
                                // Primeira linha: "Selecione"
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Selecione",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    },
                                    onClick = {
                                        selectedCategory = null
                                        dropdownExpanded = false
                                    }
                                )

                                if (filteredCategories.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Nenhuma categoria de ${if (selectedType == TransactionType.ENTRY) "Entrada" else "Saída"}") },
                                        onClick = { dropdownExpanded = false }
                                    )
                                } else {
                                    filteredCategories.forEach { cat ->
                                        DropdownMenuItem(
                                            text = { Text(cat.name) },
                                            onClick = {
                                                selectedCategory = cat
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = err,
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cleanAmountText = amountText.replace(',', '.').trim()
                    val amount = try {
                        if (cleanAmountText.isNotEmpty()) BigDecimal(cleanAmountText) else null
                    } catch (e: Exception) {
                        null
                    }
                    val type = selectedType
                    val category = selectedCategory
                    val finalDescription = description.trim()

                    if (type == null) {
                        errorMessage = "Selecione o tipo da transação (Entrada ou Saída)"
                    } else if (cleanAmountText.isEmpty() || amount == null) {
                        errorMessage = "Informe o valor da transação"
                    } else if (amount <= BigDecimal.ZERO) {
                        errorMessage = "O valor deve ser maior que zero"
                    } else if (category == null) {
                        errorMessage = "Selecione uma categoria válida"
                    } else {
                        onConfirm(finalDescription, amount, type, category.id)
                        onDismiss()
                    }
                }
            ) {
                Text(if (isEditing) "Salvar Alterações" else "Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
