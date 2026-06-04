package com.smartfinanse.presentation.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Top app bar for screens inside [com.smartfinanse.presentation.navigation.SmartFinanseNavHost].
 * Status bar inset is applied once at the nav host root — this bar uses zero window insets
 * to avoid double top padding under edge-to-edge.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartFinanseTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors()
) {
    TopAppBar(
        title = title,
        modifier = modifier,
        navigationIcon = navigationIcon,
        actions = actions,
        colors = colors,
        windowInsets = WindowInsets(0)
    )
}
