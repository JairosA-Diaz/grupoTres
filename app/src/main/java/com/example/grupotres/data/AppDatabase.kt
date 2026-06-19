package com.example.grupotres.data
// Define el paquete donde reside la configuración de la base de datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
// Importa las librerías de Room para la BD y Corrutinas para procesos en segundo plano

@Database(entities = [Challenge::class, User::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    // Clase abstracta que sirve como punto de acceso principal a la base de datos SQLite

    abstract fun challengeDao(): ChallengeDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        // Mantiene una única instancia de la base de datos (Singleton) para evitar fugas de memoria

        fun getDatabase(context: Context): AppDatabase {
            // Función para obtener la base de datos; la crea si no existe
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    // Callback que se ejecuta cuando la base de datos se abre
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        INSTANCE?.let { database ->
                            // Usa corrutinas para insertar datos iniciales sin bloquear la pantalla
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
