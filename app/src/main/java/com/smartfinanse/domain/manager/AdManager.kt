package com.smartfinanse.domain.manager

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor() {
    private var interstitialAd: InterstitialAd? = null
    private var isAdLoading = false

    fun loadInterstitialAd(context: Context) {
        if (interstitialAd != null || isAdLoading) {
            return
        }

        isAdLoading = true
        val adRequest = AdRequest.Builder().build()
        // Test Ad Unit ID for Interstitial
        val adUnitId = "ca-app-pub-3940256099942544/1033173712"

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    isAdLoading = false
                    Log.e("AdManager", "Failed to load interstitial ad: ${adError.message}")
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isAdLoading = false
                    Log.d("AdManager", "Interstitial ad loaded successfully")
                }
            }
        )
    }

    fun showInterstitialAd(activity: Activity, onDismissed: () -> Unit) {
        if (interstitialAd == null) {
            onDismissed()
            return
        }

        var callbackTriggered = false

        // Timeout to prevent callback deadlock if AdMob SDK fails to call onAdShowed or onAdFailed
        val timeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(5000L) 
            if (!callbackTriggered) {
                callbackTriggered = true
                Log.w("AdManager", "Ad callback timeout! Forcing dismiss.")
                interstitialAd = null
                onDismissed()
            }
        }

        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                if (!callbackTriggered) {
                    callbackTriggered = true
                    timeoutJob.cancel()
                    onDismissed()
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                if (!callbackTriggered) {
                    callbackTriggered = true
                    timeoutJob.cancel()
                    onDismissed()
                }
            }
            
            override fun onAdShowedFullScreenContent() {
                // Reklama faktycznie wyświetliła się użytkownikowi - nie używamy timeoutu 
                // na jej zamknięcie przez użytkownika, anulujemy go.
                timeoutJob.cancel()
            }
        }

        interstitialAd?.show(activity)
    }
}
