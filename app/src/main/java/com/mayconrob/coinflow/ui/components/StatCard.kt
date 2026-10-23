package com.mayconrob.coinflow.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mayconrob.coinflow.R
import com.mayconrob.coinflow.common.Formatters
import com.mayconrob.coinflow.ui.theme.DarkCardBorder
import com.mayconrob.coinflow.ui.theme.DarkSurface
import java.math.BigDecimal

@Composable
fun StatCard(
    title: String,
    amount: BigDecimal,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    showIcon: Boolean = false,
    dotColor: Color? = null,
    isValuesVisible: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurface)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        if (showIcon) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        AnimatedContent(
                            targetState = isValuesVisible,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                                    animationSpec = tween(220)
                                )
                            },
                            label = ""
                        ) { visible ->
                            Text(
                                text = if (visible) Formatters.formatCurrency(amount) else stringResource(
                                    R.string.text_hidden_character
                                ),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.semantics {
                                    if (!visible) {
                                        contentDescription =
                                            context.getString(R.string.label_hidden_value)
                                    }
                                }
                            )
                        }
                    }
                }
                if (trailingContent != null) {
                    trailingContent()
                }
            }
        } else {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (dotColor != null) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(dotColor)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                AnimatedContent(
                    targetState = isValuesVisible,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220)) togetherWith fadeOut(
                            animationSpec = tween(
                                220
                            )
                        )
                    },
                    label = ""
                ) { visible ->
                    Text(
                        text = if (visible) Formatters.formatCurrency(amount) else stringResource(
                            R.string.text_hidden_character
                        ),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        ),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.semantics {
                            if (!visible) {
                                contentDescription =
                                    context.getString(R.string.label_hidden_value)
                            }
                        }
                    )
                }
            }
        }
    }
}
