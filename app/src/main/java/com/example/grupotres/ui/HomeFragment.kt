package com.example.grupotres.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.grupotres.R
import com.example.grupotres.data.AppDatabase
import com.example.grupotres.repository.ChallengeRepository

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = ChallengeRepository(database.challengeDao())
        HomeViewModelFactory(repository)
    }

    private var currentAngle = 0f

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
        val tvCountdown = view.findViewById<TextView>(R.id.tv_countdown_home)

        // Animación parpadeante para el botón
        val blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
        btnSpin.startAnimation(blinkAnimation)

        btnSpin.setOnClickListener {
            viewModel.spinBottle()
        }

        // Clic en el icono de Instrucciones
        view.findViewById<ImageView>(R.id.iv_rules).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_instructionsFragment)
        }

        // Clic en el icono de Calificar (HU 4.0)
        view.findViewById<ImageView>(R.id.iv_rate).setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es")
            startActivity(intent)
        }

        // Observamos el ángulo de rotación
        viewModel.rotationAngle.observe(viewLifecycleOwner) { targetAngle ->
            val rotate = RotateAnimation(
                currentAngle, targetAngle,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            )
            rotate.duration = 3000
            rotate.fillAfter = true
            ivBottle.startAnimation(rotate)
            currentAngle = targetAngle % 360f
        }

        // Observamos el estado de giro para ocultar/mostrar el botón
        viewModel.isSpinning.observe(viewLifecycleOwner) { isSpinning ->
            if (isSpinning) {
                btnSpin.visibility = View.GONE
                btnSpin.clearAnimation()
            } else {
                btnSpin.visibility = View.VISIBLE
                btnSpin.startAnimation(blinkAnimation)
            }
        }

        // Observamos el valor del contador
        viewModel.countdownValue.observe(viewLifecycleOwner) { count ->
            if (count != null) {
                tvCountdown.visibility = View.VISIBLE
                tvCountdown.text = count.toString()
            } else {
                tvCountdown.visibility = View.GONE
            }
        }
    }
}