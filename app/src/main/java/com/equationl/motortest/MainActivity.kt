package com.equationl.motortest

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import com.equationl.motortest.compose.MyViewMode
import com.equationl.motortest.compose.ui.AdvancedScreen
import com.equationl.motortest.compose.ui.MainScreen
import com.equationl.motortest.compose.ui.VisualizationScreen
import com.equationl.motortest.util.Utils
import com.equationl.motortest.util.VibratorHelper

class MainActivity : AppCompatActivity() {

    private val viewModel: MyViewMode by viewModels()


    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        VibratorHelper.instance.init(this)

        setContent {
            viewModel.isSupportAdvanced = Utils.checkDevice(this@MainActivity, viewModel)

            if (viewModel.currentPage == 2) {
                VisualizationScreen()
            }
            else {
                Box {
                    if (viewModel.isSupportAdvanced) {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelVibrate()
    }

    override fun onPause() {
        super.onPause()

        if (viewModel.currentPage == 0 || viewModel.currentPage == 2) {
            cancelVibrate()
        }
        else if (!viewModel.isRunOnBackground) {
            cancelVibrate()
        }
    }

    override fun onBackPressed() {
        when (viewModel.currentPage) {
            0 -> super.onBackPressed()
            1 -> viewModel.currentPage = 0
            2 -> viewModel.currentPage = 1
        }
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
            if (viewModel.isSupportAdvanced) {
                viewModel.currentPage = 1
            }
        }
    }

    private fun cancelVibrate() {
        VibratorHelper.instance.cancel()
        viewModel.isVibrated = false
    }
}
