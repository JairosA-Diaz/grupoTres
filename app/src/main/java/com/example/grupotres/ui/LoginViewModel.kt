package com.example.grupotres.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupotres.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val repository: UserRepository) : ViewModel() {

    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError

    private val _isLoginEnabled = MutableLiveData<Boolean>(false)
    val isLoginEnabled: LiveData<Boolean> = _isLoginEnabled

    private val _authErrorMessage = MutableLiveData<String?>()
    val authErrorMessage: LiveData<String?> = _authErrorMessage

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
                val user = repository.login(email, password)
                if (user != null) {
                    _navigateToHome.value = true
                } else {
                    _authErrorMessage.value = "Login incorrecto"
                }
            }
        }
    }

    fun onRegisterClicked(email: String, password: String) {
        if (password.length >= 6) {
            viewModelScope.launch {
                val user = repository.register(email, password)
                if (user != null) {
                    _navigateToHome.value = true
                } else {
                    _authErrorMessage.value = "Error en el registro"
                }
            }
        }
    }

    fun onNavigatedToHome() {
        _navigateToHome.value = false
    }

    fun onErrorMessageShown() {
        _authErrorMessage.value = null
    }
}