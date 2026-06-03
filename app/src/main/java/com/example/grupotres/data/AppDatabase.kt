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

@Database(entities = [Challenge::class], version = 1)
// Define la base de datos, las tablas que incluye (Challenge) y su versión
abstract class AppDatabase : RoomDatabase() {
    // Clase abstracta que sirve como punto de acceso principal a la base de datos SQLite

    abstract fun challengeDao(): ChallengeDao
    // Define el DAO para que Room pueda generar la implementación de las consultas

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
                .addCallback(object : RoomDatabase.Callback() {
                    // Callback que se ejecuta cuando la base de datos se abre
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        INSTANCE?.let { database ->
                            // Usa corrutinas para insertar datos iniciales sin bloquear la pantalla
                            CoroutineScope(Dispatchers.IO).launch {
                                val dao = database.challengeDao()
                                // Si la lista de retos está vacía, inserta unos por defecto (Pre-poblado)
                                if (dao.getAllChallenges().isEmpty()) {
                                    dao.insertChallenge(Challenge(description = "haz 10 de pecho"))
                                    dao.insertChallenge(Challenge(description = "di un secreto"))
                                    dao.insertChallenge(Challenge(description = "haz 15 sentadillas"))
                                    dao.insertChallenge(Challenge(description = "haz 10 abdominales"))
                                }
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
