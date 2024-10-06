package kky.flab.lookaround.core.ui.util

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    brush: Brush,
    shape: Shape = RoundedCornerShape(10.dp),
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round
) = internalDashedBorder(
    brush = brush,
    shape = shape,
    strokeWidth = strokeWidth,
    dashLength = dashLength,
    gapLength = gapLength,
    cap = cap,
)

fun Modifier.dashedBorder(
    color: Color,
    shape: Shape = RoundedCornerShape(10.dp),
    strokeWidth: Dp = 2.dp,
    dashLength: Dp = 4.dp,
    gapLength: Dp = 4.dp,
    cap: StrokeCap = StrokeCap.Round
) = internalDashedBorder(
    brush = SolidColor(color),
    shape = shape,
    strokeWidth = strokeWidth,
    dashLength = dashLength,
    gapLength = gapLength,
    cap = cap,
)

private fun Modifier.internalDashedBorder(
    brush: Brush,
    shape: Shape,
    strokeWidth: Dp,
    dashLength: Dp,
    gapLength: Dp,
    cap: StrokeCap,
) = this.drawWithContent {
    val outline = shape.createOutline(size, layoutDirection, density = this)
    val dashedStroke = Stroke(
        cap = cap,
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx())
        )
    )
    // Draw the content
    drawContent()

    // Draw the border
    drawOutline(
        outline = outline,
        style = dashedStroke,
        brush = brush
    )
}