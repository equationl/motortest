package com.equationl.motortest.util

import android.content.Context
import android.widget.Toast
import com.equationl.motortest.R
import com.equationl.motortest.compose.MyViewMode


/**
 * FileName: Utils
 * Author: equation
 * Email: admin@likehide.com
 * Date: 2020/3/1 23:53
 * Description: 一些工具方法集合
 */
object Utils {
    fun checkDevice(context: Context, viewMode: MyViewMode): Boolean {
        val hasVibrator =  VibratorHelper.instance.hasVibrator()
        if (!hasVibrator) {
            Toast.makeText(context, R.string.advanced_toast_notSupportVibrator, Toast.LENGTH_LONG).show()
            viewMode.currentPage = 0
            return false
        }

        if (!VibratorHelper.instance.hasAmplitudeControl()) {
            viewMode.setAmplitudesError(context.getString(R.string.advanced_text_notSupport_amplitude))
        }

        return true
    }
}