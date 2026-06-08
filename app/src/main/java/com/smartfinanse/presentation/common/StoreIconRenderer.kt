package com.smartfinanse.presentation.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun StoreIconRenderer(
    iconName: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resId = remember(iconName) {
        context.resources.getIdentifier(iconName, "drawable", context.packageName)
    }

    if (resId != 0) {
        // Obrazek wektorowy pobrany z drawable (np. logo biedronki)
        Icon(
            painter = painterResource(id = resId),
            contentDescription = null,
            tint = Color.Unspecified, // Zachowujemy oryginalne kolory logo
            modifier = modifier
        )
    } else {
        // Próba załadowania z Material Icons
        Icon(
            imageVector = getMaterialIconByName(iconName) ?: Icons.Default.Store,
            contentDescription = null,
            tint = tint,
            modifier = modifier
        )
    }
}
