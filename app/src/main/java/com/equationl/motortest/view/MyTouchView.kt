package com.equationl.motortest.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.Nullable

/**
 * Created by lingbeijing on 2020/9/5.
 * Edited by equationl on 2021/07/17.
 */
class MyTouchView : View{
    private lateinit var mPaint: Paint
    private var mTouchActionListener: OnTouchActionListener? = null
    private var pointX = 0f
    private var pointY = 0f
    private val radio = 43.0f
    private var circleRectF: RectF? = null

    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {
        init()
    }
    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0) {
        init()
    }

    constructor(
        context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                createCircleRect(event)
                mTouchActionListener?.onDown(event)
            }
            MotionEvent.ACTION_MOVE -> {
                pointX = event.x
                pointY = event.y
                //在触摸点位置为中心绘制圆
                if (circleRectF != null) {
                    circleRectF!!.left = pointX - radio
                    circleRectF!!.right = pointX + radio
                    circleRectF!!.bottom = pointY + radio
                    circleRectF!!.top = pointY - radio
                    invalidate()
                }
                mTouchActionListener?.onMove(event)
            }
            MotionEvent.ACTION_UP,  MotionEvent.ACTION_CANCEL -> {
                pointX = 0f
                pointY = 0f
                circleRectF = null
                invalidate()
                mTouchActionListener?.onUp(event)
            }
        }
        return true
    }

    fun setOnTouchActionListener(listener: OnTouchActionListener) {
        this.mTouchActionListener = listener
    }

    private fun init() {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = Color.GREEN
        mPaint.strokeWidth = 5f
        mPaint.style = Paint.Style.FILL_AND_STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (circleRectF != null) {
            canvas.drawRoundRect(circleRectF!!, radio, radio, mPaint)
        }
    }

    private fun createCircleRect(motionEvent: MotionEvent) {
        val x = motionEvent.x
        val y = motionEvent.y
        val rectF = RectF()
        rectF.left = x - radio
        rectF.right = x + radio
        rectF.bottom = y + radio
        rectF.top = y - radio
        circleRectF = rectF
        invalidate()
    }

    interface OnTouchActionListener {
        fun onDown(motionEvent: MotionEvent)
        fun onMove(motionEvent: MotionEvent)
        fun onUp(motionEvent: MotionEvent)
    }
}