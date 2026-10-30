package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiAuditState
import com.example.ui.theme.SkynetAccentBlue
import com.example.ui.theme.SkynetAccentCyan
import com.example.ui.theme.SkynetAccentGold
import com.example.ui.theme.SkynetAccentPurple
import com.example.ui.theme.SkynetAccentRed
import com.example.ui.theme.SkynetDarkBg
import com.example.ui.theme.SkynetPanelBg
import com.example.ui.theme.SkynetPanelBorder
import com.example.ui.theme.SkynetTextDim
import com.example.ui.theme.SkynetTextMain

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAuditSheet(
    auditState: AiAuditState,
    onDismiss: () -> Unit,
    onSubmitPrompt: (String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var customQueryText by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SkynetPanelBg,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(SkynetPanelBorder)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("ai_audit_bottom_sheet")
        ) {
            // Header with Model & Thinking Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = SkynetAccentPurple,
                        modifier = Modifier
                            .size(24.dp)
                            .rotate(if (auditState is AiAuditState.Streaming) rotation else 0f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SKYNET DEEP NEURAL AUDIT",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SkynetTextMain
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Model: gemini-4-argon",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SkynetAccentCyan
                            )
                            Text(
                                text = "• ThinkingLevel.HIGH",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SkynetAccentPurple
                            )
                        }
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = SkynetTextDim)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Audit Content Scrollable Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SkynetDarkBg)
                    .border(1.dp, SkynetPanelBorder, RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                when (auditState) {
                    is AiAuditState.Idle -> {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("Ready to initiate deep neural audit.", color = SkynetTextDim, fontSize = 12.sp)
                        }
                    }
                    is AiAuditState.Streaming -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = SkynetAccentPurple
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Synthesizing market depth & thinking tokens...",
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = SkynetAccentPurple
                                )
                            }
                            Text(
                                text = auditState.thoughtsAndContent,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SkynetTextMain
                            )
                        }
                    }
                    is AiAuditState.Completed -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = SkynetAccentCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Audit Report Certified by Gemini-4-Argon",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SkynetAccentCyan
                                )
                            }
                            Text(
                                text = auditState.report,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SkynetTextMain
                            )
                        }
                    }
                    is AiAuditState.Error -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Deep Neural Audit Notice",
                                fontWeight = FontWeight.Bold,
                                color = SkynetAccentRed,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = auditState.message,
                                color = SkynetTextDim,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { onSubmitPrompt("Audit the portfolio and risk-reward profile.") },
                                colors = ButtonDefaults.buttonColors(containerColor = SkynetAccentBlue),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text("Retry Audit")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // User follow-up query input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = customQueryText,
                    onValueChange = { customQueryText = it },
                    placeholder = { Text("Ask deep quant question (e.g. 'Volatility crash test')", fontSize = 11.sp, color = SkynetTextDim) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SkynetTextMain,
                        unfocusedTextColor = SkynetTextMain,
                        focusedBorderColor = SkynetAccentPurple,
                        unfocusedBorderColor = SkynetPanelBorder,
                        cursorColor = SkynetAccentPurple
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_audit_input_field")
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        val q = customQueryText.trim()
                        if (q.isNotEmpty()) {
                            onSubmitPrompt(q)
                            customQueryText = ""
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SkynetAccentPurple),
                    modifier = Modifier.testTag("send_ai_query_button")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun StrategyOptimizerDialog(
    resultText: String?,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SkynetPanelBg,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = SkynetAccentPurple,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Genetic Recalibration (Gemini-3.8-Flash)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkynetAccentPurple
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(SkynetDarkBg)
                    .border(1.dp, SkynetPanelBorder, RoundedCornerShape(6.dp))
                    .padding(12.dp)
            ) {
                if (resultText == null || resultText == "Analyzing parameter convergence...") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = SkynetAccentPurple
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Analyzing genetic fitness and trailing stops...",
                            fontSize = 11.sp,
                            color = SkynetTextDim
                        )
                    }
                } else {
                    Text(
                        text = resultText,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        color = SkynetTextMain,
                        lineHeight = 18.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SkynetAccentPurple)
            ) {
                Text("Apply & Close", fontWeight = FontWeight.Bold)
            }
        }
    )
}
