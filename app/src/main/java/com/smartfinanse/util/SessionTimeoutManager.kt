package com.smartfinanse.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionTimeoutManager @Inject constructor() : DefaultLifecycleObserver {
    private var lastBackgroundTime: Long = 0L
    private val TIMEOUT_MILLIS = 2 * 60 * 1000L // 2 minutes

    private val _sessionExpiredEvent = kotlinx.coroutines.flow.MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: kotlinx.coroutines.flow.SharedFlow<Unit> = _sessionExpiredEvent

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        lastBackgroundTime = System.currentTimeMillis()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (lastBackgroundTime > 0) {
            val timeInBackground = System.currentTimeMillis() - lastBackgroundTime
            if (timeInBackground >= TIMEOUT_MILLIS) {
                _sessionExpiredEvent.tryEmit(Unit)
            }
            lastBackgroundTime = 0L
        }
    }
}
