package com.equationl.motortest

import android.animation.Animator
import android.animation.AnimatorInflater
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.equationl.motortest.util.VibratorHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var isVibrated = false
    private lateinit var vibrator: VibratorHelper
    private lateinit var vibrationAnimator: Animator
    private lateinit var slipUpAnimator: Animator
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(main_toolbar)

        vibrator = VibratorHelper(this)

        main_btn_advanced.setOnClickListener {
            slipUpScreen()
        }

        main_text_slip_tip.setOnClickListener {
            slipUpScreen()
        }

        initAnimator()
        initGestureDetector()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelVibrate()
    }

    override fun onPause() {
        super.onPause()
        cancelVibrate()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    private fun clickScreen() {
        if (isVibrated) {
            cancelVibrate()
        }
        else {
            main_tip_text.text = getString(R.string.main_text_click_stop)
            isVibrated = true
            vibrator.vibrate(longArrayOf(0,10000), intArrayOf(0,255), 0)
            main_btn_advanced.visibility = View.GONE
            main_text_slip_tip.visibility = View.GONE
            vibrationAnimator.start()
        }
    }

    private fun slipUpScreen() {
        if (!isVibrated) {
            val intent = Intent()
            intent.setClass(this, AdvancedActivity::class.java)
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, main_btn_advanced,
                "shared element").toBundle())
        }
    }

    private fun cancelVibrate() {
        vibrator.cancel()
        vibrationAnimator.cancel()
        main_tip_text.text = getString(R.string.main_text_click_start)
        isVibrated = false
        main_btn_advanced.visibility = View.VISIBLE
        main_text_slip_tip.visibility = View.VISIBLE
    }

    private fun initAnimator() {
        vibrationAnimator = AnimatorInflater.loadAnimator(this@MainActivity, R.animator.anim_vibrate)
        vibrationAnimator.setTarget(main_tip_text)
        slipUpAnimator = AnimatorInflater.loadAnimator(this@MainActivity, R.animator.anim_slide_up)
        slipUpAnimator.setTarget(main_btn_advanced)
        slipUpAnimator.start()
    }

    private fun initGestureDetector() {
        gestureDetector = GestureDetector(applicationContext, object : GestureDetector.OnGestureListener {
            override fun onShowPress(e: MotionEvent?) {}

            override fun onSingleTapUp(e: MotionEvent?): Boolean {
                clickScreen()
                return true
            }

            override fun onDown(e: MotionEvent?): Boolean {
                return false
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 != null && e2 != null) {
                    if (e1.y - e2.y > 50) {
                        slipUpScreen()
                        return true
                    }
                }
                return false
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return false
            }

            override fun onLongPress(e: MotionEvent?) {}
        })
    }
}
