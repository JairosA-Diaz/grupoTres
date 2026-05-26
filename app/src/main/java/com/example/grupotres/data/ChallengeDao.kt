package com.example.grupotres.data

import androidx.room.*

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM challenges")
    suspend fun getAllChallenges(): List<Challenge>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChallenge(challenge: Challenge)

    @Update
    suspend fun updateChallenge(challenge: Challenge)

    @Delete
    suspend fun deleteChallenge(challenge: Challenge)

    @Query("SELECT * FROM challenges ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomChallenge(): Challenge?
}