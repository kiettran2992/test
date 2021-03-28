package com.example.testapplication.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class StringUtils {
    companion object {

        @SuppressLint("DefaultLocale")
        fun convertMillisecondToTimeString(millisUntilFinished: Long): String {

            return String.format(
                "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                    TimeUnit.MILLISECONDS.toHours(
                        millisUntilFinished
                    )
                ),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(
                        millisUntilFinished
                    )
                )
            )
        }

        @SuppressLint("SimpleDateFormat")
        fun convertMillisecondTo24Hour(milliseconds: Long) : String {
            val date = Date(milliseconds)
            val formatter = SimpleDateFormat("hh:mm:ss a")

            return formatter.format(date)
        }
    }
}