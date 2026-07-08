package kr.hnu.ice.projectapplication.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kr.hnu.ice.projectapplication.model.DrinkRecord
import kr.hnu.ice.projectapplication.model.Item
import kr.hnu.ice.projectapplication.model.User
import kr.hnu.ice.projectapplication.model.WaterCharacter

@Database(
    entities = [User::class, DrinkRecord::class, WaterCharacter::class, Item::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun drinkRecordDao(): DrinkRecordDao
    abstract fun characterDao(): CharacterDao
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "waterbuddy.db"
                ).fallbackToDestructiveMigration(true).build().also { INSTANCE = it }
            }
        }
    }
}
