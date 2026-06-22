package com.timenw.milkteatracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timenw.milkteatracker.ui.theme.MilkTeaSafe
import com.timenw.milkteatracker.ui.theme.MilkTeaWarning
import com.timenw.milkteatracker.ui.theme.MilkTeaDanger

@Composable
fun SummaryCard(title: String, value: String, modifier: Modifier = Modifier, emoji: String = "🧋") {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = emoji, fontSize = 20.sp); Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun EmptyStateView(emoji: String, title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 48.sp); Spacer(modifier = Modifier.height(12.dp))
        Text(text = title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun SugarProgressRing(progress: Float, sugarGrams: Int, targetGrams: Int, size: Int = 140, strokeWidth: Int = 10) {
    val progressColor = when { progress < 0.5f -> MilkTeaSafe; progress < 0.8f -> MilkTeaWarning; else -> MilkTeaDanger }
    val clampedProgress = progress.coerceIn(0f, 1.5f)
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size.dp)) {
        Canvas(modifier = Modifier.size(size.dp)) {
            val stroke = Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round)
            val radius = (size.dp.toPx() - stroke.width) / 2
            drawCircle(color = Color.Gray.copy(alpha = 0.2f), radius = radius, center = Offset(size.dp.toPx() / 2, size.dp.toPx() / 2), style = stroke)
            drawArc(color = progressColor, startAngle = -90f, sweepAngle = (clampedProgress * 360f).coerceAtMost(360f), useCenter = false,
                topLeft = Offset(size.dp.toPx() / 2 - radius, size.dp.toPx() / 2 - radius), size = Size(radius * 2, radius * 2), style = stroke)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${sugarGrams}g", fontSize = (size / 6).sp, fontWeight = FontWeight.Bold, color = progressColor)
            Text(text = "/ ${targetGrams}g", fontSize = (size / 10).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "糖分", fontSize = (size / 12).sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
