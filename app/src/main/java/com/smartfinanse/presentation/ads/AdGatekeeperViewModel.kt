package com.smartfinanse.presentation.ads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartfinanse.domain.manager.AdManager
import com.smartfinanse.domain.repository.AdPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

sealed class AdGatekeeperState {
    object Loading : AdGatekeeperState()
    object ShowAd : AdGatekeeperState()
    object BypassAd : AdGatekeeperState()
}

@HiltViewModel
class AdGatekeeperViewModel @Inject constructor(
    val adManager: AdManager,
    private val adPreferencesRepository: AdPreferencesRepository
) : ViewModel() {

    private val _gatekeeperState = MutableStateFlow<AdGatekeeperState>(AdGatekeeperState.Loading)
    val gatekeeperState: StateFlow<AdGatekeeperState> = _gatekeeperState.asStateFlow()

    init {
        checkAdLimit()
    }

    private fun checkAdLimit() {
        viewModelScope.launch {
            val lastAdDate = adPreferencesRepository.lastAdShownDate.firstOrNull()
            val today = LocalDate.now().toString()

            if (lastAdDate == today) {
                _gatekeeperState.value = AdGatekeeperState.BypassAd
            } else {
                _gatekeeperState.value = AdGatekeeperState.ShowAd
            }
        }
    }

    fun onAdFinished() {
        viewModelScope.launch {
            val today = LocalDate.now().toString()
            adPreferencesRepository.setLastAdShownDate(today)
            _gatekeeperState.value = AdGatekeeperState.BypassAd
        }
    }
}
