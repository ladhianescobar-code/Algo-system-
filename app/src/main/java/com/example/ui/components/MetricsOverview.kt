package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradingRepository
import com.example.ui.theme.SkynetAccentGold
import com.example.ui.theme.SkynetAccentGreen
import com.example.ui.theme.SkynetAccentRed
import com.example.ui.theme.SkynetPanelBg
import com.example.ui.theme.SkynetPanelBorder
import com.example.ui.theme.SkynetTextDim
import com.example.ui.theme.SkynetTextMain

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MetricsOverview(
    state: TradingRepository.State,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            MetricCard(
                title = "Trading Account Balance",
                value = "$${"%.2f".format(state.balance)}",
                accentColor = SkynetAccentGreen,
                icon = Icons.Default.AccountBalanceWallet,
                testTag = "metric_trading_balance",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Vault Profits (Secured)",
                value = "$${"%.2f".format(state.vault)}",
                accentColor = SkynetAccentGold,
                icon = Icons.Default.Lock,
                testTag = "metric_vault_profits",
                modifier = Modifier.weight(1f)
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            MetricCard(
                title = "Win Rate (${state.wins}/${state.totalTrades})",
                value = "${"%.1f".format(state.winRatePercent)}%",
                accentColor = if (state.winRatePercent >= 50.0) SkynetAccentGreen else SkynetAccentRed,
                icon = if (state.winRatePercent >= 50.0) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                testTag = "metric_win_rate",
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Max Drawdown",
                value = "${"%.2f".format(state.maxDrawdown)}%",
                accentColor = if (state.maxDrawdown > 15.0) SkynetAccentRed else SkynetTextDim,
                icon = Icons.Default.ShowChart,
                testTag = "metric_max_drawdown",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    accentColor: Color,
    icon: ImageVector,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SkynetPanelBg)
            .border(1.dp, SkynetPanelBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SkynetTextDim,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 17.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor.copy(alpha = 0.7f),
            modifier = Modifier.size(20.dp)
        )
    }
}
