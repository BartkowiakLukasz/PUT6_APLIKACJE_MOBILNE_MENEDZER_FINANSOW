package com.smartfinanse.presentation.ads

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AdGatekeeperScreen(
    viewModel: AdGatekeeperViewModel = hiltViewModel(),
    onNavigateToDashboard: @Composable () -> Unit
) {
    val state by viewModel.gatekeeperState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    
    when (state) {
        is AdGatekeeperState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is AdGatekeeperState.ShowAd -> {
            LaunchedEffect(Unit) {
                val activity = context as? Activity
                if (activity != null) {
                    viewModel.adManager.showInterstitialAd(activity) {
                        viewModel.onAdFinished()
                    }
                } else {
                    viewModel.onAdFinished()
                }
            }
            
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is AdGatekeeperState.BypassAd -> {
            onNavigateToDashboard()
        }
    }
}
