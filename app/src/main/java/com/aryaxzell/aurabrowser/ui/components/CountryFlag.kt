package com.aryaxzell.aurabrowser.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.aryaxzell.aurabrowser.data.model.AppLanguage

@Composable
fun CountryFlag(
    language: AppLanguage,
    modifier: Modifier = Modifier,
    width: Dp = 28.dp,
    height: Dp = 20.dp,
    cornerRadius: Dp = 4.dp
) {
    val shape = RoundedCornerShape(cornerRadius)
    val borderColor = Color.Black.copy(alpha = 0.12f)

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .shadow(elevation = 1.dp, shape = shape)
            .clip(shape)
            .border(width = 0.6.dp, color = borderColor, shape = shape)
            .background(Color.White)
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            when (language) {
                AppLanguage.ID -> drawIndonesianFlag()
                AppLanguage.EN -> drawUsaFlag()
                AppLanguage.RU -> drawRussianFlag()
            }
        }
    }
}

private fun DrawScope.drawIndonesianFlag() {
    val w = size.width
    val h = size.height

    // Top half: Red (#E70011)
    drawRect(
        color = Color(0xFFE70011),
        topLeft = Offset(0f, 0f),
        size = Size(w, h / 2f)
    )

    // Bottom half: White (#FFFFFF)
    drawRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(0f, h / 2f),
        size = Size(w, h / 2f)
    )
}

private fun DrawScope.drawUsaFlag() {
    val w = size.width
    val h = size.height

    // 7 horizontal stripes (Red & White)
    val stripeHeight = h / 7f
    val redColor = Color(0xFFB22234)
    val whiteColor = Color(0xFFFFFFFF)

    for (i in 0 until 7) {
        drawRect(
            color = if (i % 2 == 0) redColor else whiteColor,
            topLeft = Offset(0f, i * stripeHeight),
            size = Size(w, stripeHeight)
        )
    }

    // Blue Canton (top-left rectangle)
    val cantonWidth = w * 0.45f
    val cantonHeight = stripeHeight * 4f
    drawRect(
        color = Color(0xFF3C3B6E),
        topLeft = Offset(0f, 0f),
        size = Size(cantonWidth, cantonHeight)
    )

    // Stylized star points inside canton (clean dot matrix)
    val starColor = Color.White.copy(alpha = 0.95f)
    val cols = 3
    val rows = 2
    val colSpacing = cantonWidth / (cols + 1)
    val rowSpacing = cantonHeight / (rows + 1)

    for (r in 1..rows) {
        for (c in 1..cols) {
            drawCircle(
                color = starColor,
                radius = cantonHeight * 0.08f,
                center = Offset(c * colSpacing, r * rowSpacing)
            )
        }
    }
}

private fun DrawScope.drawRussianFlag() {
    val w = size.width
    val h = size.height
    val stripeHeight = h / 3f

    // Top Stripe: White (#FFFFFF)
    drawRect(
        color = Color(0xFFFFFFFF),
        topLeft = Offset(0f, 0f),
        size = Size(w, stripeHeight)
    )

    // Middle Stripe: Russian Blue (#0039A6)
    drawRect(
        color = Color(0xFF0039A6),
        topLeft = Offset(0f, stripeHeight),
        size = Size(w, stripeHeight)
    )

    // Bottom Stripe: Russian Red (#D52B1E)
    drawRect(
        color = Color(0xFFD52B1E),
        topLeft = Offset(0f, stripeHeight * 2f),
        size = Size(w, stripeHeight)
    )
}
