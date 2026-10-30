package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.data.ExecutionMode
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
fun BrokerControlPanel(
    state: TradingRepository.State,
    onToggleBot: () -> Unit,
    onSelectBroker: (String) -> Unit,
    onSelectExecutionMode: (ExecutionMode) -> Unit,
    onTestGateway: () -> Unit,
    onOptimizeStrategy: () -> Unit,
    modifier: Modifier = Modifier
) {
    var brokerMenuExpanded by remember { mutableStateOf(false) }
    var modeMenuExpanded by remember { mutableStateOf(false) }

    val botButtonColor by animateColorAsState(
        targetValue = if (state.isRunning) SkynetAccentRed else SkynetAccentBlue,
        label = "botButtonColor"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SkynetPanelBg)
            .border(1.dp, SkynetPanelBorder, RoundedCornerShape(8.dp))
            .padding(14.dp)
            .testTag("broker_control_panel"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section: Broker Gateway
        Text(
            text = "BROKER GATEWAY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SkynetTextDim,
            letterSpacing = 1.sp
        )

        // Select Regulated Broker
        Column {
            Text(
                text = "Select Regulated Broker",
                fontSize = 11.sp,
                color = SkynetTextDim
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SkynetDarkBg)
                        .border(1.dp, SkynetPanelBorder, RoundedCornerShape(4.dp))
                        .clickable { brokerMenuExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 9.dp)
                        .testTag("broker_selector"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.selectedBroker.name,
                        fontSize = 12.sp,
                        color = SkynetTextMain,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = SkynetTextDim
                    )
                }

                DropdownMenu(
                    expanded = brokerMenuExpanded,
                    onDismissRequest = { brokerMenuExpanded = false },
                    modifier = Modifier.background(SkynetPanelBg)
                ) {
                    val brokers = listOf(
                        "Interactive Brokers",
                        "OANDA",
                        "Pepperstone",
                        "Binance (Spot/Margin)",
                        "IG Group"
                    )
                    brokers.forEach { brokerName ->
                        DropdownMenuItem(
                            text = { Text(brokerName, color = SkynetTextMain, fontSize = 12.sp) },
                            onClick = {
                                onSelectBroker(brokerName)
                                brokerMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Execution Mode
        Column {
            Text(
                text = "Execution Mode",
                fontSize = 11.sp,
                color = SkynetTextDim
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SkynetDarkBg)
                        .border(1.dp, SkynetPanelBorder, RoundedCornerShape(4.dp))
                        .clickable { modeMenuExpanded = true }
                        .padding(horizontal = 10.dp, vertical = 9.dp)
                        .testTag("execution_mode_selector"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.executionMode.displayName,
                        fontSize = 12.sp,
                        color = if (state.executionMode == ExecutionMode.LIVE) SkynetAccentGold else SkynetTextMain,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Expand",
                        tint = SkynetTextDim
                    )
                }

                DropdownMenu(
                    expanded = modeMenuExpanded,
                    onDismissRequest = { modeMenuExpanded = false },
                    modifier = Modifier.background(SkynetPanelBg)
                ) {
                    ExecutionMode.entries.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.displayName, color = SkynetTextMain, fontSize = 12.sp) },
                            onClick = {
                                onSelectExecutionMode(mode)
                                modeMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Test Broker Gateway Button
        OutlinedButton(
            onClick = onTestGateway,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("test_gateway_btn"),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SkynetTextMain
            )
        ) {
            Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = SkynetAccentCyan, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Test Broker Gateway", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SkynetPanelBorder)
        )

        // Section: Skynet AI Engine (Evolving Mode)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SKYNET AI ENGINE (EVOLVING MODE)",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SkynetTextDim,
                letterSpacing = 0.5.sp
            )
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = SkynetAccentPurple,
                modifier = Modifier.size(16.dp)
            )
        }

        // Strategy Generation
        Column {
            Text("Strategy Generation", fontSize = 11.sp, color = SkynetTextDim)
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(SkynetDarkBg)
                    .border(1.dp, SkynetAccentPurple.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp)
            ) {
                Text(
                    text = state.strategy.label,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = SkynetAccentPurple
                )
            }
        }

        // AI Win Probability & Risk-Reward
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("AI Win Probability", fontSize = 11.sp, color = SkynetTextDim)
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SkynetDarkBg)
                        .border(1.dp, SkynetPanelBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "${"%.1f".format(state.strategy.winProbability * 100)}%",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = SkynetAccentGreen
                    )
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text("Risk-Reward Ratio", fontSize = 11.sp, color = SkynetTextDim)
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(SkynetDarkBg)
                        .border(1.dp, SkynetPanelBorder, RoundedCornerShape(4.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "1 : ${state.strategy.riskRewardRatio}",
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = SkynetAccentCyan
                    )
                }
            }
        }

        // Quick Optimize Strategy with Gemini 3.8 Flash
        OutlinedButton(
            onClick = onOptimizeStrategy,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("optimize_strategy_button"),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SkynetAccentPurple
            )
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = SkynetAccentPurple,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Recalibrate Genetic Strategy (Gemini-3.8-Flash)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(SkynetPanelBorder)
        )

        // Bot Control Action Button
        Button(
            onClick = onToggleBot,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("toggle_bot_btn"),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = botButtonColor,
                contentColor = Color.White
            )
        ) {
            Icon(
                imageVector = if (state.isRunning) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (state.isRunning) "Stop Skynet Engine" else "Start Skynet Engine",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
