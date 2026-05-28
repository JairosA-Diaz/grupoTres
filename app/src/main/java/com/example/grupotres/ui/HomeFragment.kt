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

import androidx.navigation.fragment.findNavController
// Permite navegar entre fragments usando Navigation Component

import com.example.grupotres.R
// Permite acceder a recursos como layouts, imágenes, animaciones y sonidos

import com.example.grupotres.data.AppDatabase
// Importa la base de datos local del proyecto

import com.example.grupotres.repository.ChallengeRepository
// Importa el repositorio encargado de manejar los retos

class HomeFragment : Fragment() {
    // Define el Fragment principal del Home

    private val viewModel: HomeViewModel by viewModels {
        // Crea y conecta el ViewModel del Home

        val database = AppDatabase.getDatabase(requireContext())
        // Obtiene la instancia de la base de datos usando el contexto actual

        val repository = ChallengeRepository(database.challengeDao())
        // Crea el repositorio usando el DAO de retos

        HomeViewModelFactory(repository)
        // Crea el ViewModel usando la fábrica personalizada
    }

    private var currentAngle = 0f
    // Guarda el ángulo actual de la botella para continuar la rotación desde ahí

    private var mediaPlayer: MediaPlayer? = null
    // Variable encargada de reproducir el sonido de fondo

    private var isSoundOn = true
    // Indica si el sonido está encendido o apagado
    // Inicia en true porque el criterio pide que el sonido esté encendido por defecto

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

        btnSpin.setOnClickListener {
            // Detecta cuando el usuario toca el botón Presióname

            viewModel.spinBottle()
            // Ordena al ViewModel iniciar el giro de la botella
        }

        ivSound.setOnClickListener {
            // Detecta cuando el usuario toca el ícono de sonido

            toggleSound(ivSound)
            // Cambia entre sonido encendido y sonido apagado
        }

        view.findViewById<ImageView>(R.id.iv_rules).setOnClickListener {
            // Detecta cuando el usuario toca el ícono de instrucciones

            findNavController().navigate(R.id.action_homeFragment_to_instructionsFragment)
            // Navega desde HomeFragment hacia InstructionsFragment
        }

        view.findViewById<ImageView>(R.id.iv_rate).setOnClickListener {
            // Detecta cuando el usuario toca el ícono de calificar

            val intent = Intent(Intent.ACTION_VIEW)
            // Crea un Intent para abrir una página externa

            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es")
            // Define la URL que se abrirá en el navegador o Play Store

            startActivity(intent)
            // Ejecuta el Intent y abre el enlace
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
            // Observa si la botella está girando o no

            if (isSpinning) {
                // Si la botella está girando

                btnSpin.visibility = View.GONE
                // Oculta el botón Presióname

                btnSpin.clearAnimation()
                // Detiene la animación parpadeante del botón

                // Criterio 8 de HU 11: Pausar música mientras gira
                if (isSoundOn) {
                    mediaPlayer?.pause()
                }

            } else {
                // Si la botella ya dejó de girar (partida terminada o inicial)

                btnSpin.visibility = View.VISIBLE
                // Muestra nuevamente el botón

                btnSpin.startAnimation(blinkAnimation)
                // Vuelve a aplicar la animación parpadeante

                // Reanudar música al terminar (si estaba encendida)
                if (isSoundOn) {
                    mediaPlayer?.start()
                }
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
    }

    private fun showChallengeDialog(challengeText: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_challenge, null)
        val tvChallenge = dialogView.findViewById<TextView>(R.id.tv_challenge_text)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close_dialog)

        tvChallenge.text = challengeText

        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        btnClose.setOnClickListener {
            dialog.dismiss()
            viewModel.onDialogClosed()
        }

        dialog.show()
    }

    private fun startBackgroundSound(ivSound: ImageView) {
        // Función encargada de iniciar el sonido de fondo del juego

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.sonido_de_fondo)
        // Crea el reproductor usando el archivo sonido_de_fondo.mp3 ubicado en res/raw
        // En Kotlin se llama sin escribir la extensión .mp3

        mediaPlayer?.isLooping = true
        // Hace que el sonido se repita continuamente

        mediaPlayer?.setVolume(1.0f, 1.0f)
        // Establece el volumen al máximo nivel permitido para este flujo de audio

        mediaPlayer?.start()
        // Inicia la reproducción del sonido de fondo

        isSoundOn = true
        // Marca el sonido como encendido

        ivSound.setImageResource(R.drawable.ic_volume_on)
        // Muestra el ícono de sonido encendido

        ivSound.contentDescription = "Sonido encendido"
        // Actualiza la descripción del ícono
    }

    private fun toggleSound(ivSound: ImageView) {
        // Función encargada de encender o apagar el sonido

        if (isSoundOn) {
            // Si el sonido está encendido

            mediaPlayer?.pause()
            // Pausa el sonido de fondo

            isSoundOn = false
            // Cambia el estado del sonido a apagado

            ivSound.setImageResource(R.drawable.ic_volume_off)
            // Cambia el ícono a sonido apagado

            ivSound.contentDescription = "Sonido apagado"
            // Actualiza la descripción del ícono

        } else {
            // Si el sonido está apagado

            mediaPlayer?.start()
            // Reanuda el sonido de fondo

            isSoundOn = true
            // Cambia el estado del sonido a encendido

            ivSound.setImageResource(R.drawable.ic_volume_on)
            // Cambia el ícono a sonido encendido

            ivSound.contentDescription = "Sonido encendido"
            // Actualiza la descripción del ícono
        }
    }

    override fun onPause() {
        // Se ejecuta cuando el Fragment deja de estar activo

        super.onPause()
        // Llama al comportamiento original de pausa

        mediaPlayer?.pause()
        // Pausa el sonido para que no siga sonando fuera del Home
    }

    override fun onResume() {
        // Se ejecuta cuando el Fragment vuelve a estar activo

        super.onResume()
        // Llama al comportamiento original de reanudación

        if (isSoundOn) {
            // Si el sonido estaba encendido antes de pausar

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

        mediaPlayer = null
        // Limpia la referencia del reproductor
    }
}