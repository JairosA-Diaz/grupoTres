package com.example.grupotres.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    fun onPasswordChanged(password: String) {
        if (password.isNotEmpty() && password.length < 6) {
            _passwordError.value = "Mínimo 6 dígitos"
        } else {
            _passwordError.value = null
        }
    }

    fun onLoginClicked(password: String) {
        if (password.length >= 6) {
            _navigateToHome.value = true
        } else {
            _passwordError.value = "Mínimo 6 dígitos"
        }
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false
    }
}