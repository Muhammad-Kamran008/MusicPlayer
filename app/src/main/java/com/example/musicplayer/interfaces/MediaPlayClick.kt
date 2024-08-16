package com.example.musicplayer.interfaces

//this callback will be fired to update ui from service class
interface MediaPlayerClick{
    fun onPrepared()
    fun onPlayed()
    fun onPause()
    fun onDestroyed()
    fun onNext()
    fun onPrev()
    fun onFailed()
    fun onResumeUi()
}

