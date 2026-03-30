package com.rff.boingballdemo.audio

import boingballdemo.composeapp.generated.resources.Res
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.Foundation.NSURL

@OptIn(ExperimentalForeignApi::class)
actual class BoingBallAudioPlayer {
    private val bounce = Res.getUri(BOUNCE_AUDIO_FILE_PATH).let { path ->
        NSURL.URLWithString(URLString = path)
    }

    actual fun play() {
        val avAudioPlayer = AVAudioPlayer(bounce!!, error = null)
        avAudioPlayer.prepareToPlay()
        avAudioPlayer.play()
    }

    actual fun playLeft() {
        play()
    }

    actual fun playRight() {
        play()
    }

    actual fun release() {
    }
}
