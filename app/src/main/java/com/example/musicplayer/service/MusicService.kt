package com.example.musicplayer.service
import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.musicplayer.R
import com.example.musicplayer.utils.Constants

class MusicService : Service() {
    private val NOTIFICATION_ID = 1
    private lateinit var mediaPlayer: MediaPlayer
    private var isPlaying = false
    private var isPaused = false
    private var currentAudioIndex = 0

    private val audioResources = listOf(
        R.raw.alarm_sound,
        R.raw.jingle,
        R.raw.stock_tune
    )
    private val audioTitles = listOf("Alarm Sound", "Jingle", "Stock Tune")
    private val audioDescriptions =
        listOf("Audio 1", "Audio 2", "Audio 3")

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        when (intent.action) {
            Constants.ACTION.START_FOREGROUND_ACTION -> {
                startForeground(NOTIFICATION_ID, createNotification())
                startMusic()
            }

            Constants.ACTION.STOP_FOREGROUND_ACTION -> {
                stopForeground(true)
                stopMusic()
            }

            Constants.ACTION.PAUSE_ACTION -> {
                pauseMusic()
            }

            Constants.ACTION.RESUME_ACTION -> {
                resumeMusic()
            }

            Constants.ACTION.FORWARD_ACTION -> {
                forwardMusic()
            }

            Constants.ACTION.BACKWARD_ACTION -> {
                backwardMusic()
            }
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
        val stopIntent = Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION.STOP_FOREGROUND_ACTION
        }
        val pendingStopIntent = createPendingIntent(stopIntent)

        val pauseIntent = Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION.PAUSE_ACTION
        }
        val pendingPauseIntent = createPendingIntent(pauseIntent)

        val resumeIntent = Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION.RESUME_ACTION
        }
        val pendingResumeIntent = createPendingIntent(resumeIntent)

        val forwardIntent = Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION.FORWARD_ACTION
        }
        val pendingForwardIntent = createPendingIntent(forwardIntent)

        val backwardIntent = Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION.BACKWARD_ACTION
        }
        val pendingBackwardIntent = createPendingIntent(backwardIntent)

        val notificationLayout =
            RemoteViews(packageName, R.layout.music_player_notification).apply {

                setTextViewText(R.id.name, getCurrentAudioTitle())
                setTextViewText(R.id.channel, getCurrentAudioDescription())

                setOnClickPendingIntent(R.id.ivCancel, pendingStopIntent)
                setOnClickPendingIntent(R.id.ivPrev, pendingBackwardIntent)
                setOnClickPendingIntent(R.id.ivNext, pendingForwardIntent)
                //  setViewVisibility(R.id.ivPlayPause, if (isPlaying) View.VISIBLE else View.GONE)

                if (isPaused) {
                    setImageViewResource(R.id.ivPlayPause, R.drawable.play)
                    setOnClickPendingIntent(R.id.ivPlayPause, pendingResumeIntent)
                } else {
                    setImageViewResource(R.id.ivPlayPause, R.drawable.pause)
                    setOnClickPendingIntent(R.id.ivPlayPause, pendingPauseIntent)
                }
            }

        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setCustomContentView(notificationLayout)
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.music_note)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOngoing(true)
            .build()
    }


    private fun createPendingIntent(intent: Intent): PendingIntent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    private fun startMusic() {
        if (audioResources.isNotEmpty()) {
            if (::mediaPlayer.isInitialized) {
                mediaPlayer.release()
            }
            mediaPlayer = MediaPlayer.create(this, audioResources[currentAudioIndex])
            mediaPlayer.isLooping = false
            mediaPlayer.setOnCompletionListener {
                forwardMusic()
            }
            mediaPlayer.start()
            isPlaying = true
            isPaused = false
        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            isPlaying = true
            isPaused = true
            updateNotification()
        }
    }

    private fun resumeMusic() {
        if (!mediaPlayer.isPlaying && isPaused) {
            mediaPlayer.start()
            isPlaying = true
            isPaused = false
            updateNotification()
        }
    }


    private fun forwardMusic() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        if (audioResources.isNotEmpty()) {
            currentAudioIndex = (currentAudioIndex + 1) % audioResources.size
            startMusic()
        }
        updateNotification()
    }

    private fun backwardMusic() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        if (audioResources.isNotEmpty()) {
            currentAudioIndex = (currentAudioIndex - 1 + audioResources.size) % audioResources.size
            startMusic()
        }
        updateNotification()
    }


    private fun stopMusic() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        isPlaying = false
        isPaused = false
        //  updateNotification()
    }

    private fun updateNotification() {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun getCurrentAudioTitle(): String {
        return audioTitles[currentAudioIndex]
    }

    private fun getCurrentAudioDescription(): String {
        return audioDescriptions[currentAudioIndex]
    }
}


