package com.example.grupotres.ui
// Define el paquete donde está ubicado el ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// Importa las herramientas necesarias para manejar el ciclo de vida y datos observables

import com.example.grupotres.repository.ChallengeRepository
// Importa el repositorio para acceder a los retos de la base de datos

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
// Importa herramientas para manejar procesos en segundo plano (corrutinas) y retardos

import kotlin.random.Random
// Importa la herramienta para generar números aleatorios

class HomeViewModel(private val repository: ChallengeRepository) : ViewModel() {
    // Clase ViewModel encargada de la lógica de negocio de la pantalla principal

    private val _rotationAngle = MutableLiveData<Float>()
    val rotationAngle: LiveData<Float> = _rotationAngle
    // Ángulo de rotación de la botella para la animación

    private val _countdownValue = MutableLiveData<Int?>()
    val countdownValue: LiveData<Int?> = _countdownValue
    // Valor actual del contador regresivo (3, 2, 1, 0)

    private val _isSpinning = MutableLiveData<Boolean>(false)
    val isSpinning: LiveData<Boolean> = _isSpinning
    // Indica si la partida está en curso (bloquea el botón)

    private val _isRotating = MutableLiveData<Boolean>(false)
    val isRotating: LiveData<Boolean> = _isRotating
    // Indica si la botella está físicamente girando en este momento

    private val _isSoundOn = MutableLiveData<Boolean>(true)
    val isSoundOn: LiveData<Boolean> = _isSoundOn
    // Estado del interruptor de sonido de la aplicación

    private val _currentChallenge = MutableLiveData<String?>()
    val currentChallenge: LiveData<String?> = _currentChallenge
    // Almacena la descripción del reto seleccionado aleatoriamente

    private val _pokemonImage = MutableLiveData<android.graphics.Bitmap?>()
    val pokemonImage: LiveData<android.graphics.Bitmap?> = _pokemonImage

    private var lastAngle = 0f
    // Guarda el último ángulo para que la botella siempre empiece a girar desde donde quedó

    fun spinBottle() {
        // Función principal para iniciar el giro de la botella y la partida
        
        if (_isSpinning.value == true) return
        // Si ya está girando, ignora nuevos clics (Criterio 7 HU 11)

        viewModelScope.launch {
            // Inicia una corrutina para manejar los tiempos de espera sin trabar la app
            
            _isSpinning.value = true
            _isRotating.value = true
            _countdownValue.value = null
            _currentChallenge.value = null
            // Reinicia estados previos de la partida

            val newAngle = Random.nextInt(3600) + 360f
            // Genera un giro aleatorio de varias vueltas (Criterio 3 HU 11)
            
            val totalRotation = lastAngle + newAngle
            // Suma el nuevo ángulo al anterior para que la rotación sea fluida (Criterio 4 HU 11)
            
            _rotationAngle.value = totalRotation
            lastAngle = totalRotation % 360f
            // Actualiza el ángulo y guarda el resto para la próxima vez

            delay(3000)
            // Espera 3 segundos que dura la animación de giro (Criterio 1 HU 11)
            
            _isRotating.value = false
            // La botella deja de girar visualmente

            for (i in 3 downTo 0) {
                // Inicia la cuenta regresiva de 3 a 0 (Criterio 5 HU 11)
                _countdownValue.value = i
                delay(1000)
            }

            _countdownValue.value = null
            // Limpia el valor del contador después de terminar
            
            val randomChallenge = repository.getRandomChallenge()
            // Busca un reto al azar en la base de datos (HU 12)
            
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
        // Se ejecuta cuando el jugador cierra el diálogo del reto
        _isSpinning.value = false
        _currentChallenge.value = null
        _pokemonImage.value = null
    }

    fun toggleSound() {
        // Cambia el estado del sonido entre encendido y apagado (Criterio 3 HU 3.0)
        _isSoundOn.value = !(_isSoundOn.value ?: true)
    }
}
