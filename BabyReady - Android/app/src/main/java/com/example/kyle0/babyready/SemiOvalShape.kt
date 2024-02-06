package com.example.kyle0.babyready

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class SemiOvalShape : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val rect = size.toRect()
        val path = Path().apply {
            arcTo(rect, -180f, 180f, false)
            lineTo(size.width, size.height / 2)
            lineTo(size.width, size.height)
            lineTo(0f, size.height)
            lineTo(0f, size.height / 2)
            close()
        }
        return Outline.Generic(path)
    }
}