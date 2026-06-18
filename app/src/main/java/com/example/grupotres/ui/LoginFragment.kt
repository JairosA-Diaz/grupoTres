package com.example.grupotres.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.grupotres.R
import com.example.grupotres.data.AppDatabase
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch

import androidx.fragment.app.viewModels

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tilPassword = view.findViewById<TextInputLayout>(R.id.til_password)
        val etEmail = view.findViewById<TextInputEditText>(R.id.et_email)
        val etPassword = view.findViewById<TextInputEditText>(R.id.et_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val tvRegister = view.findViewById<View>(R.id.tv_register_link)

        fun updateLoginButtonState() {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val isEnabled = email.isNotEmpty() && password.isNotEmpty()
            
            btnLogin.isEnabled = isEnabled
            tvRegister.isEnabled = isEnabled
            
            // Una vez se habilite el botón, el texto “Login” tendrá un estilo bold
            if (isEnabled) {
                btnLogin.setTypeface(null, Typeface.BOLD)
            } else {
                btnLogin.setTypeface(null, Typeface.NORMAL)
            }
        }

        // Inicializar el estado del botón
        updateLoginButtonState()

        val commonTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etEmail.addTextChangedListener(commonTextWatcher)
        
        // Criterio 5: Validación en tiempo real del password + actualización de estado del botón
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateLoginButtonState()
                val password = s.toString()
                if (password.isNotEmpty() && password.length < 6) {
                    tilPassword.error = "Mínimo 6 dígitos"
                } else {
                    tilPassword.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (password.length >= 6) {
                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(requireContext())
                    val user = db.userDao().getUser(email, password)

                    if (user != null) {
                        // Criterio 1 HU 3.0: Guardar sesión
                        val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putBoolean("is_logged_in", true)
                            apply()
                        }
                        // Si cumple la validación, navegamos al Home
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        // Login incorrecto
                        Toast.makeText(requireContext(), "Login incorrecto", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                tilPassword.error = "Mínimo 6 dígitos"
            }
        }
    }
}
