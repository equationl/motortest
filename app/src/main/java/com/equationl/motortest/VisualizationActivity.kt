package com.equationl.motortest

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.equationl.motortest.databinding.ActivityVisualizationBinding
import com.equationl.motortest.util.VibratorHelper
import com.equationl.motortest.view.MyTouchView
import java.util.*
import kotlin.concurrent.fixedRateTimer


class VisualizationActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "el, in Visualization"
    }

    private lateinit var binding: ActivityVisualizationBinding
    private lateinit var refreshTextTimer: Timer

    private var timerText = 0
    private var startTime = 0
    private var timingsText = "0"
    private var amplitudeText = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initParameter()
        initListener()
        initStartTouch()
    }

    override fun onDestroy() {
        super.onDestroy()
         VibratorHelper.instance.cancel()
    }

    override fun onPause() {
        super.onPause()
         VibratorHelper.instance.cancel()
    }
    
    private fun initParameter() {
         VibratorHelper.instance.init(this)
    }

    private fun initListener() {
        binding.btnBack.setOnClickListener {
             VibratorHelper.instance.cancel()
            finish()
        }

        binding.btnSave.setOnClickListener {
             VibratorHelper.instance.cancel()
            val intent = Intent()
            intent.putExtra("timings", timingsText)
            intent.putExtra("amplitude", amplitudeText)
            this.setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initStartTouch() {
        binding.touchView.setOnTouchActionListener(object : MyTouchView.OnTouchActionListener {
            override fun onDown(motionEvent: MotionEvent) {
                val amplitude = getAmplitude(motionEvent.y)
                 VibratorHelper.instance.vibrateOneShot(120000, amplitude.coerceIn(1, 255))
                startTime = timerText
                runOnUiThread {
                    binding.textAmplitude.text = getString(R.string.visualization_text_amplitude, amplitude)
                }
                refreshTextTimer = fixedRateTimer(null, false, 0, 1) {
                    timerText++
                    runOnUiThread {
                        binding.textTime.text = getString(R.string.visualization_text_time_sec, timerText.toFloat()/1000)
                    }
                }
            }

            override fun onMove(motionEvent: MotionEvent) {
                val amplitude = getAmplitude(motionEvent.y)
                recordTouch(amplitude)
                startTime = timerText
                 VibratorHelper.instance.vibrateOneShot(120000, amplitude.coerceIn(1, 255))
            }

            override fun onUp(motionEvent: MotionEvent) {
                val amplitude = getAmplitude(motionEvent.y)
                recordTouch(amplitude)
                startTime = timerText
                 VibratorHelper.instance.cancel()
                refreshTextTimer.cancel()
            }

        })
    }

    private fun getAmplitude(y: Float): Int {
        val touchTop = binding.touchView.top.toFloat() + 50
        val touchBottom = binding.touchView.bottom.toFloat() - 50
        val relativeScreenHeight = touchBottom - touchTop
        val touchY = y.coerceIn(
            0f,
            touchBottom
        )
        val amplitude = 255 - touchY * 255 / relativeScreenHeight
        return amplitude.coerceIn(0f, 255f).toInt()
    }

    private fun recordTouch(amplitude: Int) {
        val timings = timerText - startTime
        timingsText = "$timingsText, $timings"
        amplitudeText = "$amplitudeText, $amplitude"
        runOnUiThread {
            binding.textAmplitude.text = getString(R.string.visualization_text_amplitude, amplitude)
        }
    }
}