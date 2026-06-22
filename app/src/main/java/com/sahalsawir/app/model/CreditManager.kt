package com.sahalsawir.app.model

import android.content.Context
import java.util.concurrent.TimeUnit

object CreditManager {
    private const val PREFS_NAME = "sahal_sawir_prefs"
    private const val KEY_CREDITS = "credits_remaining"
    private const val KEY_FIRST_USE = "first_use_timestamp"
    private const val KEY_PREMIUM = "is_premium_active"
    private const val MAX_DAILY_CREDITS = 3
    private const val RESET_WINDOW_MILLIS = 24L * 60L * 60L * 1000L

    fun isPremium(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_PREMIUM, false)
    }

    fun setPremium(context: Context, status: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_PREMIUM, status).apply()
    }

    fun getRemainingCredits(context: Context): Int {
        if (isPremium(context)) return 9999
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val firstUse = prefs.getLong(KEY_FIRST_USE, 0L)
        val currentTime = System.currentTimeMillis()
        if (firstUse > 0L && (currentTime - firstUse) >= RESET_WINDOW_MILLIS) {
            prefs.edit()
                .putInt(KEY_CREDITS, MAX_DAILY_CREDITS)
                .putLong(KEY_FIRST_USE, 0L)
                .apply()
            return MAX_DAILY_CREDITS
        }
        return prefs.getInt(KEY_CREDITS, MAX_DAILY_CREDITS)
    }

    fun consumeCredit(context: Context): Boolean {
        if (isPremium(context)) return true
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currentCredits = getRemainingCredits(context)
        if (currentCredits <= 0) return false
        val newCredits = currentCredits - 1
        val edit = prefs.edit().putInt(KEY_CREDITS, newCredits)
        if (currentCredits == MAX_DAILY_CREDITS) {
            edit.putLong(KEY_FIRST_USE, System.currentTimeMillis())
        }
        edit.apply()
        return true
    }

    fun getTimeToReset(context: Context): Long {
        if (isPremium(context)) return 0L
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val firstUse = prefs.getLong(KEY_FIRST_USE, 0L)
        if (firstUse == 0L) return 0L
        val elapsed = System.currentTimeMillis() - firstUse
        val remaining = RESET_WINDOW_MILLIS - elapsed
        return if (remaining < 0) 0L else remaining
    }

    fun formatRemainingTime(millis: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
