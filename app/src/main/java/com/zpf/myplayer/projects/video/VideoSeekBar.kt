package com.zpf.myplayer.projects.video

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.zpf.support.util.LogUtil

class VideoSeekBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var currentProgress: Int = 0
    private var maxProgress: Int = 10000
    private var onTouching: Boolean = false
    private var onLoading: Boolean = false
    private var currentAnimFrame: Int = 0
    private var loadAnimInterval: Long = 14
    private var loadAnimFrame: Int = 20
    private val path = Path()
    private val paint = Paint()
    private var downX: Float = 0f
    private var downProgress: Int = 0
    private val boldWidth: Float = 3 * resources.displayMetrics.density
    private val normalWidth = 1 * resources.displayMetrics.density
    private val dotRadius = 7 * resources.displayMetrics.density
    private var seekBarChangeListener: OnSeekBarChangeListener? = null

    init {
        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.run {
            if (onLoading) {
                paint.color = Color.WHITE
                if (currentAnimFrame > loadAnimFrame) {
                    currentAnimFrame = 0
                }
                paint.alpha = 255 - currentAnimFrame * 255 / loadAnimFrame
                paint.strokeWidth = 1 * resources.displayMetrics.density
                this.drawLine(width * 0.4f - width * 0.4f * currentAnimFrame / loadAnimFrame,
                        height * 0.5f,
                        width * 0.6f + width * 0.4f * currentAnimFrame / loadAnimFrame,
                        height * 0.5f, paint)
                currentAnimFrame++
                postInvalidateDelayed(loadAnimInterval)
            } else {
                paint.color = Color.LTGRAY
                paint.strokeWidth = 0.4f * resources.displayMetrics.density
                this.drawLine(0f, height * 0.5f, width * 1f, height * 0.5f, paint)
                path.reset()
                val useWidth = if (onTouching) {
                    boldWidth
                } else {
                    normalWidth
                }
                val endX = width * 1f * currentProgress / maxProgress
                val endY1 = (height - useWidth) / 2
                val endY2 = (height + useWidth) / 2
                path.moveTo(0f, endY1)
                path.lineTo(endX, endY1)
                path.lineTo(endX, endY2)
                path.lineTo(0f, endY2)
                path.close()
                paint.color = Color.WHITE
                this.drawPath(path, paint)
                if (onTouching) {
                    this.drawCircle(endX, height / 2f, dotRadius, paint)
                }
            }
        }
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (width > 0 && !onLoading) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downProgress = currentProgress
                    onTouching = true
                    seekBarChangeListener?.onStartTouch(downProgress)
                }
                MotionEvent.ACTION_MOVE -> {
                    if (width > 0 && onTouching) {
                        val dx = ((event.rawX + 0.5f - downX) * maxProgress / width).toInt()
                        setProgress(downProgress + dx, false)
                    }
                }
                MotionEvent.ACTION_UP,
                MotionEvent.ACTION_OUTSIDE,
                MotionEvent.ACTION_CANCEL -> {
                    if (onTouching) {
                        seekBarChangeListener?.onStopTouch(currentProgress)
                    }
                    onTouching = false
                    invalidate()
                }
            }
            LogUtil.w("onTouchEvent==>" + event?.action + ";onTouching=" + onTouching)
        } else {
            if (onTouching) {
                seekBarChangeListener?.onStopTouch(currentProgress)
            }
            onTouching = false
        }
        return onTouching
    }

    fun setLoading(loading: Boolean): Boolean {
        if (onTouching) {
            return false
        }
        if (onLoading != loading) {
            onLoading = loading
            if (onLoading) {
                currentAnimFrame = 0
            }
            invalidate()
        }
        return true
    }

    fun setProgress(p: Int, checkTouch: Boolean = true) {
        if (checkTouch && onTouching) {
            return
        }
        val lastProgress = currentProgress
        if (p < 0) {
            currentProgress = 0
        } else if (p > maxProgress) {
            currentProgress = maxProgress
        } else {
            currentProgress = p
        }
        if (currentProgress != lastProgress) {
            seekBarChangeListener?.onProgressChange(currentProgress, onTouching)
            invalidate()
        }
    }

    fun getProgress(): Int = currentProgress

    fun setMaxProgress(p: Int) {
        if (p < 0) {
            maxProgress = 0
        } else {
            maxProgress = p
        }
        if (currentProgress > maxProgress) {
            currentProgress = maxProgress
            invalidate()
        }
    }

    fun getMaxProgress(): Int = maxProgress
    fun setSeekBarChangeListener(listener: OnSeekBarChangeListener) {
        seekBarChangeListener = listener
    }

    interface OnSeekBarChangeListener {
        fun onStartTouch(progress: Int)
        fun onStopTouch(progress: Int)
        fun onProgressChange(progress: Int, fromTouch: Boolean)
    }
}