package com.example.musicplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.service.MusicService
import com.example.musicplayer.utils.Constants
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val PERMISSION_REQ_NOTIFICATION = 100
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.start.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                syncNotification()
            } else {
                startMusicService()
            }
        }

        binding.stop.setOnClickListener {
            val intent = Intent(this, MusicService::class.java)
            intent.action = Constants.ACTION.STOP_FOREGROUND_ACTION
            startService(intent)

        }

    }

    private fun startMusicService() {
        val intent = Intent(this, MusicService::class.java).apply {
            action = Constants.ACTION.START_FOREGROUND_ACTION
        }
        startService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun syncNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), PERMISSION_REQ_NOTIFICATION
            )
        } else {
            startMusicService()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_NOTIFICATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMusicService()

            } else {
                Snackbar.make(
                    this,
                    binding.root,
                    "Permission is Not Granted",
                    Snackbar.LENGTH_SHORT
                )
                    .show()
            }
        }
    }
}

