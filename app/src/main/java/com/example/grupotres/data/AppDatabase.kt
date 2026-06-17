package com.example.grupotres.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Challenge::class, User::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun challengeDao(): ChallengeDao
    abstract fun userDao(): UserDao

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
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                val challengeDao = database.challengeDao()
                                if (challengeDao.getAllChallenges().isEmpty()) {
                                    challengeDao.insertChallenge(Challenge(description = "haz 10 de pecho"))
                                    challengeDao.insertChallenge(Challenge(description = "di un secreto"))
                                    challengeDao.insertChallenge(Challenge(description = "haz 15 sentadillas"))
                                    challengeDao.insertChallenge(Challenge(description = "haz 10 abdominales"))
                                }
                                
                                val userDao = database.userDao()
                                // Insert a default user for testing if needed
                                userDao.insertUser(User("admin@gmail.com", "123456"))
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}