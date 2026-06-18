package com.example.grupotres.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupotres.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _isLoginEnabled = MutableLiveData<Boolean>(false)
    val isLoginEnabled: LiveData<Boolean> = _isLoginEnabled

    private val _loginErrorMessage = MutableLiveData<String?>()
    val loginErrorMessage: LiveData<String?> = _loginErrorMessage

    private val _navigateToHome = MutableLiveData<Boolean>()
    val navigateToHome: LiveData<Boolean> = _navigateToHome

    fun onFieldsChanged(email: String, password: String) {
        val isEnabled = email.isNotEmpty() && password.isNotEmpty()
        _isLoginEnabled.value = isEnabled

        if (password.isNotEmpty() && password.length < 6) {
            _passwordError.value = "Mínimo 6 dígitos"
        } else {
            _passwordError.value = null
        }
    }

    fun onLoginClicked(email: String, password: String) {
        if (password.length >= 6) {
            viewModelScope.launch {
                val user = repository.getUser(email, password)
                if (user != null) {
                    _navigateToHome.value = true
                } else {
                    _loginErrorMessage.value = "Login incorrecto"
                }
            }
        } else {
            _passwordError.value = "Mínimo 6 dígitos"
        }
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false
    }

    fun onErrorMessageShown() {
        _loginErrorMessage.value = null
    }
}