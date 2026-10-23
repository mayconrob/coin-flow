package com.mayconrob.coinflow.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mayconrob.coinflow.R
import com.mayconrob.coinflow.ui.theme.AccentIndigo
import com.mayconrob.coinflow.ui.theme.DarkCardBorder
import com.mayconrob.coinflow.ui.theme.DarkSurface
import com.mayconrob.coinflow.ui.theme.ExpenseRed
import com.mayconrob.coinflow.ui.theme.IncomeGreen
import com.mayconrob.coinflow.ui.theme.WarningYellow
import com.mayconrob.coinflow.ui.viewmodel.OrcamentoCategoriaUiState
import com.mayconrob.coinflow.common.Formatters
import androidx.core.graphics.toColorInt
import kotlin.math.roundToInt

@Composable
fun CategoryBudgetCard(
    consumption: OrcamentoCategoriaUiState,
    modifier: Modifier = Modifier
) {
    val percentageDouble = consumption.porcentagemConsumida.toDouble()
    val context = LocalContext.current

    val progressColor = when {
        percentageDouble >= 100.0 -> ExpenseRed
        percentageDouble >= 70.0 -> WarningYellow
        else -> IncomeGreen
    }

    val percentageInt = percentageDouble.toInt()
    val visualText = stringResource(R.string.text_percentage_value, percentageInt)

    val accessibilityText = stringResource(
        R.string.cd_text_percentage_value,
        visualText,
        consumption.categoria.name
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
            .semantics(mergeDescendants = true) {},
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    val catColor = try {
                        Color(consumption.categoria.colorHex.toColorInt())
                    } catch (e: Exception) {
                        AccentIndigo
                    }
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(catColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = consumption.categoria.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
                Text(
                    text = stringResource(R.string.text_percentage_value, percentageInt),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = progressColor,
                    modifier = Modifier.semantics {
                        contentDescription =
                            context.getString(
                                R.string.text_ceiling_spending,
                                percentageInt
                            )

                    }
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (percentageDouble / 100.0).coerceIn(0.0, 1.0).toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .semantics {
                        contentDescription = accessibilityText
                    },
                color = progressColor,
                trackColor = DarkCardBorder,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        R.string.label_spent, Formatters.formatCurrency(
                            consumption.totalGasto
                        )
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    stringResource(
                        R.string.label_ceiling,
                        Formatters.formatCurrency(consumption.categoria.budgetLimit)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
