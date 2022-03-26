package com.equationl.motortest

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.compose.ui.AdvancedScreen
import com.equationl.motortest.compose.ui.MainScreen
import com.equationl.motortest.util.VibratorHelper

class MainActivity : AppCompatActivity() {
    private var isVibrated = false

    private val viewModel: MyViewMode by viewModels()


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        VibratorHelper.instance.init(this)

        setContent {
            when (viewModel.currentPage) {
                0 -> {
                    MainScreen(clickScreen = { clickScreen() }, slipUpScreen = { slipUpScreen() })
                }
                1 -> {
                    AdvancedScreen(onBack = {
                        viewModel.currentPage = 0
                    })
                }
            }

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

    override fun onBackPressed() {
        if (viewModel.currentPage == 0)
            super.onBackPressed()
        else
            viewModel.currentPage = 0
    }

    private fun clickScreen(): Boolean {
        if (isVibrated) {
            cancelVibrate()
        }
        else {
            isVibrated = true
            VibratorHelper.instance.vibrate(longArrayOf(0,10000), intArrayOf(0,255), 0)
        }

        return isVibrated
    }

    private fun slipUpScreen() {
        if (!isVibrated) {
            //FIXME 这个方法会被重复调用
            Log.i("el", "slipUpScreen: 开始切换")
            // TODO 添加切换页面动画
            /*val intent = Intent()
            intent.setClass(this, AdvancedActivity::class.java)
            startActivity(intent)*/
            /*startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, main_btn_advanced,
                "shared element").toBundle())*/
            viewModel.currentPage = 1
        }
    }

    private fun cancelVibrate() {
        VibratorHelper.instance.cancel()
        isVibrated = false
    }
}
