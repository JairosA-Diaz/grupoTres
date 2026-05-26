package com.example.grupotres

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Criterio 4: Mostrar por 5 segundos (5000 ms)
        Handler(Looper.getMainLooper()).postDelayed({
            // Ir al Home (MainActivity)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            // Criterio 5: Al dar atrás no debe regresar al splash
            finish()
        }, 5000)
    }
}