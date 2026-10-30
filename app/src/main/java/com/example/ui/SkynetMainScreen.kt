package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AiAuditSheet
import com.example.ui.components.BrokerControlPanel
import com.example.ui.components.DepositDialog
import com.example.ui.components.MetricsOverview
import com.example.ui.components.RealtimePriceChart
import com.example.ui.components.StageGateMilestones
import com.example.ui.components.StrategyOptimizerDialog
import com.example.ui.components.TelemetryAndTxLog
import com.example.ui.components.WithdrawDialog
import com.example.ui.theme.SkynetAccentCyan
import com.example.ui.theme.SkynetAccentGold
import com.example.ui.theme.SkynetAccentGreen
import com.example.ui.theme.SkynetAccentPurple
import com.example.ui.theme.SkynetDarkBg
import com.example.ui.theme.SkynetPanelBg
import com.example.ui.theme.SkynetPanelBorder
import com.example.ui.theme.SkynetTextDim
import com.example.ui.theme.SkynetTextMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkynetMainScreen(
    viewModel: SkynetViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.tradingState.collectAsStateWithLifecycle()
    val dialogState by viewModel.dialogState.collectAsStateWithLifecycle()
    val aiAuditState by viewModel.aiAuditState.collectAsStateWithLifecycle()
    val optimizerResult by viewModel.optimizerResult.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(dialogState.toastMessage) {
        val msg = dialogState.toastMessage
        if (!msg.isNullOrEmpty()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("skynet_main_screen"),
        containerColor = SkynetDarkBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SkynetPanelBg,
                    titleContentColor = SkynetAccentGold
                ),
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "🤖 SKYNET v12.0",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = SkynetAccentGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (state.isRunning) SkynetAccentGreen.copy(alpha = 0.2f) else SkynetPanelBorder)
                                    .border(
                                        1.dp,
                                        if (state.isRunning) SkynetAccentGreen else SkynetTextDim,
                                        RoundedCornerShape(3.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (state.isRunning) "ACTIVE (GEN ${"%.1f".format(state.strategy.generation)})"
                                    else "READY ($${"%.2f".format(state.balance)})",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isRunning) SkynetAccentGreen else SkynetTextDim
                                )
                            }
                        }
                        Text(
                            text = "Evolving Algorithmic Terminal",
                            fontSize = 10.sp,
                            color = SkynetTextDim
                        )
                    }
                },
                actions = {
                    // Deposit Button
                    Button(
                        onClick = { viewModel.openDepositDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkynetAccentGreen,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_deposit_header")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Deposit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Withdraw Button
                    Button(
                        onClick = { viewModel.openWithdrawDialog() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkynetAccentGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_withdraw_header")
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Withdraw", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Deep AI Audit Button
                    Button(
                        onClick = { viewModel.openAiAuditSheet() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SkynetAccentPurple,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("btn_ai_audit_header")
                    ) {
                        Icon(imageVector = Icons.Default.Psychology, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("AI Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isWideScreen = maxWidth >= 760.dp

            if (isWideScreen) {
                // Adaptive Tablet / Wide Layout (Two Columns)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Column: Control Panel & Milestones
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BrokerControlPanel(
                            state = state,
                            onToggleBot = { viewModel.toggleBot() },
                            onSelectBroker = { viewModel.selectBroker(it) },
                            onSelectExecutionMode = { viewModel.selectExecutionMode(it) },
                            onTestGateway = { viewModel.testBrokerGateway() },
                            onOptimizeStrategy = { viewModel.runStrategyOptimizer() }
                        )

                        StageGateMilestones(state = state)
                    }

                    // Right Column: Metrics, Chart & Telemetry Log
                    Column(
                        modifier = Modifier
                            .weight(1.3f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricsOverview(state = state)

                        RealtimePriceChart(
                            state = state,
                            onRefreshPulse = { viewModel.runFastMarketPulse() }
                        )

                        TelemetryAndTxLog(state = state)
                    }
                }
            } else {
                // Mobile Portrait Layout (Vertical Stack)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricsOverview(state = state)

                    RealtimePriceChart(
                        state = state,
                        onRefreshPulse = { viewModel.runFastMarketPulse() }
                    )

                    StageGateMilestones(state = state)

                    BrokerControlPanel(
                        state = state,
                        onToggleBot = { viewModel.toggleBot() },
                        onSelectBroker = { viewModel.selectBroker(it) },
                        onSelectExecutionMode = { viewModel.selectExecutionMode(it) },
                        onTestGateway = { viewModel.testBrokerGateway() },
                        onOptimizeStrategy = { viewModel.runStrategyOptimizer() }
                    )

                    TelemetryAndTxLog(state = state)

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

    // Modals & Bottom Sheets
    if (dialogState.showDepositDialog) {
        DepositDialog(
            onDismiss = { viewModel.closeDepositDialog() },
            onConfirm = { amount -> viewModel.executeDeposit(amount) }
        )
    }

    if (dialogState.showWithdrawDialog) {
        WithdrawDialog(
            state = state,
            onDismiss = { viewModel.closeWithdrawDialog() },
            onConfirm = { fromVault, amount -> viewModel.executeWithdraw(fromVault, amount) }
        )
    }

    if (dialogState.showAiAuditSheet) {
        AiAuditSheet(
            auditState = aiAuditState,
            onDismiss = { viewModel.closeAiAuditSheet() },
            onSubmitPrompt = { query -> viewModel.startDeepNeuralAudit(query) }
        )
    }

    if (dialogState.showOptimizerDialog) {
        StrategyOptimizerDialog(
            resultText = optimizerResult,
            onDismiss = { viewModel.closeOptimizerDialog() }
        )
    }
}
