package com.smartfinanse.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.smartfinanse.presentation.auth.components.PinDots
import com.smartfinanse.presentation.auth.components.PinKeypad
import kotlinx.coroutines.delay

@Composable
fun ChangePinScreen(
    onVerifyOldPin: (String, (Boolean) -> Unit) -> Unit,
    onPinSetup: (String) -> Unit
) {
    var step by remember { mutableStateOf(0) } // 0: Old PIN, 1: New PIN, 2: Confirm New PIN
    var enteredPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(isError) {
        if (isError) {
            delay(1000)
            isError = false
            if (step == 0) {
                enteredPin = ""
            } else {
                confirmPin = ""
                enteredPin = ""
                step = 1
            }
        }
    }

    LaunchedEffect(enteredPin) {
        if (!isError) {
            if (step == 0 && enteredPin.length == 4) {
                delay(300)
                onVerifyOldPin(enteredPin) { isCorrect ->
                    if (isCorrect) {
                        step = 1
                        enteredPin = ""
                    } else {
                        isError = true
                    }
                }
            } else if (step == 1 && enteredPin.length == 4) {
                delay(300)
                step = 2
            }
        }
    }

    LaunchedEffect(confirmPin) {
        if (step == 2 && confirmPin.length == 4 && !isError) {
            if (confirmPin == enteredPin) {
                delay(300)
                onPinSetup(confirmPin)
            } else {
                isError = true
            }
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
            text = when (step) {
                0 -> "Podaj stary kod PIN"
                1 -> "Ustaw nowy kod PIN"
                else -> "Potwierdź nowy kod PIN"
            },
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        PinDots(
            pinLength = 4,
            currentLength = if (step == 2) confirmPin.length else enteredPin.length,
            isError = isError
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        if (isError) {
            Text(
                text = if (step == 0) "Błędny stary kod PIN." else "Kody PIN nie pasują.",
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
                if (isError) return@PinKeypad
                if (step == 0) {
                    if (enteredPin.length < 4) {
                        enteredPin += number
                    }
                } else if (step == 1) {
                    if (enteredPin.length < 4) {
                        enteredPin += number
                    }
                } else {
                    if (confirmPin.length < 4) {
                        confirmPin += number
                    }
                }
            },
            onBackspaceClick = {
                if (isError) return@PinKeypad
                if (step == 0 || step == 1) {
                    if (enteredPin.isNotEmpty()) enteredPin = enteredPin.dropLast(1)
                } else {
                    if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                    else {
                        step = 1
                        enteredPin = enteredPin.dropLast(1)
                    }
                }
            }
        )
    }
}
