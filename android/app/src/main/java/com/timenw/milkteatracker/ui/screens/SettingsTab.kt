package com.timenw.milkteatracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.timenw.milkteatracker.data.model.UserSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(settings: UserSettings, onSettingsChanged: (UserSettings) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp)); Text("设置", fontWeight = FontWeight.Bold)
            }
        })
        LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item { Text("个人信息", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("体重: ${settings.weightKg.toInt()} kg", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(value = settings.weightKg, onValueChange = { onSettingsChanged(settings.copy(weightKg = it)) }, valueRange = 40f..150f, steps = 109)
                    }
                }
            }
            item { Text("糖分目标", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("每日糖分上限: ${settings.dailySugarTargetGrams}g", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Slider(value = settings.dailySugarTargetGrams.toFloat(), onValueChange = { onSettingsChanged(settings.copy(dailySugarTargetGrams = it.toInt())) }, valueRange = 10f..60f, steps = 9)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("10g", style = MaterialTheme.typography.labelSmall); Text("60g", style = MaterialTheme.typography.labelSmall) }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("💡 WHO建议成人每日添加糖不超过25g", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(12.dp)); HorizontalDivider(); Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("糖分超标提醒", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                                Text(text = if (settings.targetEnabled) "已开启 — 超过目标时提醒" else "已关闭", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(checked = settings.targetEnabled, onCheckedChange = { onSettingsChanged(settings.copy(targetEnabled = it)) })
                        }
                    }
                }
            }
            item { Text("关于", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("奶茶了么", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text("版本 1.0.0", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "一款专为奶茶爱好者设计的饮用统计工具，帮助你了解糖分摄入、享受奶茶文化。", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}
