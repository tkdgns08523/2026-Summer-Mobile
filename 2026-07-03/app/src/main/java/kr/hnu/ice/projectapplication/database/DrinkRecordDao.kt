package kr.hnu.ice.projectapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.projectapplication.model.DrinkRecord

@Dao
interface DrinkRecordDao {

    @Insert
    suspend fun insert(record: DrinkRecord): Long

    @Update
    suspend fun update(record: DrinkRecord)

    @Delete
    suspend fun delete(record: DrinkRecord)

    /** 특정 계정의 특정 날짜 기록을 최신순으로 관찰 */
    @Query("SELECT * FROM drink_records WHERE userId = :userId AND date = :date ORDER BY timestamp DESC")
    fun observeByDate(userId: Long, date: String): Flow<List<DrinkRecord>>

    /** 특정 계정의 특정 날짜 총 섭취량(ml) */
    @Query("SELECT COALESCE(SUM(amount), 0) FROM drink_records WHERE userId = :userId AND date = :date")
    fun observeTotalByDate(userId: Long, date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM drink_records WHERE userId = :userId AND date = :date")
    suspend fun getTotalByDate(userId: Long, date: String): Int

    /** 계정별 일별 총량 (통계용, 날짜 오름차순) */
    @Query(
        "SELECT date AS date, SUM(amount) AS total FROM drink_records " +
            "WHERE userId = :userId GROUP BY date ORDER BY date ASC"
    )
    suspend fun getDailyTotals(userId: Long): List<DailyTotal>

    @Query("DELETE FROM drink_records WHERE userId = :userId")
    suspend fun clear(userId: Long)
}

/** 일별 집계 결과 */
data class DailyTotal(
    val date: String,
    val total: Int
)
