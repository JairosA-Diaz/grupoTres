package com.example.grupotres.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupotres.data.Challenge
import com.example.grupotres.repository.ChallengeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChallengesViewModel @Inject constructor(private val repository: ChallengeRepository) : ViewModel() {

    private val _allChallenges = MutableLiveData<List<Challenge>>()
    val allChallenges: LiveData<List<Challenge>> = _allChallenges

    init {
        loadChallenges()
    }

    fun loadChallenges() {
        viewModelScope.launch {
            _allChallenges.value = repository.getAllChallenges().reversed() 
            // reversed() para que el nuevo aparezca arriba (Criterio 6 HU 6.0)
        }
    }

    fun deleteChallenge(challenge: Challenge) {
        viewModelScope.launch {
            repository.deleteChallenge(challenge)
            loadChallenges()
        }
    }
    
    fun addChallenge(description: String) {
        viewModelScope.launch {
            repository.insertChallenge(Challenge(description = description))
            loadChallenges()
        }
    }

    fun updateChallenge(challenge: Challenge) {
        viewModelScope.launch {
            repository.updateChallenge(challenge)
            loadChallenges()
        }
    }
}