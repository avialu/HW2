package com.avialu.hw2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.roundToInt

class RoadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val roadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#8B8B8B")
    }

    private val whiteDashPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeCap = Paint.Cap.ROUND
    }

    private val yellowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.parseColor("#F2D400")
        strokeCap = Paint.Cap.BUTT
    }

    private val yellowBlockPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.parseColor("#F2D400")
    }

    override fun onDraw(canvas: Canvas) {
        val w = width.toFloat()
        val h = height.toFloat()
        if (w <= 0f || h <= 0f) return

        canvas.drawRect(0f, 0f, w, h, roadPaint)

        val lanes = 5
        val laneW = w / lanes

        val dashX1 = laneW * 1f
        val yellowX1 = laneW * 2f
        val yellowX2 = laneW * 3f
        val dashX2 = laneW * 4f

        val whiteStroke = (w * 0.012f).coerceAtLeast(3f)
        val yellowStroke = (w * 0.016f).coerceAtLeast(4f)

        whiteDashPaint.strokeWidth = whiteStroke
        whiteDashPaint.pathEffect = DashPathEffect(floatArrayOf(h * 0.05f, h * 0.05f), 0f)

        yellowPaint.strokeWidth = yellowStroke

        canvas.drawLine(dashX1, 0f, dashX1, h, whiteDashPaint)
        canvas.drawLine(dashX2, 0f, dashX2, h, whiteDashPaint)

        canvas.drawLine(yellowX1, 0f, yellowX1, h, yellowPaint)
        canvas.drawLine(yellowX2, 0f, yellowX2, h, yellowPaint)

        val blockH = (h * 0.04f).coerceAtLeast(10f)
        val blockW = (yellowStroke * 1.6f).coerceAtLeast(8f)
        val step = blockH * 1.6f

        var y = 0f
        var flip = false
        while (y < h) {
            val dx = if (flip) blockW * 0.9f else -blockW * 0.9f

            canvas.drawRect(
                (yellowX1 - blockW / 2f) + dx,
                y,
                (yellowX1 + blockW / 2f) + dx,
                y + blockH,
                yellowBlockPaint
            )

            canvas.drawRect(
                (yellowX2 - blockW / 2f) - dx,
                y,
                (yellowX2 + blockW / 2f) - dx,
                y + blockH,
                yellowBlockPaint
            )

            flip = !flip
            y += step
        }
    }
}
