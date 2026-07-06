package kr.hnu.ice.projectapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.projectapplication.model.WaterCharacter

@Dao
interface CharacterDao {

    @Insert
    suspend fun insert(character: WaterCharacter): Long

    @Update
    suspend fun update(character: WaterCharacter)

    @Query("SELECT * FROM characters WHERE userId = :userId LIMIT 1")
    fun observeByUser(userId: Long): Flow<WaterCharacter?>

    @Query("SELECT * FROM characters WHERE userId = :userId LIMIT 1")
    suspend fun getByUser(userId: Long): WaterCharacter?

    @Query("DELETE FROM characters WHERE userId = :userId")
    suspend fun clear(userId: Long)
}
