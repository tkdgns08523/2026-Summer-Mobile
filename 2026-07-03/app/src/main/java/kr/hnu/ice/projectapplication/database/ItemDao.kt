package kr.hnu.ice.projectapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.projectapplication.model.Item

@Dao
interface ItemDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<Item>)

    @Update
    suspend fun update(item: Item)

    @Query("SELECT * FROM items WHERE userId = :userId ORDER BY type ASC, price ASC")
    fun observeAll(userId: Long): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE userId = :userId ORDER BY type ASC, price ASC")
    suspend fun getAll(userId: Long): List<Item>

    @Query("SELECT COUNT(*) FROM items WHERE userId = :userId")
    suspend fun count(userId: Long): Int

    @Query("UPDATE items SET isEquipped = 0 WHERE userId = :userId AND type = :type")
    suspend fun unequipType(userId: Long, type: String)

    @Query("DELETE FROM items WHERE userId = :userId")
    suspend fun clear(userId: Long)
}
