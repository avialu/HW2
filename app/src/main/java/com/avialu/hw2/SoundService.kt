package com.avialu.hw2

import android.media.AudioManager
import android.media.ToneGenerator

class SoundService {

    private val tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)

    fun coin() {
        tone.startTone(ToneGenerator.TONE_PROP_ACK, 120)
    }

    fun crash() {
        tone.startTone(ToneGenerator.TONE_SUP_ERROR, 350)
    }

    fun release() {
        tone.release()
    }
}
