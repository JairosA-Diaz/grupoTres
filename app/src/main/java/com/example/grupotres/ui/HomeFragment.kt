package com.example.grupotres.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.grupotres.R
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var lastAngle = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivBottle = view.findViewById<ImageView>(R.id.iv_bottle_home)
        val btnSpin = view.findViewById<Button>(R.id.btn_spin_home)

        // Animación parpadeante para el botón
        val blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
        btnSpin.startAnimation(blinkAnimation)

        btnSpin.setOnClickListener {
            spinBottle(ivBottle, btnSpin)
        }
    }

    private fun spinBottle(ivBottle: ImageView, btnSpin: Button) {
        // Desaparecer botón momentáneamente
        btnSpin.visibility = View.GONE
        btnSpin.clearAnimation()

        // Giro aleatorio (HU 11)
        val newAngle = Random.nextInt(3600) + 360f
        
        val rotate = RotateAnimation(
            lastAngle, lastAngle + newAngle,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        lastAngle = (lastAngle + newAngle) % 360f
        rotate.duration = 3000
        rotate.fillAfter = true

        rotate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                // Reaparecer botón con animación
                btnSpin.visibility = View.VISIBLE
                val blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
                btnSpin.startAnimation(blinkAnimation)
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        ivBottle.startAnimation(rotate)
    }
}