package com.smartfinanse.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material.icons.rounded.Star as RoundedStar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CategoryIconRenderer(
    iconName: String,
    colorHex: String,
    modifier: Modifier = Modifier
) {
    val fallback = MaterialTheme.colorScheme.onSurfaceVariant
    val color = parseCategoryHexColor(colorHex, fallback)

    Box(
        modifier = modifier
            .background(color = color.copy(alpha = 0.2f), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (iconName.startsWith("file://") || iconName.startsWith("content://")) {
            AsyncImage(
                model = iconName,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
                    .clip(CircleShape)
            )
        } else {
            val vector = getMaterialIconByName(iconName)
            Icon(
                imageVector = vector,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

fun parseCategoryHexColor(hex: String, fallback: Color): Color {
    return try {
        Color(android.graphics.Color.parseColor(hex))
    } catch (e: Exception) {
        fallback
    }
}

fun getMaterialIconByName(name: String): ImageVector {
    return when (name) {
        "ic_food" -> Icons.Default.ShoppingCart
        "ic_transport" -> Icons.Rounded.DirectionsCar
        "ic_health" -> Icons.Default.Favorite
        "ic_bills" -> Icons.Rounded.ReceiptLong
        "ic_entertainment" -> Icons.Rounded.RoundedStar
        "ic_other" -> Icons.Default.Build
        "ic_shopping" -> Icons.Default.ShoppingCart
        "ic_home" -> Icons.Default.Home
        "ic_work" -> Icons.Default.Work
        "ic_star" -> Icons.Default.Star
        else -> Icons.Rounded.RoundedStar
    }
}
