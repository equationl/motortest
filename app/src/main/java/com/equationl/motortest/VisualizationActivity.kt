package com.equationl.motortest

import android.content.Intent
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.equationl.motortest.util.VibratorHelper
import com.equationl.motortest.view.MyTouchView
import kotlinx.android.synthetic.main.activity_visualization.*
import java.util.*
import kotlin.concurrent.fixedRateTimer

private const val TAG = "el, in Visualization"


class VisualizationActivity : AppCompatActivity() {
    lateinit var vibrator: VibratorHelper
    lateinit var refreshTextTimer: Timer
    var timerText = 0
    var startTime = 0
    var timingsText = "0"
    var amplitudeText = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualization)
        
        initParameter()
        initListener()
        initStartTouch()
    }

    override fun onDestroy() {
        super.onDestroy()
        vibrator.cancel()
    }

    override fun onPause() {
        super.onPause()
        vibrator.cancel()
    }
    
    private fun initParameter() {
        vibrator = VibratorHelper(getSystemService(VIBRATOR_SERVICE) as Vibrator)
    }

    private fun initListener() {
        visualization_btn_back.setOnClickListener {
            vibrator.cancel()
            finish()
        }

        visualization_btn_save.setOnClickListener {
            vibrator.cancel()
            val intent = Intent()
            intent.putExtra("timings", timingsText)
            intent.putExtra("amplitude", amplitudeText)
            this.setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun initStartTouch() {
        visualization_touchView.setOnTouchActionListener(object : MyTouchView.OnTouchActionListener {
            override fun onDown(motionEvent: MotionEvent) {
                val amplitude = getAmplitude(motionEvent.y)
                vibrator.vibrateOneShot(120000, amplitude)
                startTime = timerText
                refreshTextTimer = fixedRateTimer(null, false, 0, 1) {
                    timerText++
                    runOnUiThread {
                        visualization_text_time.text = getString(R.string.visualization_text_time_sec, timerText.toFloat()/1000)
                    }
                }
            }

            override fun onMove(motionEvent: MotionEvent) {
                val amplitude = getAmplitude(motionEvent.y)
                recordTouch(amplitude)
                startTime = timerText
                vibrator.vibrateOneShot(120000, amplitude)
            }

            override fun onUp(motionEvent: MotionEvent) {
                val amplitude = getAmplitude(motionEvent.y)
                recordTouch(amplitude)
                startTime = timerText
                vibrator.cancel()
                refreshTextTimer.cancel()
                Log.i(TAG, "onUp: \nt=$timingsText\na=$amplitudeText")
            }

        })
    }

    private fun getAmplitude(y: Float): Int {
        val relativeScreenHeight = visualization_touchView.bottom - visualization_touchView.top
        val touchY = y.coerceIn(visualization_touchView.top.toFloat(), visualization_touchView.bottom.toFloat())
        val amplitude = 255 - touchY * 255 / relativeScreenHeight
        return amplitude.coerceIn(1f, 255f).toInt()
    }

    private fun recordTouch(amplitude: Int) {
        val timings = timerText - startTime
        timingsText = "$timingsText, $timings"
        amplitudeText = "$amplitudeText, $amplitude"
    }
}