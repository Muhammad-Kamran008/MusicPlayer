package com.example.musicplayer.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.musicplayer.R
import com.example.musicplayer.utils.Constants

class MusicService : Service() {
    private val NOTIFICATION_ID = 1
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == Constants.ACTION.START_FOREGROUND_ACTION) {
            startForeground(NOTIFICATION_ID, createNotification())
            startMusic()

        } else if (intent.action == Constants.ACTION.STOP_FOREGROUND_ACTION) {
            stopForeground(true)
            stopMusic()

        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlaying) {
            stopMusic()
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(this, MusicService::class.java)
        stopIntent.action = Constants.ACTION.STOP_FOREGROUND_ACTION


        val pendingStopIntent: PendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val builder = NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Music Player")
            .setContentText("Playing music")
            .setSmallIcon(R.drawable.music_note)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(R.drawable.stop_icon, "Stop", pendingStopIntent)
        return builder.build()
    }


    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        mediaPlayer.isLooping = true

    }


    private fun startMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            isPlaying = true
        }
    }
    private fun stopMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
            isPlaying = false
        }
    }

}


