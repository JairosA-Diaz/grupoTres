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

    private val _isRotating = MutableLiveData<Boolean>(false)
    val isRotating: LiveData<Boolean> = _isRotating

    private val _isSoundOn = MutableLiveData<Boolean>(true)
    val isSoundOn: LiveData<Boolean> = _isSoundOn

    private val _currentChallenge = MutableLiveData<String?>()
    val currentChallenge: LiveData<String?> = _currentChallenge

    private val _pokemonImage = MutableLiveData<android.graphics.Bitmap?>()
    val pokemonImage: LiveData<android.graphics.Bitmap?> = _pokemonImage

    private var lastAngle = 0f

    fun spinBottle() {
        if (_isSpinning.value == true) return

        viewModelScope.launch {
            _isSpinning.value = true
            _isRotating.value = true
            _countdownValue.value = null
            _currentChallenge.value = null

            val newAngle = Random.nextInt(3600) + 360f
            val totalRotation = lastAngle + newAngle
            
            _rotationAngle.value = totalRotation
            lastAngle = totalRotation % 360f

            delay(3000)
            _isRotating.value = false

            for (i in 3 downTo 0) {
                _countdownValue.value = i
                delay(1000)
            }

            _countdownValue.value = null
            
            val randomChallenge = repository.getRandomChallenge()
            _currentChallenge.value = randomChallenge?.description ?: "¡Baila por 30 segundos!"
            
            fetchRandomPokemon()
        }
    }

    private fun fetchRandomPokemon() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val json = java.net.URL("https://raw.githubusercontent.com/Biuni/PokemonGO-Pokedex/master/pokedex.json")
                    .readText()
                val array = org.json.JSONObject(json).getJSONArray("pokemon")
                val randomPokemon = array.getJSONObject((0 until array.length()).random())
                val imgUrl = randomPokemon.getString("img").replace("http://", "https://")
                
                val stream = java.net.URL(imgUrl).openStream()
                val bitmap = android.graphics.BitmapFactory.decodeStream(stream)
                _pokemonImage.postValue(bitmap)
            } catch (e: Exception) {
                android.util.Log.e("POKEMON_ERROR", "Error: ${e.message}")
            }
        }
    }

    fun onDialogClosed() {
        _isSpinning.value = false
        _currentChallenge.value = null
        _pokemonImage.value = null
    }

    fun toggleSound() {
        _isSoundOn.value = !(_isSoundOn.value ?: true)
    }
}