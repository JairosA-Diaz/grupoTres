package com.example.grupotres
// Define el paquete principal donde está ubicada esta clase dentro del proyecto

import android.os.Bundle
// Importa Bundle, que permite manejar información cuando se crea la pantalla

import android.os.CountDownTimer
// Importa CountDownTimer para crear el contador regresivo de 3 a 0

import android.view.animation.AnimationUtils
// Importa AnimationUtils para cargar animaciones XML desde la carpeta res/anim

import android.widget.TextView
// Importa TextView para poder manipular textos y vistas tipo texto desde Kotlin

import androidx.activity.enableEdgeToEdge
// Importa enableEdgeToEdge para permitir que la pantalla use todo el espacio disponible

import androidx.appcompat.app.AppCompatActivity
// Importa AppCompatActivity, que permite crear una pantalla compatible con versiones anteriores de Android

import androidx.core.view.ViewCompat
// Importa ViewCompat para trabajar con ajustes visuales de compatibilidad

import androidx.core.view.WindowInsetsCompat
// Importa WindowInsetsCompat para manejar espacios de barras del sistema como estado y navegación

class MainActivity : AppCompatActivity() {
    // Define la pantalla principal de la aplicación

    private lateinit var txtCounter: TextView
    // Declara una variable para controlar el contador que está sobre la botella

    private lateinit var btnPress: TextView
    // Declara una variable para controlar el botón circular naranja parpadeante

    override fun onCreate(savedInstanceState: Bundle?) {
        // Método que se ejecuta automáticamente cuando se abre esta pantalla

        super.onCreate(savedInstanceState)
        // Llama al comportamiento original de Android para crear correctamente la pantalla

        enableEdgeToEdge()
        // Permite que la interfaz pueda ocupar el espacio completo de la pantalla

        setContentView(R.layout.activity_main)
        // Indica que esta pantalla usará el diseño definido en activity_main.xml

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            // Ajusta automáticamente los espacios para no tapar contenido con barras del sistema

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            // Obtiene el tamaño de las barras del sistema: superior, inferior, izquierda y derecha

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            // Aplica esos espacios como margen interno para que el contenido no quede oculto

            insets
            // Devuelve los insets para que Android termine correctamente el ajuste visual
        }

        txtCounter = findViewById(R.id.txtCounter)
        // Conecta la variable txtCounter con el TextView del contador en activity_main.xml

        btnPress = findViewById(R.id.btnPress)
        // Conecta la variable btnPress con el botón circular naranja en activity_main.xml

        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink_button)
        // Carga la animación parpadeante creada en res/anim/blink_button.xml

        btnPress.startAnimation(blinkAnimation)
        // Aplica la animación parpadeante al botón naranja

        startInitialCounter()
        // Inicia el contador regresivo cuando se abre la pantalla Home
    }

    private fun startInitialCounter() {
        // Función encargada de iniciar el contador regresivo de la botella

        object : CountDownTimer(4000, 1000) {
            // Crea un contador de 4 segundos que se actualiza cada 1 segundo
            // Se usa 4000 para que alcance a mostrar 3, 2, 1 y 0

            override fun onTick(millisUntilFinished: Long) {
                // Método que se ejecuta cada segundo mientras el contador está activo

                val seconds = (millisUntilFinished / 1000).toInt()
                // Convierte los milisegundos restantes en segundos enteros

                txtCounter.text = seconds.toString()
                // Muestra el número actual del contador en pantalla
            }

            override fun onFinish() {
                // Método que se ejecuta cuando el contador termina

                txtCounter.text = "0"
                // Muestra el número 0 al finalizar el contador
            }

        }.start()
        // Inicia la ejecución del contador regresivo
    }
}