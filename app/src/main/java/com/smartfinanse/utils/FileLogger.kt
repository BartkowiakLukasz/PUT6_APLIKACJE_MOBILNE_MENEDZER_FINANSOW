package com.smartfinanse.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.google.firebase.crashlytics.FirebaseCrashlytics

object FileLogger {
    private var logFile: File? = null

    fun init(context: Context) {
        logFile = File(context.getExternalFilesDir(null), "scanner_errors.log")
    }

    fun logError(tag: String, message: String, e: Throwable? = null) {
        try {
            // Log to logcat for local USB debugging
            if (e != null) {
                android.util.Log.e(tag, message, e)
            } else {
                android.util.Log.d(tag, message)
            }

            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val stacktrace = e?.stackTraceToString() ?: ""
            val logMessage = "[$timestamp] $tag: $message\n$stacktrace\n----------------------------------------\n"
            logFile?.appendText(logMessage)
            
            // Send to Crashlytics
            val crashlytics = FirebaseCrashlytics.getInstance()
            crashlytics.log("$tag: $message")
            if (e != null) {
                crashlytics.recordException(e)
            } else {
                crashlytics.recordException(Exception("$tag: $message"))
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}
