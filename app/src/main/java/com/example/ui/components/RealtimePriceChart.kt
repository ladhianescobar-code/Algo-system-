package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradingRepository
import com.example.ui.theme.SkynetAccentBlue
import com.example.ui.theme.SkynetAccentCyan
import com.example.ui.theme.SkynetAccentGold
import com.example.ui.theme.SkynetAccentGreen
import com.example.ui.theme.SkynetAccentRed
import com.example.ui.theme.SkynetDarkBg
import com.example.ui.theme.SkynetPanelBg
import com.example.ui.theme.SkynetPanelBorder
import com.example.ui.theme.SkynetTextDim
import com.example.ui.theme.SkynetTextMain

@Composable
fun RealtimePriceChart(
    state: TradingRepository.State,
    onRefreshPulse: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SkynetPanelBg)
            .border(1.dp, SkynetPanelBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("chart_container")
    ) {
        // Chart Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SkynetAccentGreen.copy(alpha = pulseAlpha))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BTC/USDT Real-Time Execution Chart",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkynetTextMain
                )
            }

            Text(
                text = "$${"%,.2f".format(state.currentPrice)} USDT",
                fontSize = 13.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = SkynetAccentGold,
                modifier = Modifier.testTag("current_price_display")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Canvas Chart
        val prices = state.priceHistory
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SkynetDarkBg)
                .border(1.dp, SkynetPanelBorder.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                if (prices.size < 2) return@Canvas

                val minPrice = prices.minOrNull() ?: 60000.0
                val maxPrice = prices.maxOrNull() ?: 70000.0
                val range = (maxPrice - minPrice).coerceAtLeast(1.0)

                val width = size.width
                val height = size.height

                // Draw horizontal subtle grid lines
                val gridLines = 3
                for (g in 0..gridLines) {
                    val y = (height / gridLines) * g
                    drawLine(
                        color = Color(0xFF2A2E39).copy(alpha = 0.4f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f
                    )
                }

                // Construct line and area path
                val linePath = Path()
                val areaPath = Path()

                val stepX = width / (prices.size - 1)

                prices.forEachIndexed { i, price ->
                    val x = i * stepX
                    val normalized = (price - minPrice) / range
                    val y = height - (normalized * height).toFloat()

                    if (i == 0) {
                        linePath.moveTo(x, y)
                        areaPath.moveTo(x, height)
                        areaPath.lineTo(x, y)
                    } else {
                        linePath.lineTo(x, y)
                        areaPath.lineTo(x, y)
                    }

                    if (i == prices.lastIndex) {
                        areaPath.lineTo(x, height)
                        areaPath.close()

                        // Draw current price node
                        drawCircle(
                            color = SkynetAccentCyan,
                            radius = 4.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = SkynetAccentBlue.copy(alpha = pulseAlpha),
                            radius = 8.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }

                // Fill glowing gradient under the chart line
                val gradientBrush = Brush.verticalGradient(
                    colors = listOf(
                        SkynetAccentBlue.copy(alpha = 0.35f),
                        SkynetAccentCyan.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = height
                )
                drawPath(path = areaPath, brush = gradientBrush)

                // Draw neon stroke line
                drawPath(
                    path = linePath,
                    color = SkynetAccentBlue,
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // Min and Max price overlay labels
            if (prices.isNotEmpty()) {
                val minP = prices.minOrNull() ?: 0.0
                val maxP = prices.maxOrNull() ?: 0.0
                Text(
                    text = "H: $${"%,.0f".format(maxP)}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SkynetTextDim,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                )
                Text(
                    text = "L: $${"%,.0f".format(minP)}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = SkynetTextDim,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fast Pulse Bar (Gemini 3.1 Flash-Lite micro-intelligence)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SkynetDarkBg)
                .border(1.dp, SkynetAccentCyan.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = SkynetAccentCyan,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Market Pulse (Gemini-3.1-Flash-Lite)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkynetAccentCyan
                )
                Text(
                    text = state.fastPulseMessage ?: "Orderbook delta positive. Mean-reversion bias intact.",
                    fontSize = 11.sp,
                    color = SkynetTextMain,
                    maxLines = 2
                )
            }
            IconButton(
                onClick = onRefreshPulse,
                modifier = Modifier
                    .size(28.dp)
                    .testTag("refresh_pulse_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Pulse",
                    tint = SkynetTextDim,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
