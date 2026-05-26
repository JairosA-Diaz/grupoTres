package com.example.grupotres.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupotres.repository.ChallengeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class HomeViewModel(private val repository: ChallengeRepository) : ViewModel() {

    private val _rotationAngle = MutableLiveData<Float>()
    val rotationAngle: LiveData<Float> = _rotationAngle

    private val _countdownValue = MutableLiveData<Int?>()
    val countdownValue: LiveData<Int?> = _countdownValue

    private val _isSpinning = MutableLiveData<Boolean>(false)
    val isSpinning: LiveData<Boolean> = _isSpinning

    private var lastAngle = 0f

    fun spinBottle() {
        if (_isSpinning.value == true) return

        viewModelScope.launch {
            _isSpinning.value = true
            _countdownValue.value = null

            val newAngle = Random.nextInt(3600) + 360f
            val totalRotation = lastAngle + newAngle
            
            _rotationAngle.value = totalRotation
            lastAngle = totalRotation % 360f

            // Simulamos el tiempo de la animación de giro (3 segundos)
            delay(3000)

            // Iniciar cuenta regresiva
            for (i in 3 downTo 0) {
                _countdownValue.value = i
                delay(1000)
            }

            _countdownValue.value = null
            _isSpinning.value = false
            
            // Aquí en el futuro pediremos un reto al repository
            // val challenge = repository.getRandomChallenge()
        }
    }
}