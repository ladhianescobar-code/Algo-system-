package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TelemetryEntry
import com.example.data.TelemetryType
import com.example.data.Trade
import com.example.data.TradeType
import com.example.data.TradingRepository
import com.example.ui.theme.SkynetAccentBlue
import com.example.ui.theme.SkynetAccentCyan
import com.example.ui.theme.SkynetAccentGold
import com.example.ui.theme.SkynetAccentGreen
import com.example.ui.theme.SkynetAccentPurple
import com.example.ui.theme.SkynetAccentRed
import com.example.ui.theme.SkynetDarkBg
import com.example.ui.theme.SkynetPanelBg
import com.example.ui.theme.SkynetPanelBorder
import com.example.ui.theme.SkynetTextDim
import com.example.ui.theme.SkynetTextMain

@Composable
fun TelemetryAndTxLog(
    state: TradingRepository.State,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Telemetry Log", "Tx History (${state.recentTrades.size})")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SkynetPanelBg)
            .border(1.dp, SkynetPanelBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("telemetry_tx_container")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = SkynetPanelBg,
            contentColor = SkynetAccentBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = SkynetAccentBlue
                )
            },
            divider = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(SkynetPanelBorder)
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.testTag("tab_${if (index == 0) "telemetry" else "tx"}"),
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) SkynetAccentBlue else SkynetTextDim
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp, max = 280.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(SkynetDarkBg)
                .border(1.dp, SkynetPanelBorder.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(8.dp)
        ) {
            if (selectedTabIndex == 0) {
                TelemetryLogView(logs = state.telemetryLogs)
            } else {
                TxHistoryView(trades = state.recentTrades)
            }
        }
    }
}

@Composable
fun TelemetryLogView(logs: List<TelemetryEntry>) {
    if (logs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No telemetry events recorded.", color = SkynetTextDim, fontSize = 11.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(logs, key = { it.id }) { entry ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "[${entry.timeFormatted}] ",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = SkynetTextDim
                    )
                    Text(
                        text = entry.message,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = if (entry.type != TelemetryType.NORMAL) FontWeight.SemiBold else FontWeight.Normal,
                        color = when (entry.type) {
                            TelemetryType.BUY -> SkynetAccentGreen
                            TelemetryType.SELL -> SkynetAccentRed
                            TelemetryType.ALERT -> SkynetAccentGold
                            TelemetryType.EVO -> SkynetAccentPurple
                            TelemetryType.SYSTEM -> SkynetAccentCyan
                            TelemetryType.NORMAL -> SkynetTextMain
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TxHistoryView(trades: List<Trade>) {
    if (trades.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("No trades executed yet. Start engine to begin trading.", color = SkynetTextDim, fontSize = 11.sp)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(trades, key = { it.id }) { trade ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SkynetPanelBg)
                        .border(
                            1.dp,
                            if (trade.isWin) SkynetAccentGreen.copy(alpha = 0.25f) else SkynetAccentRed.copy(alpha = 0.25f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (trade.type == TradeType.BUY) SkynetAccentGreen else SkynetAccentRed)
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = trade.type.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = trade.symbol,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SkynetTextMain
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = trade.timeFormatted,
                                fontSize = 10.sp,
                                color = SkynetTextDim
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "In: $${"%,.0f".format(trade.entryPrice)} -> Out: $${"%,.0f".format(trade.exitPrice)}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SkynetTextDim
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${if (trade.pnlAmount >= 0) "+" else ""}$${"%.2f".format(trade.pnlAmount)}",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trade.isWin) SkynetAccentGreen else SkynetAccentRed
                        )
                        Text(
                            text = "Gen ${"%.1f".format(trade.generation)}",
                            fontSize = 9.sp,
                            color = SkynetAccentPurple
                        )
                    }
                }
            }
        }
    }
}
