package com.example.animalshelter.util

import android.content.Context
import android.media.MediaPlayer
import com.example.animalshelter.R

object SoundHelper {

    private var player: MediaPlayer? = null

    fun playPetSound(context: Context, type: String) {
        android.util.Log.d("SoundHelper", "type=$type")
        val resId = when (type) {
            "dog" -> R.raw.dog_bark
            "rabbit" -> R.raw.rabbit_hr
            "goat" -> R.raw.goat_bleat
            else -> {
                android.util.Log.d("SoundHelper", "unknown type")
                return
            }
        }
        android.util.Log.d("SoundHelper", "resId=$resId")

        try {
            player?.release()
            player = MediaPlayer.create(context, resId)?.apply {
                setOnCompletionListener { it.release() }
                start()
            }
            android.util.Log.d("SoundHelper", "playing")
        } catch (e: Exception) {
            android.util.Log.e("SoundHelper", "error: ${e.message}", e)
        }
    }
}