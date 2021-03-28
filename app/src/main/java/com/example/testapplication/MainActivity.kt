package com.example.testapplication

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.testapplication.service.TimerService
import com.example.testapplication.utils.PreferencesManager
import com.example.testapplication.utils.StringUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private var hour: Int = 0
    private var minute: Int = 0
    private var second: Int = 0
    private var intentService: Intent? = null
    private var totalMilliseconds: Long = 0
    private var state = TimerState.CANCELED

    enum class TimerState {
        PAUSED, STARTED, CANCELED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set status bar color
        val window = window
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        setContentView(R.layout.activity_main)
        PreferencesManager.clearPreference(applicationContext)
        setupView()
        setupListener()
    }

    private fun setupListener() {
        cancelBtn.setOnClickListener {
            processCancelTimer()
        }

        startBtn.setOnClickListener {
            when(state) {
                TimerState.STARTED -> {
                    processPauseTimer()
                }
                TimerState.PAUSED -> {
                    processResumeTimer()
                }
                else -> {
                    processStartTimer()
                }
            }
        }

        hourNumberPicker.setOnValueChangedListener { _, _, newVal ->
            hour = newVal
            validateTimer()
        }

        minuteNumberPicker.setOnValueChangedListener { _, _, newVal ->
            minute = newVal
            validateTimer()
        }

        secondNumberPicker.setOnValueChangedListener { _, _, newVal ->
            second = newVal
            validateTimer()
        }
    }

    private fun validateTimer() {
        startBtn.isEnabled = hour > 0 || minute > 0 || second > 0
    }

    private fun setupView() {
        hourNumberPicker.minValue = 0
        hourNumberPicker.maxValue = 23

        minuteNumberPicker.minValue = 0
        minuteNumberPicker.maxValue = 59

        secondNumberPicker.minValue = 0
        secondNumberPicker.maxValue = 59

    }

    private fun processPauseTimer() {
        state = TimerState.PAUSED
        startBtn.text = getString(R.string.resume)
        stopService(intentService)
    }

    private fun processResumeTimer() {
        state = TimerState.STARTED
        startBtn.text = getString(R.string.pause)
        startTimerService()
    }

    private fun processStartTimer() {
        state = TimerState.STARTED

        showTimerPickerView()
        getTotalTime()

        countTimeProgressBar.progress = 0
        countTimeProgressBar.max = (totalMilliseconds / 100).toInt()

        countTimeTv.text = StringUtils.convertMillisecondToTimeString(totalMilliseconds)
        endTimeTv.text = StringUtils.convertMillisecondTo24Hour(System.currentTimeMillis() + totalMilliseconds)

        startTimerService()
    }

    private fun showTimerPickerView() {
        timePickerView.visibility = View.GONE
        runTimerView.visibility = View.VISIBLE
        startBtn.text = getString(R.string.pause)
    }

    private fun showProcessView() {
        timePickerView.visibility = View.VISIBLE
        runTimerView.visibility = View.GONE
        startBtn.text = getString(R.string.start)
    }

    private fun startTimerService() {
        intentService = Intent(this, TimerService::class.java)
        intentService!!.putExtra("totalTime", totalMilliseconds)
        startService(intentService)
        Log.i(TAG, "Started service")
    }

    @SuppressLint("SimpleDateFormat")
    private fun getTotalTime() {
        this.totalMilliseconds =
            TimeUnit.HOURS.toMillis(hour.toLong()) + TimeUnit.MINUTES.toMillis(minute.toLong()) + TimeUnit.SECONDS.toMillis(
                second.toLong()
            )
    }

    private fun processCancelTimer() {
        resetTimer()
        showProcessView()
        if (intentService != null) {
            stopService(intentService)
        }
    }

    private fun resetTimer() {
        state = TimerState.CANCELED
        this.totalMilliseconds = 0
        countTimeProgressBar.progress = 0
        PreferencesManager.clearPreference(applicationContext)
    }

    fun updateUI() {
        val millisUntilFinished = PreferencesManager.getCountDownTime(applicationContext)
        val isFinished = PreferencesManager.isFinished(applicationContext)

        if (isFinished) {
            processFinished()
        } else {
            updateProgressUI(millisUntilFinished)
            totalMilliseconds = millisUntilFinished
        }
    }

    private fun updateProgressUI(millisUntilFinished:Long) {
        val progress = (millisUntilFinished / 100).toInt()
        countTimeProgressBar.progress = countTimeProgressBar.max - progress
        countTimeTv.text = StringUtils.convertMillisecondToTimeString(millisUntilFinished)
    }

    private fun processFinished() {
        processCancelTimer()

        // Showing dialog
        showDialogSuccess()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(timerBroadCaseReceiver, IntentFilter(TimerService.countDownBackground()))
        updateUI()
        Log.i(TAG, "Registered broacast receiver")
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(timerBroadCaseReceiver)
        Log.i(TAG, "Unregistered broacast receiver")
    }

    override fun onStop() {
        try {
            unregisterReceiver(timerBroadCaseReceiver)
        } catch (e: Exception) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop()
    }

    override fun onDestroy() {
        stopService(intentService)
        Log.i(TAG, "Stopped service")
        super.onDestroy()
    }

    private val timerBroadCaseReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                updateUI()
            }
        }
    }

    private fun showDialogSuccess() {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("Time's Up")
        alertDialogBuilder.setCancelable(true)

        alertDialogBuilder.setPositiveButton(
            getString(android.R.string.ok)
        ) { dialog, _ ->
            dialog.cancel()
        }

        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}