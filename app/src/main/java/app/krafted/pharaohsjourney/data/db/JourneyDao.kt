package app.krafted.pharaohsjourney.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface JourneyDao {
    @Query("SELECT * FROM journey_progress WHERE id = :id LIMIT 1")
    suspend fun getProgress(id: Int): JourneyProgress?

    @Query("SELECT * FROM journey_progress WHERE id = :id LIMIT 1")
    fun observeProgress(id: Int): Flow<JourneyProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertProgress(progress: JourneyProgress)

    @Query("DELETE FROM journey_progress")
    suspend fun clearProgress()
}
