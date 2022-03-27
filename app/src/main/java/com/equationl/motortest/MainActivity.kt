package com.equationl.motortest

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.compose.ui.AdvancedScreen
import com.equationl.motortest.compose.ui.MainScreen
import com.equationl.motortest.util.Utils
import com.equationl.motortest.util.VibratorHelper

class MainActivity : AppCompatActivity() {

    private val viewModel: MyViewMode by viewModels()


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        VibratorHelper.instance.init(this)

        setContent {
            Box {
                if (Utils.checkDevice(this@MainActivity, viewModel)) {
                    AdvancedScreen(onBack = {
                        viewModel.currentPage = 0
                    })
                }
                if (viewModel.currentPage == 0) {
                    MainScreen(
                        clickScreen = { clickScreen() },
                        slipUpScreen = { slipUpScreen() })
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

        if (viewModel.currentPage == 0) {
            cancelVibrate()
        }
        else if (!viewModel.isRunOnBackground) {
            cancelVibrate()
        }
    }

    override fun onBackPressed() {
        if (viewModel.currentPage == 0)
            super.onBackPressed()
        else
            viewModel.currentPage = 0
    }

    private fun clickScreen(): Boolean {
        if (viewModel.isVibrated) {
            cancelVibrate()
        }
        else {
            viewModel.isVibrated = true
            VibratorHelper.instance.vibrate(longArrayOf(0,10000), intArrayOf(0,255), 0)
        }

        return viewModel.isVibrated
    }

    private fun slipUpScreen() {
        if (!viewModel.isVibrated) {
            if (Utils.checkDevice(this, viewModel)) {
                viewModel.currentPage = 1
            }
        }
    }

    private fun cancelVibrate() {
        VibratorHelper.instance.cancel()
        viewModel.isVibrated = false
    }
}
