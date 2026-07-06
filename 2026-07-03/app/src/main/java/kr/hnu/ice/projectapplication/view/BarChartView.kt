package kr.hnu.ice.projectapplication.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kr.hnu.ice.projectapplication.R

/**
 * 외부 차트 라이브러리 없이 막대그래프를 그리는 커스텀 View.
 * 값 목록과 목표선(goal)을 캔버스에 직접 그린다.
 */
class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var values: List<Float> = emptyList()
    private var labels: List<String> = emptyList()
    private var goalLine: Float = 0f

    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.water_blue)
    }
    private val goalPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.water_blue_dark)
        strokeWidth = 3f
        style = Paint.Style.STROKE
        pathEffect = android.graphics.DashPathEffect(floatArrayOf(12f, 8f), 0f)
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_secondary)
        textSize = 28f
        textAlign = Paint.Align.CENTER
    }
    private val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.text_primary)
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    fun setData(values: List<Float>, labels: List<String>, goalLine: Float = 0f) {
        this.values = values
        this.labels = labels
        this.goalLine = goalLine
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (values.isEmpty()) return

        val maxValue = (values.maxOrNull() ?: 0f).coerceAtLeast(goalLine).coerceAtLeast(1f)
        val chartBottom = height - 60f
        val chartTop = 20f
        val chartHeight = chartBottom - chartTop
        val barCount = values.size
        val slotWidth = width.toFloat() / barCount
        val barWidth = slotWidth * 0.5f

        if (goalLine > 0f) {
            val goalY = chartBottom - (goalLine / maxValue) * chartHeight
            canvas.drawLine(0f, goalY, width.toFloat(), goalY, goalPaint)
        }

        values.forEachIndexed { index, value ->
            val centerX = slotWidth * index + slotWidth / 2f
            val barHeight = (value / maxValue) * chartHeight
            val top = chartBottom - barHeight
            canvas.drawRoundRect(
                centerX - barWidth / 2f, top, centerX + barWidth / 2f, chartBottom,
                8f, 8f, barPaint
            )
            if (value > 0f) {
                canvas.drawText(value.toInt().toString(), centerX, top - 10f, valuePaint)
            }
            labels.getOrNull(index)?.let { label ->
                canvas.drawText(label, centerX, height.toFloat() - 10f, labelPaint)
            }
        }
    }

    init {
        setBackgroundColor(Color.TRANSPARENT)
    }
}
