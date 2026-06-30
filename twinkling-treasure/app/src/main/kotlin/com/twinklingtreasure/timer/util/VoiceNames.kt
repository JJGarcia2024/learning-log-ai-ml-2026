package com.twinklingtreasure.timer.util

import android.speech.tts.Voice
import java.util.Locale

/**
 * Maps Android TTS [Voice] objects (which have cryptic technical names like
 * "en-us-x-iol-local") to deterministic, human-friendly display names. The same
 * [Voice.name] always maps to the same adjective, since String.hashCode() is a stable,
 * specified algorithm — not subject to per-process randomization.
 */
object VoiceNames {

    private val ADJECTIVES = listOf(
        "Buttery", "Airy", "Mellow", "Glassy", "Rounded", "Warm", "Crisp",
        "Bright", "Calm", "Deep", "Soft", "Bold", "Silky", "Velvet", "Clear",
        "Gentle", "Rich", "Smooth", "Light", "Sunny",
    )

    fun friendlyName(voice: Voice): String =
        ADJECTIVES[Math.floorMod(voice.name.hashCode(), ADJECTIVES.size)]

    /** e.g. "English (US) · On-device" or "English (US) · Network". */
    fun subtitle(voice: Voice): String {
        val localeName = voice.locale.getDisplayName(Locale.getDefault())
        val connectivity = if (voice.isNetworkConnectionRequired) "Network" else "On-device"
        return "$localeName · $connectivity"
    }
}
