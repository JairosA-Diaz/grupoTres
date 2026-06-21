package com.example.grupotres.ui
// Define el paquete donde está ubicado este Fragment dentro del proyecto

import android.content.Intent
// Permite abrir otras aplicaciones o acciones externas, como Play Store

import android.media.MediaPlayer
// Permite reproducir el sonido de fondo del juego

import android.net.Uri
// Permite manejar enlaces o direcciones web

import android.os.Bundle
// Permite recibir información cuando se crea el Fragment

import android.view.LayoutInflater
// Permite convertir el archivo XML en una vista visible

import android.view.View
// Representa cualquier elemento visual de Android

import android.view.ViewGroup
// Representa el contenedor donde se va a mostrar el Fragment

import android.view.animation.Animation
// Permite usar animaciones generales

import android.view.animation.AnimationUtils
// Permite cargar animaciones desde la carpeta res/anim

import android.view.animation.RotateAnimation
// Permite crear una animación de rotación para la botella

import android.widget.Button
// Permite manipular botones desde Kotlin

import android.widget.ImageView
// Permite manipular imágenes e íconos desde Kotlin

import android.widget.TextView
// Permite manipular textos desde Kotlin

import androidx.appcompat.app.AlertDialog
// Permite crear cuadros de diálogo personalizados

import androidx.fragment.app.Fragment
// Permite crear un Fragment, que es una parte reutilizable de una pantalla

import androidx.fragment.app.viewModels
// Permite conectar el Fragment con su ViewModel

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import androidx.navigation.fragment.findNavController
// Permite navegar entre fragments usando Navigation Component

import com.example.grupotres.R
// Permite acceder a recursos como layouts, imágenes, animaciones y sonidos

import com.example.grupotres.repository.ChallengeRepository
// Importa el repositorio encargado de manejar los retos

class HomeFragment : Fragment() {
    // Define el Fragment principal del Home

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(ChallengeRepository())
    }

    private var currentAngle = 0f
    // Guarda el ángulo actual de la botella para continuar la rotación desde ahí

    private var mediaPlayer: MediaPlayer? = null
    // Variable encargada de reproducir el sonido de fondo

    private var spinMediaPlayer: MediaPlayer? = null
    // Variable encargada de reproducir el sonido de la botella girando

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Método que crea la vista visual del Fragment

        return inflater.inflate(R.layout.fragment_home, container, false)
        // Carga el diseño fragment_home.xml y lo muestra en pantalla
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Método que se ejecuta cuando la vista del Fragment ya fue creada

        super.onViewCreated(view, savedInstanceState)
        // Llama al comportamiento original del Fragment

        val ivBottle = view.findViewById<ImageView>(R.id.iv_bottle_home)
        // Conecta la imagen de la botella del XML con Kotlin

        val btnSpin = view.findViewById<Button>(R.id.btn_spin_home)
        // Conecta el botón "Presióname" del XML con Kotlin

        val tvCountdown = view.findViewById<TextView>(R.id.tv_countdown_home)
        // Conecta el contador regresivo del XML con Kotlin

        val ivSound = view.findViewById<ImageView>(R.id.iv_sound)
        // Conecta el ícono de sonido de la toolbar con Kotlin

        val blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
        // Carga la animación parpadeante desde res/anim/blink.xml

        btnSpin.startAnimation(blinkAnimation)
        // Aplica la animación parpadeante al botón

        startBackgroundSound(ivSound)
        // Inicia automáticamente el sonido de fondo al entrar al Home

        viewModel.isSoundOn.observe(viewLifecycleOwner) { isOn ->
            updateSoundIcon(ivSound, isOn)
            if (isOn) {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.seekTo(0)
                    mediaPlayer?.start()
                }
            } else {
                mediaPlayer?.pause()
            }
        }

        btnSpin.setOnClickListener {
            // Detecta cuando el usuario toca el botón Presióname

            viewModel.spinBottle()
            // Ordena al ViewModel iniciar el giro de la botella
        }

        ivSound.setOnClickListener {
            it.playTouchAnimation {
                // Detecta cuando el usuario toca el ícono de sonido
                viewModel.toggleSound()
                // Cambia entre sonido encendido y sonido apagado en el ViewModel
            }
        }

        view.findViewById<ImageView>(R.id.iv_rules).setOnClickListener {
            it.playTouchAnimation {
                // Detecta cuando el usuario toca el ícono de instrucciones
                findNavController().navigate(R.id.action_homeFragment_to_instructionsFragment)
                // Navega desde HomeFragment hacia InstructionsFragment
            }
        }

        view.findViewById<ImageView>(R.id.iv_rate).setOnClickListener {
            it.playTouchAnimation {
                // Detecta cuando el usuario toca el ícono de calificar
                val intent = Intent(Intent.ACTION_VIEW)
                // Crea un Intent para abrir una página externa
                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es")
                // Define la URL que se abrirá en el navegador o Play Store
                startActivity(intent)
                // Ejecuta el Intent y abre el enlace
            }
        }

        view.findViewById<ImageView>(R.id.iv_challenges).setOnClickListener {
            it.playTouchAnimation {
                // Navega desde HomeFragment hacia ChallengesFragment (HU 6.0)
                findNavController().navigate(R.id.action_homeFragment_to_challengesFragment)
            }
        }

        // Clic en el icono de Compartir (HU 10.0)
        view.findViewById<ImageView>(R.id.iv_share).setOnClickListener {
            it.playTouchAnimation {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                startActivity(Intent.createChooser(shareIntent, "Compartir usando:"))
            }
        }

        // Clic en el icono de Cerrar Sesión
        view.findViewById<ImageView>(R.id.iv_logout).setOnClickListener {
            it.playTouchAnimation {
                // Navegación al Login/Registro (Criterio 7)
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }

        viewModel.rotationAngle.observe(viewLifecycleOwner) { targetAngle ->
            // Observa el ángulo generado por el ViewModel para girar la botella

            val rotate = RotateAnimation(
                currentAngle,
                targetAngle,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            // Crea una animación de rotación desde el ángulo actual hasta el ángulo destino
            // El punto de giro queda en el centro de la botella

            rotate.duration = 3000
            // Define que la animación dure 3 segundos

            rotate.fillAfter = true
            // Mantiene la botella en la posición final después de girar

            ivBottle.startAnimation(rotate)
            // Aplica la animación de rotación a la botella

            currentAngle = targetAngle % 360f
            // Guarda el nuevo ángulo para el próximo giro
        }

        viewModel.isSpinning.observe(viewLifecycleOwner) { isSpinning ->
            // Observa si el juego está en curso para mostrar/ocultar el botón
            
            // Referencias a los iconos de la toolbar para desactivarlos durante el juego
            val ivRules = view.findViewById<ImageView>(R.id.iv_rules)
            val ivRate = view.findViewById<ImageView>(R.id.iv_rate)
            val ivChallenges = view.findViewById<ImageView>(R.id.iv_challenges)
            val ivShare = view.findViewById<ImageView>(R.id.iv_share)

            if (isSpinning) {
                btnSpin.visibility = View.GONE
                btnSpin.clearAnimation()

                // Desactivar clics en la toolbar (Criterio 7 HU 11: no manipular mientras partida esté en proceso)
                ivRules.isEnabled = false
                ivRate.isEnabled = false
                ivChallenges.isEnabled = false
                ivShare.isEnabled = false
                
                // Efecto visual de desactivado
                ivRules.alpha = 0.5f
                ivRate.alpha = 0.5f
                ivChallenges.alpha = 0.5f
                ivShare.alpha = 0.5f

                // Criterio 8 de HU 11: Pausar música mientras gira
                if (viewModel.isSoundOn.value == true) {
                    mediaPlayer?.pause()
                }
            } else {
                btnSpin.visibility = View.VISIBLE
                btnSpin.startAnimation(blinkAnimation)

                // Reactivar iconos de la toolbar al terminar la partida
                ivRules.isEnabled = true
                ivRate.isEnabled = true
                ivChallenges.isEnabled = true
                ivShare.isEnabled = true
                
                // Restaurar opacidad original
                ivRules.alpha = 1.0f
                ivRate.alpha = 1.0f
                ivChallenges.alpha = 1.0f
                ivShare.alpha = 1.0f

                // Reanudar música al terminar (si estaba encendida)
                if (viewModel.isSoundOn.value == true) {
                    mediaPlayer?.start()
                }
            }
        }

        viewModel.isRotating.observe(viewLifecycleOwner) { isRotating ->
            // Criterio 2: Manejar sonido de giro de forma independiente
            if (isRotating) {
                if (spinMediaPlayer == null) {
                    spinMediaPlayer = MediaPlayer.create(requireContext(), R.raw.botella_girando)
                    spinMediaPlayer?.isLooping = true
                }
                spinMediaPlayer?.start()
            } else {
                spinMediaPlayer?.pause()
                spinMediaPlayer?.seekTo(0)
            }
        }

        viewModel.countdownValue.observe(viewLifecycleOwner) { count ->
            // Observa el valor del contador regresivo

            if (count != null) {
                // Si hay un número para mostrar

                tvCountdown.visibility = View.VISIBLE
                // Muestra el contador

                tvCountdown.text = count.toString()
                // Coloca el número del contador en pantalla

            } else {
                // Si no hay contador activo

                tvCountdown.visibility = View.GONE
                // Oculta el contador
            }
        }

        viewModel.currentChallenge.observe(viewLifecycleOwner) { challenge ->
            if (challenge != null) {
                showChallengeDialog(challenge)
            }
        }

        viewModel.pokemonImage.observe(viewLifecycleOwner) { bitmap ->
            // El fragment solo se encarga de mostrar la imagen cuando el ViewModel la tiene lista
            val ivPokemon = view?.findViewById<ImageView>(R.id.iv_pokemon)
            if (bitmap != null && ivPokemon != null) {
                ivPokemon.setImageBitmap(bitmap)
            }
        }
    }

    private fun showChallengeDialog(challengeText: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_challenge, null)
        
        val tvChallenge = dialogView.findViewById<TextView>(R.id.tv_challenge_text)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close_dialog)

        tvChallenge.text = challengeText

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnClose.setOnClickListener {
            dialog.dismiss()
            viewModel.onDialogClosed()
        }

        dialog.show()
    }

    private fun startBackgroundSound(ivSound: ImageView) {
        // Función encargada de iniciar el sonido de fondo del juego

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.sonido_de_fondo)
            mediaPlayer?.isLooping = true
            mediaPlayer?.setVolume(1.0f, 1.0f)
        }

        val isOn = viewModel.isSoundOn.value ?: true
        if (isOn) {
            mediaPlayer?.seekTo(0)
            mediaPlayer?.start()
        }
        updateSoundIcon(ivSound, isOn)
    }

    private fun updateSoundIcon(ivSound: ImageView, isOn: Boolean) {
        if (isOn) {
            ivSound.setImageResource(R.drawable.ic_volume_on)
            ivSound.contentDescription = "Sonido encendido"
        } else {
            ivSound.setImageResource(R.drawable.ic_volume_off)
            ivSound.contentDescription = "Sonido apagado"
        }
    }

    private fun View.playTouchAnimation(action: () -> Unit) {
        val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.touch_click)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                action()
            }
        })
        this.startAnimation(anim)
    }

    private fun toggleSound(ivSound: ImageView) {
        // Esta función ya no se usa directamente, se usa viewModel.toggleSound()
    }

    override fun onPause() {
        // Se ejecuta cuando el Fragment deja de estar activo

        super.onPause()
        // Llama al comportamiento original de pausa

        mediaPlayer?.pause()
        // Pausa el sonido para que no siga sonando fuera del Home

        spinMediaPlayer?.pause()
        // Pausa el sonido de giro si el app se va a segundo plano
    }

    override fun onResume() {
        // Se ejecuta cuando el Fragment vuelve a estar activo

        super.onResume()
        // Llama al comportamiento original de reanudación

        if (viewModel.isSoundOn.value == true) {
            // Si el sonido estaba encendido antes de pausar

            mediaPlayer?.seekTo(0)
            // Reinicia el sonido desde el principio (Criterio 3)

            mediaPlayer?.start()
            // Reanuda el sonido de fondo
        }
    }

    override fun onDestroyView() {
        // Se ejecuta cuando se destruye la vista del Fragment

        super.onDestroyView()
        // Llama al comportamiento original de destrucción

        mediaPlayer?.release()
        // Libera los recursos usados por el reproductor de audio

        spinMediaPlayer?.release()
        // Libera los recursos usados por el reproductor de giro

        mediaPlayer = null
        spinMediaPlayer = null
        // Limpia las referencias
    }
}