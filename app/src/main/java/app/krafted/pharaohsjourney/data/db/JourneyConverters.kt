package app.krafted.pharaohsjourney.data.db

import androidx.room.TypeConverter

class JourneyConverters {
    @TypeConverter
    fun fromCompletedChamberIds(value: List<Int>): String = value.joinToString(",")

    @TypeConverter
    fun toCompletedChamberIds(value: String?): List<Int> {
        if (value.isNullOrBlank()) return emptyList()
        return value.split(",").mapNotNull { it.toIntOrNull() }.distinct().sorted()
    }
}
