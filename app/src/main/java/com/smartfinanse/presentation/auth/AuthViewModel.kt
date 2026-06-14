package com.smartfinanse.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.repository.SecurityPreferencesRepository
import com.smartfinanse.util.SecurityUtils
import com.smartfinanse.util.SessionTimeoutManager
import com.smartfinanse.domain.usecase.SeedCategoriesUseCase
import com.smartfinanse.domain.usecase.SeedStoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Loading : AuthState()
    object SetupPin : AuthState()
    data class RequirePin(val failedAttempts: Int, val lockoutEndTime: Long) : AuthState()
    object Authenticated : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val securityRepository: SecurityPreferencesRepository,
    private val seedCategoriesUseCase: SeedCategoriesUseCase,
    private val seedStoresUseCase: SeedStoresUseCase,
    sessionTimeoutManager: SessionTimeoutManager
) : ViewModel() {

    private val _isUserAuthenticated = MutableStateFlow(false)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Listen for session timeout
        sessionTimeoutManager.sessionExpiredEvent
            .onEach {
                _isUserAuthenticated.value = false
            }
            .launchIn(viewModelScope)

        // Combine all relevant state to determine current AuthState
        combine(
            securityRepository.hasPin,
            securityRepository.failedAttempts,
            securityRepository.lockoutEndTime,
            _isUserAuthenticated
        ) { hasPin, failedAttempts, lockoutEndTime, isAuthenticated ->
            when {
                isAuthenticated -> AuthState.Authenticated
                !hasPin -> AuthState.SetupPin
                else -> AuthState.RequirePin(failedAttempts, lockoutEndTime)
            }
        }.onEach { state ->
            _authState.value = state
        }.launchIn(viewModelScope)
    }

    fun setupPin(pin: String) {
        viewModelScope.launch {
            val salt = SecurityUtils.generateSalt()
            val hash = SecurityUtils.hashPin(pin, salt)
            securityRepository.savePin(hash, salt)
            _isUserAuthenticated.value = true
        }
    }

    fun verifyPin(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val (savedHash, savedSalt) = securityRepository.getPinData()
            if (savedHash == null || savedSalt == null) {
                onResult(false)
                return@launch
            }

            val inputHash = SecurityUtils.hashPin(pin, savedSalt)
            if (inputHash == savedHash) {
                securityRepository.resetFailedAttempts()
                _isUserAuthenticated.value = true
                onResult(true)
            } else {
                securityRepository.incrementFailedAttempts()
                onResult(false)
            }
        }
    }
    
    fun clearAllDataAndReset(onComplete: () -> Unit) {
        viewModelScope.launch {
            securityRepository.clearAllData()
            
            // Re-initialize default categories and stores after a hard reset
            seedCategoriesUseCase()
            seedStoresUseCase()
            
            _isUserAuthenticated.value = false
            onComplete()
        }
    }
}
