package com.example.grupotres.repository

import com.example.grupotres.data.Challenge
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChallengeRepository {
    private val db = FirebaseFirestore.getInstance()
    private val challengesCollection = db.collection("challenges")

    suspend fun getAllChallenges(): List<Challenge> {
        return try {
            val snapshot = challengesCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                val challenge = doc.toObject(Challenge::class.java)
                challenge?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun insertChallenge(challenge: Challenge) {
        try {
            val data = hashMapOf(
                "description" to challenge.description
            )
            challengesCollection.add(data).await()
        } catch (e: Exception) {
            // Manejar error
        }
    }

    suspend fun updateChallenge(challenge: Challenge) {
        try {
            if (challenge.id.isNotEmpty()) {
                val data = hashMapOf(
                    "description" to challenge.description
                )
                challengesCollection.document(challenge.id).set(data).await()
            }
        } catch (e: Exception) {
            // Manejar error
        }
    }

    suspend fun deleteChallenge(challenge: Challenge) {
        try {
            if (challenge.id.isNotEmpty()) {
                challengesCollection.document(challenge.id).delete().await()
            }
        } catch (e: Exception) {
            // Manejar error
        }
    }

    suspend fun getRandomChallenge(): Challenge? {
        val all = getAllChallenges()
        return if (all.isNotEmpty()) {
            all.random()
        } else {
            null
        }
    }
}
