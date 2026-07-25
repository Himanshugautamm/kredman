package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NothingRed

@Composable
fun NothingDotMatrixText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 28.sp,
    color: Color = MaterialTheme.colorScheme.onBackground,
    isDotStyle: Boolean = true
) {
    if (isDotStyle) {
        Column(modifier = modifier) {
            Text(
                text = text.uppercase(),
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.5.sp,
                color = color
            )
            // Accent Dot Matrix Line under text
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .padding(top = 2.dp)
            ) {
                val dotRadius = 1.5.dp.toPx()
                val gap = 5.dp.toPx()
                var currentX = 0f
                while (currentX < size.width) {
                    drawCircle(
                        color = if ((currentX / gap).toInt() % 4 == 0) NothingRed else color.copy(alpha = 0.3f),
                        radius = dotRadius,
                        center = Offset(currentX, size.height / 2)
                    )
                    currentX += gap
                }
            }
        }
    } else {
        Text(
            text = text,
            modifier = modifier,
            fontSize = fontSize,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
