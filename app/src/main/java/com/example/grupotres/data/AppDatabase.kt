package com.example.grupotres.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Challenge::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addCallback(DatabaseCallback())
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val dao = database.challengeDao()
                        dao.insertChallenge(Challenge(description = "haz 10 de pecho"))
                        dao.insertChallenge(Challenge(description = "di un secreto"))
                        dao.insertChallenge(Challenge(description = "haz 15 sentadillas"))
                        dao.insertChallenge(Challenge(description = "haz 10 abdominales"))
                    }
                }
            }
        }
    }
}