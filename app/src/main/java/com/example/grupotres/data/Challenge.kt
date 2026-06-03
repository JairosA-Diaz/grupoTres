package com.example.grupotres.data
// Define el paquete de la capa de datos

import androidx.room.Entity
import androidx.room.PrimaryKey
// Importa las anotaciones de Room para definir tablas y llaves primarias

@Entity(tableName = "challenges")
// Define que esta clase será una tabla en la base de datos llamada "challenges"
data class Challenge(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0, 
    // Llave primaria única que se genera automáticamente (1, 2, 3...)

    val description: String
    // Columna que almacena la descripción del reto o desafío
)
