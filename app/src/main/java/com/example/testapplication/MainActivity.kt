package com.example.testapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        // Set status bar color
        // Set status bar color
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        setContentView(R.layout.activity_main)
        setupListener()
    }

    private fun setupListener() {
        cancelBtn.setOnClickListener{
            processCancelTimer()
        }

        startBtn.setOnClickListener{
            processStartTimer()
        }
    }

    private fun processStartTimer(){
        timePicker.visibility = View.GONE
        runTimerView.visibility = View.VISIBLE
        startBtn.text = getString(R.string.pause)

    }

    private fun processCancelTimer(){
        timePicker.visibility = View.VISIBLE
        runTimerView.visibility = View.GONE
        startBtn.text = getString(R.string.start)
        countTimeProgressBar.progress = 100

    }


}