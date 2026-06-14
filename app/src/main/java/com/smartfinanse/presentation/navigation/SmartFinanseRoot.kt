package com.smartfinanse.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smartfinanse.presentation.auth.AuthState
import com.smartfinanse.presentation.auth.AuthViewModel
import com.smartfinanse.presentation.auth.LoginPinScreen
import com.smartfinanse.presentation.auth.SetPinScreen
import com.smartfinanse.presentation.auth.components.ResetDataDialog

@Composable
fun SmartFinanseRoot(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    when (val state = authState) {
        is AuthState.Loading -> {
            Box(modifier = Modifier.fillMaxSize())
        }
        is AuthState.SetupPin -> {
            SetPinScreen(onPinSetup = { authViewModel.setupPin(it) })
        }
        is AuthState.RequirePin -> {
            var showResetDialog by remember { mutableStateOf(false) }

            if (showResetDialog) {
                ResetDataDialog(
                    onConfirm = {
                        showResetDialog = false
                        authViewModel.clearAllDataAndReset {}
                    },
                    onDismiss = { showResetDialog = false }
                )
            }

            LoginPinScreen(
                lockoutEndTime = state.lockoutEndTime,
                failedAttempts = state.failedAttempts,
                onVerifyPin = { authViewModel.verifyPin(it) {} },
                onForgotPinClick = { showResetDialog = true }
            )
        }
        is AuthState.Authenticated -> {
            SmartFinanseNavHost()
        }
    }
}
