package com.equationl.motortest

import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import com.equationl.motortest.view.MyTouchView
import kotlinx.android.synthetic.main.activity_visualization.*

class VisualizationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualization)

        initListener()
        initStartTouch()
    }

    private fun initListener() {
        visualization_btn_back.setOnClickListener {
            //TODO
            finish()
        }

        visualization_btn_save.setOnClickListener {
            //TODO
        }
    }

    private fun initStartTouch() {
        visualization_touchView.setOnTouchActionListener(object : MyTouchView.OnTouchActionListener {
            override fun onDown(motionEvent: MotionEvent) {
                //TODO("Not yet implemented")
            }

            override fun onMove(motionEvent: MotionEvent) {
                //TODO("Not yet implemented")
            }

            override fun onUp(motionEvent: MotionEvent) {
                //TODO("Not yet implemented")
            }

        })
    }
}