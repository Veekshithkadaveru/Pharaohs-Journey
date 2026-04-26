package app.krafted.pharaohsjourney.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Insert
    suspend fun insertScore(scoreRecord: ScoreRecord): Long

    @Query("SELECT * FROM score_records ORDER BY score DESC, completedAtMillis ASC LIMIT :limit")
    fun observeTopScores(limit: Int): Flow<List<ScoreRecord>>

    @Query("SELECT * FROM score_records ORDER BY score DESC, completedAtMillis ASC LIMIT :limit")
    suspend fun getTopScores(limit: Int): List<ScoreRecord>

    @Query("SELECT MAX(score) FROM score_records")
    suspend fun getBestScore(): Int?

    @Query("DELETE FROM score_records")
    suspend fun clearScores()
}
