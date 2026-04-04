package com.achtsamkeit.tagebuch.presentation.security

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.achtsamkeit.tagebuch.core.security.BiometricAuthenticator
import com.achtsamkeit.tagebuch.domain.repository.SecurityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val securityRepository: SecurityRepository,
    private val biometricAuthenticator: BiometricAuthenticator
) : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(false)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            _isBiometricEnabled.value = securityRepository.isBiometricEnabled.first()
            // Falls Biometrie deaktiviert ist, direkt als authentifiziert markieren
            if (!_isBiometricEnabled.value) {
                _isAuthenticated.value = true
            }
        }
    }

    fun authenticate(activity: FragmentActivity) {
        if (!biometricAuthenticator.isBiometricAvailable(activity)) {
            _isAuthenticated.value = true
            return
        }

        biometricAuthenticator.promptAuthenticate(
            activity = activity,
            title = "Achtsamkeitstagebuch entsperren",
            subtitle = "Bitte authentifiziere dich, um deine Einträge zu sehen",
            onSuccess = { _ ->
                _isAuthenticated.value = true
            },
            onError = { _, _ ->
                // Fehlerbehandlung (z.B. Abbruch durch Nutzer)
            },
            onFailed = {
                // Fehlgeschlagen
            }
        )
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            securityRepository.setBiometricEnabled(enabled)
            _isBiometricEnabled.value = enabled
        }
    }
}
