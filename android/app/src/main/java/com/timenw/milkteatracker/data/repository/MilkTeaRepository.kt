package com.timenw.milkteatracker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.timenw.milkteatracker.data.model.*
import java.time.LocalDate

class MilkTeaRepository(private val context: Context) {
    private val prefs = context.getSharedPreferences("milktea_tracker", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getMilkTeaRecords(date: LocalDate = LocalDate.now()): List<MilkTeaRecord> {
        val key = "milkteas_${date}"
        val json = prefs.getString(key, "[]") ?: "[]"
        val type = object : TypeToken<List<MilkTeaRecord>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun addMilkTeaRecord(record: MilkTeaRecord) {
        val records = getMilkTeaRecords(LocalDate.parse(record.date)).toMutableList()
        records.add(record.copy(sugarGrams = record.calcSugar(), caloriesKcal = record.calcCalories()))
        saveMilkTeaRecords(records, LocalDate.parse(record.date))
    }

    fun removeMilkTeaRecord(id: Long, date: LocalDate = LocalDate.now()) {
        val records = getMilkTeaRecords(date).toMutableList()
        records.removeAll { it.id == id }
        saveMilkTeaRecords(records, date)
    }

    private fun saveMilkTeaRecords(records: List<MilkTeaRecord>, date: LocalDate) {
        prefs.edit().putString("milkteas_${date}", gson.toJson(records)).apply()
    }

    fun getDailySummary(date: LocalDate = LocalDate.now()): DailyMilkTeaSummary {
        val records = getMilkTeaRecords(date)
        if (records.isEmpty()) return DailyMilkTeaSummary(date = date.toString())
        return DailyMilkTeaSummary(
            date = date.toString(), totalMl = records.sumOf { it.amountMl },
            totalSugarGrams = records.sumOf { it.sugarGrams }, totalCaffeineMg = records.sumOf { it.caffeineMg },
            totalCaloriesKcal = records.sumOf { it.caloriesKcal }, drinkCount = records.size, records = records
        )
    }

    fun getWeeklyData(): List<DailyMilkTeaSummary> {
        val today = LocalDate.now()
        return (0..6).map { daysAgo -> getDailySummary(today.minusDays(daysAgo.toLong())) }.reversed()
    }

    fun getDrinkFrequency(days: Int = 30): Map<String, Int> {
        val today = LocalDate.now()
        val freq = mutableMapOf<String, Int>()
        for (i in 0 until days) {
            getMilkTeaRecords(today.minusDays(i.toLong())).forEach { record ->
                freq[record.teaType] = (freq[record.teaType] ?: 0) + 1
            }
        }
        return freq
    }

    data class CategoryTotal(val teaType: String, val totalMl: Int, val totalSugar: Int, val totalCalories: Int, val drinkCount: Int, val firstDate: String, val lastDate: String, val daysSpan: Int)

    fun getCategoryTotals(): List<CategoryTotal> {
        val today = LocalDate.now()
        val earliestDate = today.minusDays(365)
        val categoryMap = mutableMapOf<String, MutableList<MilkTeaRecord>>()
        var date = earliestDate
        while (!date.isAfter(today)) {
            getMilkTeaRecords(date).forEach { record -> categoryMap.getOrPut(record.teaType) { mutableListOf() }.add(record) }
            date = date.plusDays(1)
        }
        return categoryMap.map { (typeName, records) ->
            val dates = records.map { it.date }.sorted()
            val daysSpan = try { java.time.temporal.ChronoUnit.DAYS.between(LocalDate.parse(dates.first()), LocalDate.parse(dates.last())).toInt() } catch (e: Exception) { 0 }
            CategoryTotal(typeName, records.sumOf { it.amountMl }, records.sumOf { it.sugarGrams }, records.sumOf { it.caloriesKcal }, records.size, dates.firstOrNull() ?: today.toString(), dates.lastOrNull() ?: today.toString(), daysSpan)
        }.sortedByDescending { it.totalMl }
    }

    fun getSettings(): UserSettings {
        val json = prefs.getString("settings", null)
        return if (json != null) gson.fromJson(json, UserSettings::class.java) else UserSettings()
    }

    fun saveSettings(settings: UserSettings) { prefs.edit().putString("settings", gson.toJson(settings)).apply() }
}
