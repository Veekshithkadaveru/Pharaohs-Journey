package app.krafted.pharaohsjourney.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

const val JOURNEY_PROGRESS_ID = 1

@Entity(tableName = "journey_progress")
data class JourneyProgress(
    @PrimaryKey val id: Int = JOURNEY_PROGRESS_ID,
    val currentChamberId: Int = 1,
    val completedChamberIds: List<Int> = emptyList(),
    val updatedAtMillis: Long = System.currentTimeMillis()
)
