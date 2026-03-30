package com.rff.boingballdemo.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.rff.boingballdemo.audio.BoingBallAudioPlayer
import org.koin.compose.getKoin

@Composable
fun rememberBoingBallAudio(): BoingBallAudioPlayer {
    val koin = getKoin()
    return remember(koin) { koin.get<BoingBallAudioPlayer>() }
}
