package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        KycDocumentEntity::class,
        TripEntity::class,
        MessageEntity::class,
        RatingEntity::class,
        FareConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class YatraDatabase : RoomDatabase() {
    abstract fun yatraDao(): YatraDao

    companion object {
        @Volatile
        private var INSTANCE: YatraDatabase? = null

        fun getDatabase(context: Context): YatraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    YatraDatabase::class.java,
                    "yatrax_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
