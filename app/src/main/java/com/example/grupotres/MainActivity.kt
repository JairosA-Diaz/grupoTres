package com.example.grupotres

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private var lastAngle = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnSpin = findViewById<Button>(R.id.btn_spin)
        val ivBottle = findViewById<ImageView>(R.id.iv_bottle)
        
        val blinkAnimation = AnimationUtils.loadAnimation(this, R.anim.blink)
        btnSpin.startAnimation(blinkAnimation)

        btnSpin.setOnClickListener {
            spinBottle(ivBottle, btnSpin)
        }
    }

    private fun spinBottle(ivBottle: ImageView, btnSpin: Button) {
        // Criterio 7: El botón desaparece momentáneamente
        btnSpin.visibility = View.GONE
        btnSpin.clearAnimation()

        // Criterio 1 y 3: Giro aleatorio por unos segundos
        val newAngle = Random.nextInt(3600) + 360f // Al menos una vuelta completa + aleatorio
        
        val rotate = RotateAnimation(
            lastAngle, lastAngle + newAngle,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        lastAngle = (lastAngle + newAngle) % 360f
        rotate.duration = 3000 // 3 segundos (Criterio 1)
        rotate.fillAfter = true // Mantener la posición final (Criterio 4)

        rotate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Aquí irá el contador regresivo (Criterio 5) más adelante
                btnSpin.visibility = View.VISIBLE
                btnSpin.startAnimation(AnimationUtils.loadAnimation(this@MainActivity, R.anim.blink))
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        ivBottle.startAnimation(rotate)
    }
}