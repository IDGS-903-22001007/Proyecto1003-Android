package com.example.proye_1003.recordatorios.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Aumenta la versión si agregas más tablas en el futuro
@Database(
    entities = [RecordatorioEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recordatorioDao(): RecordatorioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database.db"
                )
                    .fallbackToDestructiveMigration() // evitar crashes si cambias estructura
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
