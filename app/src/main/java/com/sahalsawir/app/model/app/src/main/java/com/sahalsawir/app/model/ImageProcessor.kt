package com.sahalsawir.app.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ImageProcessor {

    suspend fun enhanceBitmap(
        source: Bitmap,
        upscaleFactor: Float,
        denoiseStrength: Float,
        colorCorrection: Float,
        filterId: String = "asal"
    ): Bitmap = withContext(Dispatchers.IO) {

        val scaledWidth = (source.width * (1.1f + (upscaleFactor - 1.0f) * 0.15f)).toInt().coerceAtLeast(100)
        val scaledHeight = (source.height * (1.1f + (upscaleFactor - 1.0f) * 0.15f)).toInt().coerceAtLeast(100)
        val safeWidth = scaledWidth.coerceAtMost(1600)
        val safeHeight = scaledHeight.coerceAtMost(1600)

        val scaledBitmap = Bitmap.createScaledBitmap(source, safeWidth, safeHeight, true)
        val outputBitmap = Bitmap.createBitmap(safeWidth, safeHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val colorMatrix = ColorMatrix()

        val satVal = 1.0f + (colorCorrection * 0.7f)
        colorMatrix.setSaturation(satVal)

        if (colorCorrection > 0.1f) {
            val scale = 1.0f + (colorCorrection * 0.25f)
            val translate = (-.5f * scale + .5f) * 255f
            val contrastMatrix = ColorMatrix(floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            ))
            colorMatrix.postConcat(contrastMatrix)
        }

        when (filterId) {
            "madow_caddaan" -> {
                val bwMatrix = ColorMatrix()
                bwMatrix.setSaturation(0f)
                colorMatrix.postConcat(bwMatrix)
            }
            "kulaal" -> {
                val warmMatrix = ColorMatrix(floatArrayOf(
                    1.15f, 0f, 0f, 0f, 10f,
                    0f, 1.05f, 0f, 0f, 5f,
                    0f, 0f, 0.90f, 0f, -10f,
                    0f, 0f, 0f, 1.0f, 0f
                ))
                colorMatrix.postConcat(warmMatrix)
            }
            "qabow" -> {
                val coolMatrix = ColorMatrix(floatArrayOf(
                    0.90f, 0f, 0f, 0f, -5f,
                    0f, 1.02f, 0f, 0f, 0f,
                    0f, 0f, 1.20f, 0f, 15f,
                    0f, 0f, 0f, 1.0f, 0f
                ))
                colorMatrix.postConcat(coolMatrix)
            }
            "istuudiyo" -> {
                val studioMatrix = ColorMatrix(floatArrayOf(
                    1.08f, 0f, 0.02f, 0f, 12f,
                    0f, 1.08f, 0.02f, 0f, 12f,
                    0f, 0f, 1.04f, 0f, 5f,
                    0f, 0f, 0f, 1.00f, 0f
                ))
                val extraSat = ColorMatrix()
                extraSat.setSaturation(1.3f)
                colorMatrix.postConcat(studioMatrix)
                colorMatrix.postConcat(extraSat)
            }
        }

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

        return@withContext if (denoiseStrength > 0.05f) {
            applySmoothFilter(outputBitmap, denoiseStrength)
        } else {
            outputBitmap
        }
    }

    private fun applySmoothFilter(bitmap: Bitmap, strength: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val scaleFactor = 1.0f - (strength * 0.08f).coerceAtMost(0.06f)
        val smallW = (width * scaleFactor).toInt().coerceAtLeast(10)
        val smallH = (height * scaleFactor).toInt().coerceAtLeast(10)
        val small = Bitmap.createScaledBitmap(bitmap, smallW, smallH, true)
        val upscaled = Bitmap.createScaledBitmap(small, width, height, true)
        canvas.drawBitmap(upscaled, 0f, 0f, paint)
        val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        overlayPaint.alpha = ((1f - strength) * 80).toInt().coerceIn(0, 80)
        canvas.drawBitmap(bitmap, 0f, 0f, overlayPaint)
        small.recycle()
        upscaled.recycle()
        return result
    }
}
