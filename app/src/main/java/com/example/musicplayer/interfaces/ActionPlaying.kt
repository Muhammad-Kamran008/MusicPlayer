package com.example.musicplayer.interfaces

//this callback will be fired from ui to services
interface ActionPlaying {
    fun nextClicked()
    fun prvClicked()
    fun playClicked()
}

