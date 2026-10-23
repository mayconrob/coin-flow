package com.mayconrob.coinflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mayconrob.coinflow.R
import com.mayconrob.coinflow.ui.theme.ExpenseRed
import com.mayconrob.coinflow.common.Formatters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    initialStartTimestamp: Long? = null,
    initialEndTimestamp: Long? = null,
    onDismiss: () -> Unit,
    onConfirm: (startTimestamp: Long, endTimestamp: Long) -> Unit
) {
    val defaultUtcToday = remember {
        Formatters.localTimestampToUtcMidnight(System.currentTimeMillis())
    }

    var selectedStartUtcMillis by remember {
        mutableStateOf(
            initialStartTimestamp?.let { Formatters.localTimestampToUtcMidnight(it) } ?: defaultUtcToday
        )
    }
    var selectedEndUtcMillis by remember {
        mutableStateOf(
            initialEndTimestamp?.let { Formatters.localTimestampToUtcMidnight(it) } ?: defaultUtcToday
        )
    }

    var showStartCalendarPicker by remember { mutableStateOf(false) }
    var showEndCalendarPicker by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.filter_by_period),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.text_select_date_begin_end),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Campo de Escolha da Data Início (Somente Seleção por Clique)
                OutlinedTextField(
                    value = Formatters.formatShortDateUtc(selectedStartUtcMillis),
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text(stringResource(R.string.begin_date)) },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showStartCalendarPicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo de Escolha da Data Fim (Somente Seleção por Clique)
                OutlinedTextField(
                    value = Formatters.formatShortDateUtc(selectedEndUtcMillis),
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text(stringResource(R.string.end_date)) },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.CalendarToday, contentDescription = stringResource(
                            R.string.select_end_date
                        ))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showEndCalendarPicker = true },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.primary
                    )
                )

                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = err, color = ExpenseRed, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedStartUtcMillis > selectedEndUtcMillis) {
                        errorMessage = context.getString(R.string.message_error_date_range)
                    } else {
                        val startOfDay = Formatters.utcMidnightToLocalStartOfDay(selectedStartUtcMillis)
                        val endOfDay = Formatters.utcMidnightToLocalEndOfDay(selectedEndUtcMillis)
                        onConfirm(startOfDay, endOfDay)
                        onDismiss()
                    }
                }
            ) {
                Text(stringResource(R.string.apply_filters))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.text_cancel))
            }
        }
    )

    // Modal do Calendário Interativo para Data Início
    if (showStartCalendarPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedStartUtcMillis
        )
        DatePickerDialog(
            onDismissRequest = { showStartCalendarPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedStartUtcMillis = it
                            errorMessage = null
                        }
                        showStartCalendarPicker = false
                    }
                ) {
                    Text(stringResource(R.string.text_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartCalendarPicker = false }) {
                    Text(stringResource(R.string.text_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Modal do Calendário Interativo para Data Fim
    if (showEndCalendarPicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedEndUtcMillis
        )
        DatePickerDialog(
            onDismissRequest = { showEndCalendarPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedEndUtcMillis = it
                            errorMessage = null
                        }
                        showEndCalendarPicker = false
                    }
                ) {
                    Text(stringResource(R.string.text_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndCalendarPicker = false }) {
                    Text(stringResource(R.string.text_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
