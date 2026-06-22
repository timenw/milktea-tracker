package com.timenw.milkteatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.timenw.milkteatracker.data.model.*
import com.timenw.milkteatracker.ui.components.SugarProgressRing
import com.timenw.milkteatracker.ui.components.EmptyStateView
import com.timenw.milkteatracker.ui.components.SummaryCard
import com.timenw.milkteatracker.ui.theme.MilkTeaDanger
import com.timenw.milkteatracker.ui.theme.MilkTeaWarning
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilkTeaHomeTab(
    summary: DailyMilkTeaSummary,
    targetSugar: Int,
    records: List<MilkTeaRecord>,
    selectedType: MilkTeaType,
    onSelectedTypeChanged: (MilkTeaType) -> Unit,
    onAddDrink: (MilkTeaType, Int) -> Unit,
    onRemoveRecord: (Long) -> Unit
) {
    var showCustomDialog by remember { mutableStateOf(false) }
    var customAmount by remember { mutableStateOf("") }
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🧋", fontSize = 24.sp); Spacer(modifier = Modifier.width(8.dp))
                Text("奶了么", fontWeight = FontWeight.Bold)
            }
        })

        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    SugarProgressRing(progress = summary.sugarProgress / 100f, sugarGrams = summary.totalSugarGrams, targetGrams = targetSugar, size = 150)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "热量 ${summary.totalCaloriesKcal}kcal · 咖啡因 ${summary.totalCaffeineMg}mg",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                if (summary.isOverSugar) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "⚠️ 糖分摄入超标", style = MaterialTheme.typography.labelMedium, color = MilkTeaDanger,
                        modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryCard(title = "总容量", value = "${summary.totalMl}ml", modifier = Modifier.weight(1f), emoji = "🥤")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "总糖分", value = "${summary.totalSugarGrams}g", modifier = Modifier.weight(1f), emoji = "🍬")
                    Spacer(modifier = Modifier.width(8.dp))
                    SummaryCard(title = "杯数", value = "${summary.drinkCount}", modifier = Modifier.weight(1f), emoji = "🧋")
                }
            }
            item { Text("选择奶茶", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item { MilkTeaTypeSelector(selectedType = selectedType, onTypeSelected = { onSelectedTypeChanged(it) }) }
            item { Text("快速添加", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item { QuickAddGrid(teaType = selectedType, onAddDrink = { amount -> onAddDrink(selectedType, amount) }) }
            item {
                OutlinedButton(onClick = { showCustomDialog = true }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp)); Text("自定义杯量")
                }
            }
            item { Text("今日记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            if (records.isEmpty()) {
                item { EmptyStateView(emoji = "🧋", title = "还没有奶茶记录", subtitle = "选择一款奶茶，开始记录吧") }
            } else {
                items(records.reversed(), key = { it.id }) { record ->
                    val teaType = try { MilkTeaType.valueOf(record.teaType) } catch (e: Exception) { MilkTeaType.OTHER }
                    Card(modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = teaType.emoji, fontSize = 20.sp, modifier = Modifier.padding(end = 12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${teaType.displayName} ${record.amountMl}ml", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text(text = "${record.sugarGrams}g糖 · ${record.caloriesKcal}kcal · ${record.caffeineMg}mg咖啡因 · ${formatter.format(Date(record.timestamp))}",
                                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            IconButton(onClick = { onRemoveRecord(record.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "删除", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }

    if (showCustomDialog) {
        AlertDialog(onDismissRequest = { showCustomDialog = false }, title = { Text("自定义杯量") },
            text = {
                Column {
                    Text("奶茶: ${selectedType.displayName} (参考糖 ${selectedType.sugarRange})")
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = customAmount, onValueChange = { customAmount = it.filter { c -> c.isDigit() } }, label = { Text("容量 (ml)") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val ml = customAmount.toIntOrNull()
                    if (ml != null && ml > 0) { onAddDrink(selectedType, ml); customAmount = ""; showCustomDialog = false }
                }) { Text("添加") }
            },
            dismissButton = { TextButton(onClick = { showCustomDialog = false; customAmount = "" }) { Text("取消") } }
        )
    }
}

@Composable
fun MilkTeaTypeSelector(selectedType: MilkTeaType, onTypeSelected: (MilkTeaType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        OutlinedButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Text("${selectedType.emoji} ${selectedType.displayName}  (参考 ${selectedType.sugarRange}糖)")
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(8.dp))
            val groups = listOf(
                listOf(MilkTeaType.CLASSIC_MILK_TEA),
                listOf(MilkTeaType.LEMON_TEA, MilkTeaType.PEACH_TEA, MilkTeaType.MANGO_TEA, MilkTeaType.GRAPE_TEA, MilkTeaType.STRAWBERRY_TEA),
                listOf(MilkTeaType.CHEESE_TEA, MilkTeaType.MATCHA_CHEESE, MilkTeaType.OOLONG_CHEESE),
                listOf(MilkTeaType.BOBA_MILK_TEA, MilkTeaType.COCONUT_JELLY_TEA, MilkTeaType.PUDDING_TEA, MilkTeaType.RED_BEAN_TEA),
                listOf(MilkTeaType.MATCHA_LATTE, MilkTeaType.TARO_LATTE, MilkTeaType.BROWN_SUGAR, MilkTeaType.FRUIT_SMOOTHIE, MilkTeaType.OTHER)
            )
            groups.forEach { group ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    group.forEach { type ->
                        FilterChip(selected = selectedType == type, onClick = { onTypeSelected(type); expanded = false },
                            label = { Text("${type.emoji} ${type.displayName}", style = MaterialTheme.typography.labelSmall) }, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
fun QuickAddGrid(teaType: MilkTeaType, onAddDrink: (Int) -> Unit) {
    val amounts = listOf(300, 400, 500, 700)
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        amounts.chunked(4).forEach { rowAmounts ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowAmounts.forEach { amount ->
                    FilledTonalButton(onClick = { onAddDrink(amount) }, modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = MaterialTheme.shapes.medium) {
                        Text(text = "${amount}ml", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                }
                repeat(4 - rowAmounts.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}
