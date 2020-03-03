package com.equationl.motortest.util

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * FileName: VibratorHelper.kt
 * Author: equationl
 * Email: admin@likehide.com
 * Date: 2020/3/1 18:30
 * Description: Vibrator帮助类，用于解决旧版本兼容问题
 */
class VibratorHelper(private val vibrator: Vibrator) {

    fun cancel() {
        vibrator.cancel()
    }

    fun hasVibrator(): Boolean {
        return vibrator.hasVibrator()
    }

    fun hasAmplitudeControl(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }
        return vibrator.hasAmplitudeControl()
    }

    fun vibrate(timings: LongArray, amplitudes: IntArray, repeat: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, repeat)
            vibrator.vibrate(vibrationEffect)
        }
        else {
            val pattern = mutableListOf<Long>()
            var isCloseMotor = false
            var duration = 0L
            for (i in amplitudes.indices) {
                if ((amplitudes[i] > 0) == isCloseMotor) {
                    duration += timings[i]
                }
                else {
                    pattern.add(duration)
                    isCloseMotor = amplitudes[i] > 0
                    duration = timings[i]
                }
            }
            pattern.add(duration)

            val patternA = pattern.toLongArray()
            @Suppress("DEPRECATION")
            vibrator.vibrate(patternA, repeat)
        }
    }

    fun vibrateOneShot(milliseconds: Long, amplitude: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(milliseconds, amplitude)
            vibrator.vibrate(vibrationEffect)
        }
        else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(milliseconds)
        }
    }

    fun vibratePredefined(predefined: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val vibrationEffect = VibrationEffect.createPredefined(predefined)
            vibrator.vibrate(vibrationEffect)
        }
        else {
            TODO("系统预设效果就暂时不适配了")
        }
    }

}