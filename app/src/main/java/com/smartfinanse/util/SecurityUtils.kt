package com.smartfinanse.util

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object SecurityUtils {

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        // For Android API < 26 we might need android.util.Base64, but since minSdk is 26, java.util.Base64 is fine
        return Base64.getEncoder().encodeToString(saltBytes)
    }

    fun hashPin(pin: String, salt: String): String {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        val input = (pin + salt).toByteArray()
        val hashBytes = messageDigest.digest(input)
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}
