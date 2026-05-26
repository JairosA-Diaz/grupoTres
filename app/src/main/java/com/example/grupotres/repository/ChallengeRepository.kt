package com.example.grupotres.repository

import com.example.grupotres.data.Challenge
import com.example.grupotres.data.ChallengeDao

class ChallengeRepository(private val challengeDao: ChallengeDao) {

    suspend fun getAllChallenges(): List<Challenge> {
        return challengeDao.getAllChallenges()
    }

    suspend fun insertChallenge(challenge: Challenge) {
        challengeDao.insertChallenge(challenge)
    }

    suspend fun updateChallenge(challenge: Challenge) {
        challengeDao.updateChallenge(challenge)
    }

    suspend fun deleteChallenge(challenge: Challenge) {
        challengeDao.deleteChallenge(challenge)
    }

    suspend fun getRandomChallenge(): Challenge? {
        return challengeDao.getRandomChallenge()
    }
}