package com.example.testapplication.utils

import android.app.Service
import android.content.Context
import android.util.Log

class PreferencesManager {
    companion object {

        private val PREF_FILE_NAME = "prefs"
        private val IS_FINISHED = "isFinished"
        private val COUNT_DOWN_MILLIS = "countdown"

        fun removeKey(key: String?, context: Context) {
            try {
                val sharedPreferences = context.getSharedPreferences(
                    PREF_FILE_NAME,
                    Service.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()
                editor.remove(key)
                editor.apply()
            } catch (ex: Exception) {
                Log.e("EXCEPTION", ex.message)
            }
        }

        private fun saveBoolPref(key: String?, value: Boolean, context: Context) {
            val sharedPreferences = context.getSharedPreferences(
                PREF_FILE_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            if (key != null) {
                editor.putBoolean(key, value)
                editor.apply()
            }
        }

        private fun getBoolPref(key: String?, context: Context): Boolean {
            val sharedPreferences = context.getSharedPreferences(
                PREF_FILE_NAME,
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getBoolean(key, false)
        }

        private fun saveLongPref(key: String?, value: Long, context: Context) {
            val sharedPreferences = context.getSharedPreferences(
                PREF_FILE_NAME,
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            if (value > 0) {
                editor.putLong(key, value)
                editor.apply()
            }
        }

        private fun getLongPref(key: String?, context: Context): Long {
            val sharedPreferences = context.getSharedPreferences(
                PREF_FILE_NAME,
                Context.MODE_PRIVATE
            )
            return sharedPreferences.getLong(key, -1)
        }


        fun saveIsFinished(isFinished: Boolean, context: Context) {
            saveBoolPref(IS_FINISHED, isFinished, context)
        }

        fun isFinished(context: Context): Boolean {
            return getBoolPref(IS_FINISHED, context)
        }

        fun saveCountDownTime(millisecond: Long, context: Context) {
            saveLongPref(COUNT_DOWN_MILLIS, millisecond, context)
        }

        fun getCountDownTime(context: Context): Long {
            return getLongPref(COUNT_DOWN_MILLIS, context)
        }

        fun clearPreference(context: Context) {
            removeKey(IS_FINISHED, context)
            removeKey(COUNT_DOWN_MILLIS, context)
        }

    }

}