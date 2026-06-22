package com.timenw.milkteatracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timenw.milkteatracker.data.model.DailyMilkTeaSummary
import com.timenw.milkteatracker.data.model.MilkTeaType
import com.timenw.milkteatracker.data.repository.MilkTeaRepository
import com.timenw.milkteatracker.ui.components.SummaryCard
import com.timenw.milkteatracker.ui.theme.MilkTeaSafe
import com.timenw.milkteatracker.ui.theme.MilkTeaDanger
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsTab(weeklyData: List<DailyMilkTeaSummary>, drinkFrequency: Map<String, Int>, categoryTotals: List<MilkTeaRepository.CategoryTotal>) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.BarChart, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp)); Text("数据统计", fontWeight = FontWeight.Bold)
            }
        })
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                val totalSugar = weeklyData.sumOf { it.totalSugarGrams }
                val totalCal = weeklyData.sumOf { it.totalCaloriesKcal }
                val totalDrinks = weeklyData.sumOf { it.drinkCount }
                val avgDaily = if (weeklyData.isNotEmpty()) totalSugar / 7 else 0
                val overDays = weeklyData.count { it.isOverSugar }
                Text("本周总览", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "总糖分", value = "${totalSugar}g", modifier = Modifier.weight(1f), emoji = "🍬")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "总热量", value = "${totalCal}kcal", modifier = Modifier.weight(1f), emoji = "🔥")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "日均糖分", value = "${avgDaily}g", modifier = Modifier.weight(1f), emoji = "📊")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "超标天数", value = "${overDays}天", modifier = Modifier.weight(1f), emoji = "⚠️")
                }
            }
            item {
                Text("本周饮用趋势", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (weeklyData.all { it.totalSugarGrams == 0 }) {
                            Text(text = "暂无数据，开始记录奶茶吧 🧋", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 24.dp))
                        } else { SugarBarChart(weeklyData) }
                    }
                }
            }
            item {
                Text("各类奶茶总饮用统计", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "统计时间：从第一次饮用记录到最后一次", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (categoryTotals.isEmpty()) {
                item { Card(modifier = Modifier.fillMaxWidth()) { Text(text = "还没有奶茶记录，开始记录吧 🧋", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(24.dp)) }
            } else {
                items(categoryTotals.size) { index ->
                    val ct = categoryTotals[index]
                    val teaType = try { MilkTeaType.valueOf(ct.teaType) } catch (e: Exception) { MilkTeaType.OTHER }
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = teaType.emoji, style = MaterialTheme.typography.headlineSmall)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = teaType.displayName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) { Text("总容量", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("${ct.totalMl}ml", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium) }
                                Column(modifier = Modifier.weight(1f)) { Text("总糖分", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("${ct.totalSugar}g", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium) }
                                Column(modifier = Modifier.weight(1f)) { Text("总热量", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text("${ct.totalCalories}kcal", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium) }
                            }
                            Spacer(modifier = Modifier.height(8.dp)); HorizontalDivider(); Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.weight(1f)) { Text("首次饮用", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(text = formatDateShort(ct.firstDate), style = MaterialTheme.typography.bodyMedium) }
                                Column(modifier = Modifier.weight(1f)) { Text("最近饮用", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(text = formatDateShort(ct.lastDate), style = MaterialTheme.typography.bodyMedium) }
                                Column(modifier = Modifier.weight(1f)) { Text("时间跨度", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(text = if (ct.daysSpan == 0) "首次" else "${ct.daysSpan}天", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary) }
                            }
                        }
                    }
                }
            }
            item {
                Text("奶茶偏好（近30天）", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (drinkFrequency.isEmpty()) { Text(text = "还没有奶茶记录", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(vertical = 16.dp)) }
                        else { drinkFrequency.entries.sortedByDescending { it.value }.take(5).forEach { (typeName, count) ->
                            val teaType = try { MilkTeaType.valueOf(typeName) } catch (e: Exception) { null }
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "${teaType?.emoji ?: "🧋"} ${teaType?.displayName ?: typeName}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "${count}次", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.primary)
                            }
                        }}
                    }
                }
            }
            item {
                Text("健康提示", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f))) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("🧋 健康饮茶建议：", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("• 成人每日添加糖建议不超过 25g", style = MaterialTheme.typography.bodySmall)
                        Text("• 一杯奶茶热量约 300-500kcal，相当于一顿饭", style = MaterialTheme.typography.bodySmall)
                        Text("• 建议选择少糖或无糖选项", style = MaterialTheme.typography.bodySmall)
                        Text("• 避免睡前饮用，咖啡因可能影响睡眠", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💡 经典奶茶≈40g糖 · 果茶≈30g糖 · 芝士奶盖≈25g糖", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

private fun formatDateShort(dateStr: String): String = try { val parts = dateStr.split("-"); if (parts.size == 3) "${parts[1]}/${parts[2]}" else dateStr } catch (e: Exception) { dateStr }

@Composable
fun SugarBarChart(data: List<DailyMilkTeaSummary>) {
    val maxSugar = data.maxOfOrNull { it.totalSugarGrams } ?: 50
    val dayFormatter = SimpleDateFormat("E", Locale.getDefault())
    Row(modifier = Modifier.fillMaxWidth().height(160.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom) {
        data.forEach { summary ->
            val barHeight = (summary.totalSugarGrams.toFloat() / maxSugar.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
            val isOver = summary.isOverSugar
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Bottom, modifier = Modifier.weight(1f)) {
                Text(text = "${summary.totalSugarGrams}g", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Canvas(modifier = Modifier.fillMaxWidth(0.6f).height(100.dp)) {
                    val barWidth = size.width; val barH = size.height * barHeight
                    drawRect(color = if (isOver) MilkTeaDanger else MilkTeaSafe, topLeft = Offset(0f, size.height - barH), size = androidx.compose.ui.geometry.Size(barWidth, barH))
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = try { dayFormatter.format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(summary.date) ?: Date()) } catch (e: Exception) { summary.date.takeLast(2) }, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
