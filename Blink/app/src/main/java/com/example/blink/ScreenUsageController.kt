package com.example.blink

import android.content.Context

object ScreenUsageController {

    // ===== CONFIG =====
    private const val MAX_ON_MS = 1 * 20 * 1000L   // 40 minutes
    private const val RESET_OFF_MS = 1 * 10 * 1000L // 2 minutes

    // ===== STATE =====
    private var accumulatedOnMs = 0L
    private var lastOnAt = 0L
    private var lastOffAt = 0L

    private var screenOn = false
    private var inPenalty = false

    // ===== EVENTS =====

    /** Call when screen turns ON */
    fun onScreenOn(now: Long = System.currentTimeMillis()): Result {
        // If coming from OFF and not in penalty, check if rest was enough
        if (!screenOn && !inPenalty && lastOffAt != 0L) {
            val offDuration = now - lastOffAt
            if (offDuration >= RESET_OFF_MS) {
                resetUsage()
            }
        }

        screenOn = true
        lastOnAt = now

        return if (inPenalty) Result.ENFORCE_LOCK else Result.NO_ACTION
    }

    /** Call when screen turns OFF */
    fun onScreenOff(now: Long = System.currentTimeMillis()): Result {
        if (screenOn) {
            accumulatedOnMs += now - lastOnAt
        }

        screenOn = false
        lastOffAt = now

        // If we just exceeded limit → enter penalty
        if (!inPenalty && accumulatedOnMs >= MAX_ON_MS) {
            inPenalty = true
            return Result.ENFORCE_LOCK
        }

        return Result.NO_ACTION
    }

    /** Call periodically while screen is ON (e.g., every second) */
    fun checkWhileOn(now: Long = System.currentTimeMillis()): Result {
        if (!screenOn) return Result.NO_ACTION

        val currentUsage = accumulatedOnMs + (now - lastOnAt)

        return if (!inPenalty && currentUsage >= MAX_ON_MS) {
            inPenalty = true
            Result.ENFORCE_LOCK
        } else if (inPenalty) {
            Result.ENFORCE_LOCK
        } else {
            Result.NO_ACTION
        }
    }

    /** Call periodically while screen is OFF to see if break completed */
    fun checkWhileOff(now: Long = System.currentTimeMillis()): Result {
        if (!inPenalty || lastOffAt == 0L) return Result.NO_ACTION

        val offDuration = now - lastOffAt
        return if (offDuration >= RESET_OFF_MS) {
            inPenalty = false
            resetUsage()
            Result.BREAK_COMPLETED
        } else {
            Result.NO_ACTION
        }
    }

    // ===== HELPERS =====

    private fun resetUsage() {
        accumulatedOnMs = 0L
        lastOnAt = 0L
        lastOffAt = 0L
    }


    fun getUsageSeconds(now: Long = System.currentTimeMillis()): Long {
        val total = if (screenOn) {
            accumulatedOnMs + (now - lastOnAt)
        } else {
            accumulatedOnMs
        }
        return total / 1000
    }

    fun isInPenalty(): Boolean = inPenalty

    enum class Result {
        NO_ACTION,
        ENFORCE_LOCK,
        BREAK_COMPLETED
    }
}
