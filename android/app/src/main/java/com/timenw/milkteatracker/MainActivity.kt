package com.timenw.milkteatracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timenw.milkteatracker.data.model.*
import com.timenw.milkteatracker.data.repository.MilkTeaRepository
import com.timenw.milkteatracker.notification.NotificationHelper
import com.timenw.milkteatracker.ui.screens.*
import com.timenw.milkteatracker.ui.theme.MilkTeaTrackerTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    private lateinit var repository: MilkTeaRepository
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = MilkTeaRepository(applicationContext)
        NotificationHelper.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        setContent { MilkTeaTrackerTheme { MainScreen(repository) } }
    }
}

sealed class Screen(val route: String, val label: String, val selectedIcon: @Composable () -> Unit, val unselectedIcon: @Composable () -> Unit) {
    object Home : Screen("home", "奶茶", { Icon(Icons.Filled.LocalCafe, contentDescription = null) }, { Icon(Icons.Outlined.LocalCafe, contentDescription = null) })
    object Stats : Screen("stats", "统计", { Icon(Icons.Filled.BarChart, contentDescription = null) }, { Icon(Icons.Outlined.BarChart, contentDescription = null) })
    object Settings : Screen("settings", "设置", { Icon(Icons.Filled.Settings, contentDescription = null) }, { Icon(Icons.Outlined.Settings, contentDescription = null) })
}

@Composable
fun MainScreen(repository: MilkTeaRepository) {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.Stats, Screen.Settings)
    val context = LocalContext.current
    var selectedTeaType by remember { mutableStateOf(MilkTeaType.BOBA_MILK_TEA) }
    var settings by remember { mutableStateOf(repository.getSettings()) }
    val today = remember { LocalDate.now() }
    var hasNotifiedSugar by remember { mutableStateOf(false) }
    var summary by remember { mutableStateOf(repository.getDailySummary(today)) }
    var records by remember { mutableStateOf(repository.getMilkTeaRecords(today)) }
    var weeklyData by remember { mutableStateOf(repository.getWeeklyData()) }
    var drinkFrequency by remember { mutableStateOf(repository.getDrinkFrequency()) }
    var categoryTotals by remember { mutableStateOf(repository.getCategoryTotals()) }

    fun refreshData() {
        summary = repository.getDailySummary(today); records = repository.getMilkTeaRecords(today)
        weeklyData = repository.getWeeklyData(); drinkFrequency = repository.getDrinkFrequency(); categoryTotals = repository.getCategoryTotals()
    }

    Scaffold(bottomBar = {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            screens.forEach { screen ->
                NavigationBarItem(
                    icon = { if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) screen.selectedIcon() else screen.unselectedIcon() },
                    label = { Text(screen.label) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = { navController.navigate(screen.route) { popUpTo(navController.graph.findStartDestination().id) { saveState = true }; launchSingleTop = true; restoreState = true } }
                )
            }
        }
    }) { innerPadding ->
        NavHost(navController = navController, startDestination = Screen.Home.route, modifier = Modifier.padding(innerPadding)) {
            composable(Screen.Home.route) {
                MilkTeaHomeTab(summary = summary, targetSugar = settings.dailySugarTargetGrams, records = records,
                    selectedType = selectedTeaType, onSelectedTypeChanged = { selectedTeaType = it },
                    onAddDrink = { teaType, amount ->
                        val newRecord = MilkTeaRecord(teaType = teaType.name, amountMl = amount, sugarGrams = teaType.sugarRange.split("-").first().toIntOrNull() ?: 40, caffeineMg = teaType.caffeineMg, caloriesKcal = teaType.caloriesKcal, date = today.toString())
                        repository.addMilkTeaRecord(newRecord)
                        refreshData()
                        if (settings.targetEnabled && !hasNotifiedSugar) {
                            val newSummary = repository.getDailySummary(today)
                            if (newSummary.totalSugarGrams >= settings.dailySugarTargetGrams) {
                                NotificationHelper.sendSugarLimitNotification(context, newSummary.totalSugarGrams, settings.dailySugarTargetGrams)
                                hasNotifiedSugar = true
                            }
                        }
                    },
                    onRemoveRecord = { id -> repository.removeMilkTeaRecord(id, today); refreshData() })
            }
            composable(Screen.Stats.route) { StatsTab(weeklyData = weeklyData, drinkFrequency = drinkFrequency, categoryTotals = categoryTotals) }
            composable(Screen.Settings.route) { SettingsTab(settings = settings, onSettingsChanged = { newSettings -> repository.saveSettings(newSettings); settings = newSettings }) }
        }
    }
}
