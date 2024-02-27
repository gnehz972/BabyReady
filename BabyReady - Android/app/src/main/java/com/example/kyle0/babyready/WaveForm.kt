package com.example.kyle0.babyready

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.util.lerp
import kotlin.random.Random

@Composable
fun WaveForm(
    modifier: Modifier,
    amplitude: Int = 0
) {
    val clampedAmplitude = if (amplitude > 20000) 20000 else amplitude
    val targetValue = 6f - clampedAmplitude / (4000)
    val heightDivider by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(1000, easing = LinearEasing), label = ""
    )

    val infiniteAnimation = rememberInfiniteTransition(label = "infiniteAnimation")
    val animations = mutableListOf<State<Float>>()
    val random = remember { Random(System.currentTimeMillis()) }

    repeat(15) {
        val durationMillis = random.nextInt(300, 1000)
        animations += infiniteAnimation.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis),
                repeatMode = RepeatMode.Reverse,
            ), label = "animation"
        )
    }
    val maxLinesCount = 100
    val initialMultipliers = remember {
        mutableListOf<Float>().apply {
            repeat(maxLinesCount) { this += random.nextFloat() }
        }
    }

    Canvas(
        modifier = modifier
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val canvasCenterY = size.height / 2
        val barWidthFloat = 10f
        val gapWidthFloat = 10f
        val count =
            (canvasWidth / (barWidthFloat + gapWidthFloat)).toInt().coerceAtMost(maxLinesCount)
        val animatedVolumeWidth = count * (barWidthFloat + gapWidthFloat)
        var startOffset = (canvasWidth - animatedVolumeWidth) / 2 + barWidthFloat
        val barMinHeight = 0f

        val barMaxHeight = canvasHeight / 2f / heightDivider

        repeat(count) { index ->
            val currentSize = animations[index % animations.size].value
            var barHeightPercent = initialMultipliers[index] + currentSize
            if (barHeightPercent > 1.0f) {
                val diff = barHeightPercent - 1.0f
                barHeightPercent = 1.0f - diff
            }
            val barColor = Color.White

            val barHeight = lerp(barMinHeight, barMaxHeight, barHeightPercent)
            drawLine(
                color = barColor,
                start = Offset(startOffset, canvasCenterY - barHeight / 2),
                end = Offset(startOffset, canvasCenterY + barHeight / 2),
                strokeWidth = barWidthFloat,
                cap = StrokeCap.Round,
            )
            startOffset += barWidthFloat + gapWidthFloat
        }
    }
}