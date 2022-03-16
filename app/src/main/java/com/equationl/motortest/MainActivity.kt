package com.equationl.motortest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.equationl.motortest.compose.ui.MainScreen
import com.equationl.motortest.util.VibratorHelper

class MainActivity : AppCompatActivity() {
    private var isVibrated = false
    private lateinit var vibrator: VibratorHelper

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vibrator = VibratorHelper(this)

        setContent {
            MainScreen(
                clickScreen =
                {
                    clickScreen()
                },
                slipUpScreen = {
                    slipUpScreen()
                })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelVibrate()
    }

    override fun onPause() {
        super.onPause()
        cancelVibrate()
    }

    private fun clickScreen(): Boolean {
        if (isVibrated) {
            cancelVibrate()
        }
        else {
            isVibrated = true
            vibrator.vibrate(longArrayOf(0,10000), intArrayOf(0,255), 0)
        }

        return isVibrated
    }

    private fun slipUpScreen() {
        if (!isVibrated) {
            //FIXME 这个方法会被重复调用，导致启动多个页面
            Log.i("el", "slipUpScreen: 开始切换")
            // TODO 添加切换页面动画
            val intent = Intent()
            intent.setClass(this, AdvancedActivity::class.java)
            startActivity(intent)
            /*startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, main_btn_advanced,
                "shared element").toBundle())*/
        }
    }

    private fun cancelVibrate() {
        vibrator.cancel()
        isVibrated = false
    }
}
