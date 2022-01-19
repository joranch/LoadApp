package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = resources.getString(R.string.button_name)
    private var progress = 0
    private var buttonColor = 0
    private var buttonTextColor = 0
    private var buttonCircleColor = 0
    private var progressColor = 0

    // Use 360 for the arc
    private val valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(3000)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when (buttonState) {
            ButtonState.Clicked -> {
                // Do something?
            }
            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                valueAnimator.start()
            }
            else -> {
                // Completed
                buttonText = resources.getString(R.string.button_name)
                valueAnimator.cancel()
                progress = 0
            }
        }
        invalidate()
    }

    private val paint = Paint().apply {
        isAntiAlias = true
        strokeWidth = 5f
        textSize = resources.getDimension(R.dimen.default_text_size)
        textAlign = Paint.Align.CENTER
        style = Paint.Style.FILL
        typeface = Typeface.create( "", Typeface.BOLD)
    }

    init {
        isClickable = true

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            buttonTextColor = getColor(R.styleable.LoadingButton_buttonTextColor, 0)
            buttonCircleColor = getColor(R.styleable.LoadingButton_buttonCircleColor, 0)
            progressColor = getColor(R.styleable.LoadingButton_progressColor, 0)
        }

        buttonState = ButtonState.Completed
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART

        valueAnimator.addUpdateListener {
            progress = it.animatedValue as Int
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawButtonRect(canvas)
        drawProgressRect(canvas)
        drawButtonText(canvas)
        drawButtonArc(canvas)
    }

    fun setCustomButtonState(state: ButtonState){
        buttonState = state
    }

    private fun drawButtonRect(canvas: Canvas?) {
        paint.color = buttonColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
    }

    private fun drawProgressRect(canvas: Canvas?) {
        paint.color = progressColor
        // Divide progress by 360 to get correct value
        canvas?.drawRect(0f, 0f, widthSize * progress / 360f, heightSize.toFloat(), paint)
    }

    private fun drawButtonText(canvas: Canvas?) {
        paint.color = buttonTextColor
        canvas?.drawText(buttonText, (widthSize / 2.0f), (heightSize / 2.0f) + 25, paint)
    }

    private fun drawButtonArc(canvas: Canvas?) {
        //https://stackoverflow.com/questions/14688117/how-to-make-circle-custom-progress-bar-in-android/26806770#26806770
        paint.color = buttonCircleColor
        canvas?.drawArc(
            widthSize - 150f,
            30f,
            widthSize - 50f,
            130f,
            0f,
            progress.toFloat(),
            true,
            paint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}