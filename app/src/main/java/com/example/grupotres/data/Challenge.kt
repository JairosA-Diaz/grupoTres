package com.example.grupotres.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "challenges")
data class Challenge(
    @PrimaryKey(autoGenerate = true) 
    val idRoom: Int = 0, 
    val id: String = "", // Para Firestore
    val description: String = ""
)
