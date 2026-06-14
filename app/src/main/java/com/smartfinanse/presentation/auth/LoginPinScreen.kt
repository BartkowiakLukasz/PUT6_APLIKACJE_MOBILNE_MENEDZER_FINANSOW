package com.smartfinanse.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartfinanse.presentation.auth.components.PinDots
import com.smartfinanse.presentation.auth.components.PinKeypad
import kotlinx.coroutines.delay

@Composable
fun LoginPinScreen(
    lockoutEndTime: Long,
    failedAttempts: Int,
    onVerifyPin: (String) -> Unit,
    onForgotPinClick: () -> Unit
) {
    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var lockoutSecondsRemaining by remember { mutableStateOf(0L) }

    LaunchedEffect(lockoutEndTime) {
        while (true) {
            val now = System.currentTimeMillis()
            if (lockoutEndTime > now) {
                lockoutSecondsRemaining = (lockoutEndTime - now) / 1000
                delay(1000)
            } else {
                lockoutSecondsRemaining = 0L
                break
            }
        }
    }

    LaunchedEffect(isError) {
        if (isError) {
            delay(500)
            isError = false
            enteredPin = ""
        }
    }
    
    LaunchedEffect(failedAttempts) {
        if (failedAttempts > 0 && enteredPin.length == 4) {
            isError = true
        }
    }

    LaunchedEffect(enteredPin) {
        if (enteredPin.length == 4 && !isError && lockoutSecondsRemaining == 0L) {
            delay(300)
            onVerifyPin(enteredPin)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "Podaj kod PIN",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        PinDots(
            pinLength = 4,
            currentLength = enteredPin.length,
            isError = isError
        )

        Spacer(modifier = Modifier.height(16.dp))
        if (lockoutSecondsRemaining > 0) {
            Text(
                text = "Zbyt wiele błędnych prób.\nSpróbuj ponownie za ${lockoutSecondsRemaining}s.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else if (isError) {
            Text(
                text = "Błędny kod PIN.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        } else {
            Text(text = " ", style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(modifier = Modifier.weight(1f))

        PinKeypad(
            onNumberClick = { number ->
                if (isError || lockoutSecondsRemaining > 0) return@PinKeypad
                if (enteredPin.length < 4) {
                    enteredPin += number
                }
            },
            onBackspaceClick = {
                if (isError || lockoutSecondsRemaining > 0) return@PinKeypad
                if (enteredPin.isNotEmpty()) {
                    enteredPin = enteredPin.dropLast(1)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onForgotPinClick) {
            Text(text = "Zapomniałem kodu PIN")
        }
    }
}
