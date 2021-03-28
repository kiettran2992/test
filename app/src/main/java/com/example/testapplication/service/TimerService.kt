package com.example.testapplication.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.*
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.testapplication.utils.PreferencesManager


class TimerService : Service() {

    private val TAG = "TimerService"

    companion object {
        fun countDownBackground(): String {
            return "com.example.testapplication.countdown.background"
        }
    }

    var mIntent = Intent(countDownBackground())

    var milliseconds: Long = 0

    private var countDownTimer: CountDownTimer? = null

    private fun startCountDownTimer() {
        Log.i(TAG, "Starting timer...")

        countDownTimer = object : CountDownTimer(milliseconds, 100) {
            override fun onTick(millisUntilFinished: Long) {
                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000)
                PreferencesManager.saveCountDownTime(millisUntilFinished,applicationContext)
                sendBroadcast(mIntent)
            }

            override fun onFinish() {
                Log.i(TAG, "Timer finished")
                // Set vibration
                vibrate()

                // Set play sound
                playSound()

                PreferencesManager.saveIsFinished(true,applicationContext)
                sendBroadcast(mIntent)
            }
        }

        (countDownTimer as CountDownTimer).start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null && intent.extras != null) {
            milliseconds = intent.getLongExtra("totalTime", 0)
            startCountDownTimer()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.i(TAG, "Timer cancelled")
        countDownTimer?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun playSound() {
        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val mediaPlayer: MediaPlayer = MediaPlayer.create(applicationContext, alarmSound)
        mediaPlayer.start()
    }

    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(200)
        }
    }
}