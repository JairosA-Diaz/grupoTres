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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.grupotres.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
        val tvRegister = view.findViewById<TextView>(R.id.tv_register_link)

        // Observar si los botones deben estar habilitados (Criterio 7, 8, 11 y 12)
        viewModel.isLoginEnabled.observe(viewLifecycleOwner) { isEnabled ->
            btnLogin.isEnabled = isEnabled
            tvRegister.isEnabled = isEnabled
            
            if (isEnabled) {
                btnLogin.setTypeface(null, Typeface.BOLD)
                tvRegister.setTypeface(null, Typeface.BOLD)
            } else {
                btnLogin.setTypeface(null, Typeface.NORMAL)
                tvRegister.setTypeface(null, Typeface.NORMAL)
            }
        }

        // Observar errores de validación de password (Criterio 5)
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            tilPassword.error = error
        }

        // Observar mensajes de Auth (Criterio 9 y 13)
        viewModel.authErrorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.onErrorMessageShown()
            }
        }

        // Observar éxito para navegar (Criterio 10 y 14)
        viewModel.navigateToHome.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("is_logged_in", true)
                    apply()
                }
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                viewModel.onNavigatedToHome()
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onFieldsChanged(
                    etEmail.text.toString().trim(),
                    etPassword.text.toString().trim()
                )
            }
            override fun afterTextChanged(s: Editable?) {}
        }

        etEmail.addTextChangedListener(textWatcher)
        etPassword.addTextChangedListener(textWatcher)

        btnLogin.setOnClickListener {
            viewModel.onLoginClicked(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )
        }

        tvRegister.setOnClickListener {
            viewModel.onRegisterClicked(
                etEmail.text.toString().trim(),
                etPassword.text.toString().trim()
            )
        }
    }
}