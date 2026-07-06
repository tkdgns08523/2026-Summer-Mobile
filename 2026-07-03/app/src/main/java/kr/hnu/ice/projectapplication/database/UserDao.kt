package kr.hnu.ice.projectapplication.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kr.hnu.ice.projectapplication.model.User

@Dao
interface UserDao {

    /** 새 계정 생성. 생성된 id를 반환한다. */
    @Insert
    suspend fun insert(user: User): Long

    @Update
    suspend fun update(user: User)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: Long): User?

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeById(userId: Long): Flow<User?>

    @Query("SELECT * FROM users WHERE nickname = :nickname COLLATE NOCASE LIMIT 1")
    suspend fun findByNickname(nickname: String): User?

    @Query("SELECT * FROM users ORDER BY nickname ASC")
    suspend fun getAll(): List<User>

    @Query("UPDATE users SET dailyGoal = :goal WHERE id = :userId")
    suspend fun updateDailyGoal(userId: Long, goal: Int)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun delete(userId: Long)
}
