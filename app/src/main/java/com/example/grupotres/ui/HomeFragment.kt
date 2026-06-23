package com.example.grupotres.ui

import android.content.Intent
import android.media.MediaPlayer
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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.grupotres.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModels()

    private var currentAngle = 0f
    private var mediaPlayer: MediaPlayer? = null
    private var spinMediaPlayer: MediaPlayer? = null
    private var challengeDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivBottle = view.findViewById<ImageView>(R.id.iv_bottle_home)
        val btnSpin = view.findViewById<Button>(R.id.btn_spin_home)
        val tvCountdown = view.findViewById<TextView>(R.id.tv_countdown_home)
        val ivSound = view.findViewById<ImageView>(R.id.iv_sound)
        val blinkAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)

        btnSpin.startAnimation(blinkAnimation)
        startBackgroundSound(ivSound)

        viewModel.isSoundOn.observe(viewLifecycleOwner) { isOn ->
            updateSoundIcon(ivSound, isOn)
            if (isOn) {
                if (mediaPlayer?.isPlaying == false) {
                    mediaPlayer?.start()
                }
            } else {
                mediaPlayer?.pause()
            }
        }

        btnSpin.setOnClickListener {
            viewModel.spinBottle()
        }

        ivSound.setOnClickListener {
            it.playTouchAnimation {
                viewModel.toggleSound()
            }
        }

        view.findViewById<ImageView>(R.id.iv_rules).setOnClickListener {
            it.playTouchAnimation {
                findNavController().navigate(R.id.action_homeFragment_to_instructionsFragment)
            }
        }

        view.findViewById<ImageView>(R.id.iv_rate).setOnClickListener {
            it.playTouchAnimation {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://play.google.com/store/apps/details?id=com.nequi.MobileApp&hl=es_419&gl=es")
                startActivity(intent)
            }
        }

        view.findViewById<ImageView>(R.id.iv_challenges).setOnClickListener {
            it.playTouchAnimation {
                findNavController().navigate(R.id.action_homeFragment_to_challengesFragment)
            }
        }

        view.findViewById<ImageView>(R.id.iv_share).setOnClickListener {
            it.playTouchAnimation {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message))
                startActivity(Intent.createChooser(shareIntent, "Compartir usando:"))
            }
        }

        view.findViewById<ImageView>(R.id.iv_logout).setOnClickListener {
            it.playTouchAnimation {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }

        viewModel.rotationAngle.observe(viewLifecycleOwner) { targetAngle ->
            val rotate = RotateAnimation(
                currentAngle,
                targetAngle,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            )
            rotate.duration = 3000
            rotate.fillAfter = true
            ivBottle.startAnimation(rotate)
            currentAngle = targetAngle % 360f
        }

        viewModel.isSpinning.observe(viewLifecycleOwner) { isSpinning ->
            val ivRules = view.findViewById<ImageView>(R.id.iv_rules)
            val ivRate = view.findViewById<ImageView>(R.id.iv_rate)
            val ivChallenges = view.findViewById<ImageView>(R.id.iv_challenges)
            val ivShare = view.findViewById<ImageView>(R.id.iv_share)

            if (isSpinning) {
                btnSpin.visibility = View.GONE
                btnSpin.clearAnimation()
                ivRules.isEnabled = false
                ivRate.isEnabled = false
                ivChallenges.isEnabled = false
                ivShare.isEnabled = false
                ivRules.alpha = 0.5f
                ivRate.alpha = 0.5f
                ivChallenges.alpha = 0.5f
                ivShare.alpha = 0.5f
                if (viewModel.isSoundOn.value == true) {
                    mediaPlayer?.pause()
                }
            } else {
                btnSpin.visibility = View.VISIBLE
                btnSpin.startAnimation(blinkAnimation)
                ivRules.isEnabled = true
                ivRate.isEnabled = true
                ivChallenges.isEnabled = true
                ivShare.isEnabled = true
                ivRules.alpha = 1.0f
                ivRate.alpha = 1.0f
                ivChallenges.alpha = 1.0f
                ivShare.alpha = 1.0f
                if (viewModel.isSoundOn.value == true) {
                    mediaPlayer?.start()
                }
            }
        }

        viewModel.isRotating.observe(viewLifecycleOwner) { isRotating ->
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
            if (count != null) {
                tvCountdown.visibility = View.VISIBLE
                tvCountdown.text = count.toString()
            } else {
                tvCountdown.visibility = View.GONE
            }
        }

        viewModel.currentChallenge.observe(viewLifecycleOwner) { challenge ->
            if (challenge != null) {
                showChallengeDialog(challenge)
            } else {
                challengeDialog?.dismiss()
                challengeDialog = null
            }
        }

        viewModel.pokemonImage.observe(viewLifecycleOwner) { bitmap ->
            if (bitmap != null && challengeDialog?.isShowing == true) {
                val ivPokemon = challengeDialog?.findViewById<ImageView>(R.id.iv_pokemon)
                ivPokemon?.setImageBitmap(bitmap)
            }
        }
    }

    private fun showChallengeDialog(challengeText: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_challenge, null)
        
        val tvChallenge = dialogView.findViewById<TextView>(R.id.tv_challenge_text)
        val btnClose = dialogView.findViewById<Button>(R.id.btn_close_dialog)
        val ivPokemon = dialogView.findViewById<ImageView>(R.id.iv_pokemon)

        tvChallenge.text = challengeText
        
        viewModel.pokemonImage.value?.let {
            ivPokemon.setImageBitmap(it)
        }

        challengeDialog = AlertDialog.Builder(requireContext(), R.style.CustomDialogTheme)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        challengeDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        btnClose.setOnClickListener {
            viewModel.onDialogClosed()
        }

        challengeDialog?.show()
    }

    private fun startBackgroundSound(ivSound: ImageView) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(requireContext(), R.raw.sonido_de_fondo)
            mediaPlayer?.isLooping = true
        }

        val isOn = viewModel.isSoundOn.value ?: true
        if (isOn) {
            mediaPlayer?.start()
        }
        updateSoundIcon(ivSound, isOn)
    }

    private fun updateSoundIcon(ivSound: ImageView, isOn: Boolean) {
        if (isOn) {
            ivSound.setImageResource(R.drawable.ic_volume_on)
        } else {
            ivSound.setImageResource(R.drawable.ic_volume_off)
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

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
        spinMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.isSoundOn.value == true && viewModel.isSpinning.value == false) {
            mediaPlayer?.start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        spinMediaPlayer?.release()
        mediaPlayer = null
        spinMediaPlayer = null
        challengeDialog?.dismiss()
        challengeDialog = null
    }
}