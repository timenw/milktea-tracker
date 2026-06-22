package com.timenw.milkteatracker.data.model

import java.time.LocalDate

enum class MilkTeaType(
    val displayName: String, val emoji: String, val category: MilkTeaCategory,
    val sugarRange: String, val caffeineMg: Int, val caloriesKcal: Int, val standardMl: Int
) {
    // ===== 经典奶茶 =====
    CLASSIC_MILK_TEA("经典奶茶", "🧋", MilkTeaCategory.CLASSIC, "30-60g", 40, 450, 500),
    HONG_KONG_MILK_TEA("港式奶茶", "🇭🇰", MilkTeaCategory.CLASSIC, "20-40g", 60, 350, 400),
    TAIWANESE_MILK_TEA("台式奶茶", "🇹🇼", MilkTeaCategory.CLASSIC, "40-70g", 35, 500, 500),
    BRITISH_MILK_TEA("英式奶茶", "🇬🇧", MilkTeaCategory.CLASSIC, "15-30g", 50, 200, 350),

    // ===== 果茶 =====
    LEMON_TEA("柠檬茶", "🍋", MilkTeaCategory.FRUIT, "20-50g", 20, 250, 500),
    PEACH_TEA("蜜桃茶", "🍑", MilkTeaCategory.FRUIT, "30-60g", 15, 300, 500),
    MANGO_TEA("芒果茶", "🥭", MilkTeaCategory.FRUIT, "35-65g", 10, 350, 500),
    GRAPE_TEA("葡萄茶", "🍇", MilkTeaCategory.FRUIT, "30-55g", 10, 280, 500),
    STRAWBERRY_TEA("草莓茶", "🍓", MilkTeaCategory.FRUIT, "25-50g", 10, 260, 500),

    // ===== 芝士/奶盖 =====
    CHEESE_TEA("芝士奶盖茶", "🧀", MilkTeaCategory.CHEESE, "20-40g", 30, 400, 450),
    MATCHA_CHEESE("抹茶芝士奶盖", "🍵", MilkTeaCategory.CHEESE, "25-45g", 45, 380, 450),
    OOLONG_CHEESE("乌龙芝士奶盖", "🫖", MilkTeaCategory.CHEESE, "20-35g", 35, 350, 450),

    // ===== 珍珠/配料 =====
    BOBA_MILK_TEA("珍珠奶茶", "⚫", MilkTeaCategory.BOBA, "40-70g", 35, 550, 500),
    COCONUT_JELLY_TEA("椰果奶茶", "🥥", MilkTeaCategory.BOBA, "35-60g", 30, 480, 500),
    PUDDING_TEA("布丁奶茶", "🍮", MilkTeaCategory.BOBA, "30-55g", 30, 500, 500),
    RED_BEAN_TEA("红豆奶茶", "🫘", MilkTeaCategory.BOBA, "35-60g", 25, 480, 500),

    // ===== 冰沙/特调 =====
    MATCHA_LATTE("抹茶拿铁", "🍵", MilkTeaCategory.SPECIALTY, "20-40g", 50, 300, 400),
    TARO_LATTE("芋泥拿铁", "🟣", MilkTeaCategory.SPECIALTY, "25-45g", 20, 380, 450),
    BROWN_SUGAR("黑糖珍珠鲜奶", "🟤", MilkTeaCategory.SPECIALTY, "30-50g", 25, 420, 500),
    FRUIT_SMOOTHIE("水果冰沙", "🥤", MilkTeaCategory.SPECIALTY, "15-35g", 0, 200, 500),
    OTHER("其他", "🧋", MilkTeaCategory.SPECIALTY, "0-100g", 0, 300, 500);

    companion object {
        fun getByCategory(category: MilkTeaCategory): List<MilkTeaType> = values().filter { it.category == category }
    }
}

enum class MilkTeaCategory(val displayName: String, val emoji: String) {
    CLASSIC("经典奶茶", "🧋"), FRUIT("果茶", "🍋"), CHEESE("芝士奶盖", "🧀"),
    BOBA("珍珠配料", "⚫"), SPECIALTY("特调", "🍵")
}

data class MilkTeaRecord(
    val id: Long = System.currentTimeMillis(),
    val teaType: String = MilkTeaType.BOBA_MILK_TEA.name,
    val amountMl: Int = 500,
    val sugarGrams: Int = 40,
    val caffeineMg: Int = 35,
    val caloriesKcal: Int = 550,
    val timestamp: Long = System.currentTimeMillis(),
    val date: String = LocalDate.now().toString(),
    val note: String = "",
    val scene: MilkTeaScene = MilkTeaScene.OTHER,
    val brand: String = ""
) {
    fun calcSugar(): Int = sugarGrams
    fun calcCalories(): Int = caloriesKcal
}

enum class MilkTeaScene(val displayName: String, val emoji: String) {
    WORK("工作", "💼"), STUDY("学习", "📚"), SOCIAL("社交", "👥"),
    AFTERNOON("下午茶", "☀️"), EVENING("晚间", "🌙"),
    CAFE("奶茶店", "🏠"), TAKEOUT("外卖", "📦"), OTHER("其他", "📍")
}

data class UserSettings(
    val dailySugarTargetGrams: Int = 25,
    val dailyCaloriesTarget: Int = 200,
    val targetEnabled: Boolean = true,
    val weightKg: Float = 70f
)

data class DailyMilkTeaSummary(
    val date: String = LocalDate.now().toString(),
    val totalMl: Int = 0,
    val totalSugarGrams: Int = 0,
    val totalCaffeineMg: Int = 0,
    val totalCaloriesKcal: Int = 0,
    val drinkCount: Int = 0,
    val records: List<MilkTeaRecord> = emptyList()
) {
    val isOverSugar: Boolean get() = totalSugarGrams > 25
    val isOverCalories: Boolean get() = totalCaloriesKcal > 200
    val sugarProgress: Float get() = (totalSugarGrams / 25f * 100).coerceIn(0f, 150f)
    val caloriesProgress: Float get() = (totalCaloriesKcal / 200f * 100).coerceIn(0f, 150f)
}
