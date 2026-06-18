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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.grupotres.R
import com.example.grupotres.data.AppDatabase
import com.example.grupotres.repository.UserRepository
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = UserRepository(database.userDao())
        LoginViewModelFactory(repository)
    }

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

        // Observar si el botón de login debe estar habilitado
        viewModel.isLoginEnabled.observe(viewLifecycleOwner) { isEnabled ->
            btnLogin.isEnabled = isEnabled
            tvRegister.isEnabled = isEnabled
            
            // Criterio: Estilo bold cuando se habilita
            if (isEnabled) {
                btnLogin.setTypeface(null, Typeface.BOLD)
            } else {
                btnLogin.setTypeface(null, Typeface.NORMAL)
            }
        }

        // Observar errores de validación de password
        viewModel.passwordError.observe(viewLifecycleOwner) { error ->
            tilPassword.error = error
        }

        // Observar errores de login (credenciales incorrectas)
        viewModel.loginErrorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                viewModel.onErrorMessageShown()
            }
        }

        // Observar éxito del login para navegar
        viewModel.navigateToHome.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                // Criterio 1 HU 3.0: Guardar sesión
                val sharedPref = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("is_logged_in", true)
                    apply()
                }
                
                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                viewModel.onNavigatedToHome()
            }
        }

        // Listener común para ambos campos
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
    }
}