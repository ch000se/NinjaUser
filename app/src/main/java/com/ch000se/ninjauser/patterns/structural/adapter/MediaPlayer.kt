package com.ch000se.ninjauser.patterns.structural.adapter

interface MediaPlayer {
    fun play(fileName: String)
}

class AudioLib {
    fun playAudio(file: String) {
        println("AudioLib playing: $file")
    }
}

// fun AudioLib.play(fileName: String) = playAudio(fileName)

class AudioLibAdapter(private val audioLib: AudioLib) : MediaPlayer {

    override fun play(fileName: String) {
        audioLib.playAudio(fileName)
    }
}


fun main() {
    val audioLib = AudioLib()

//    audioLib.play("song.mp3")
    val player: MediaPlayer = AudioLibAdapter(audioLib)

    player.play("song.mp3")
}