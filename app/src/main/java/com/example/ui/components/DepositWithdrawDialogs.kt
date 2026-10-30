package com.example.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradingRepository
import com.example.ui.theme.SkynetAccentGold
import com.example.ui.theme.SkynetAccentGreen
import com.example.ui.theme.SkynetAccentRed
import com.example.ui.theme.SkynetDarkBg
import com.example.ui.theme.SkynetPanelBg
import com.example.ui.theme.SkynetPanelBorder
import com.example.ui.theme.SkynetTextDim
import com.example.ui.theme.SkynetTextMain

@Composable
fun DepositDialog(
    onDismiss: () -> Unit,
    onConfirm: (Double) -> Unit
) {
    var amountText by remember { mutableStateOf("10.00") }
    val quickAmounts = listOf("10.00", "25.00", "50.00", "100.00")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SkynetPanelBg,
        title = {
            Text(
                text = "Deposit Capital",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SkynetAccentGreen
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Enter amount to inject into the active Skynet trading account balance.",
                    fontSize = 12.sp,
                    color = SkynetTextDim
                )

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount ($)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SkynetTextMain,
                        unfocusedTextColor = SkynetTextMain,
                        focusedBorderColor = SkynetAccentGreen,
                        unfocusedBorderColor = SkynetPanelBorder,
                        focusedLabelColor = SkynetAccentGreen,
                        unfocusedLabelColor = SkynetTextDim,
                        cursorColor = SkynetAccentGreen
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deposit_input_field")
                )

                // Quick chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickAmounts.forEach { amt ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SkynetDarkBg)
                                .border(1.dp, SkynetPanelBorder, RoundedCornerShape(4.dp))
                                .clickable { amountText = amt }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+$$amt",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SkynetAccentGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (amt > 0.0) {
                        onConfirm(amt)
                    }
                },
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkynetAccentGreen,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("confirm_deposit_button")
            ) {
                Text("Confirm Deposit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Cancel", color = SkynetTextDim)
            }
        }
    )
}

@Composable
fun WithdrawDialog(
    state: TradingRepository.State,
    onDismiss: () -> Unit,
    onConfirm: (fromVault: Boolean, amount: Double) -> Unit
) {
    var fromVault by remember { mutableStateOf(false) }
    var amountText by remember { mutableStateOf("5.00") }
    var sourceMenuExpanded by remember { mutableStateOf(false) }

    val available = if (fromVault) state.vault else state.balance
    val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
    val isOverLimit = parsedAmount > available

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SkynetPanelBg,
        title = {
            Text(
                text = "Withdraw Funds",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SkynetAccentGold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Select source account to withdraw from:",
                    fontSize = 12.sp,
                    color = SkynetTextDim
                )

                // Source selector
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .background(SkynetDarkBg)
                            .border(1.dp, SkynetPanelBorder, RoundedCornerShape(4.dp))
                            .clickable { sourceMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 9.dp)
                            .testTag("withdraw_source_selector"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (fromVault) "Secured Vault ($${"%.2f".format(state.vault)})"
                            else "Trading Balance ($${"%.2f".format(state.balance)})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (fromVault) SkynetAccentGold else SkynetAccentGreen
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = SkynetTextDim
                        )
                    }

                    DropdownMenu(
                        expanded = sourceMenuExpanded,
                        onDismissRequest = { sourceMenuExpanded = false },
                        modifier = Modifier.background(SkynetPanelBg)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Trading Balance ($${"%.2f".format(state.balance)})", color = SkynetTextMain, fontSize = 12.sp) },
                            onClick = {
                                fromVault = false
                                sourceMenuExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Secured Vault ($${"%.2f".format(state.vault)})", color = SkynetTextMain, fontSize = 12.sp) },
                            onClick = {
                                fromVault = true
                                sourceMenuExpanded = false
                            }
                        )
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Withdraw Amount ($)") },
                    isError = isOverLimit,
                    supportingText = {
                        if (isOverLimit) {
                            Text("Exceeds available balance ($${"%.2f".format(available)})", color = SkynetAccentRed)
                        } else {
                            Text("Available: $${"%.2f".format(available)}", color = SkynetTextDim)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SkynetTextMain,
                        unfocusedTextColor = SkynetTextMain,
                        focusedBorderColor = SkynetAccentGold,
                        unfocusedBorderColor = SkynetPanelBorder,
                        focusedLabelColor = SkynetAccentGold,
                        unfocusedLabelColor = SkynetTextDim,
                        cursorColor = SkynetAccentGold
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("withdraw_input_field")
                )

                // Quick percentage chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(0.25 to "25%", 0.50 to "50%", 1.0 to "MAX").forEach { (pct, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(SkynetDarkBg)
                                .border(1.dp, SkynetPanelBorder, RoundedCornerShape(4.dp))
                                .clickable {
                                    val calc = available * pct
                                    amountText = "%.2f".format(calc)
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = SkynetAccentGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (parsedAmount > 0.0 && !isOverLimit) {
                        onConfirm(fromVault, parsedAmount)
                    }
                },
                enabled = parsedAmount > 0.0 && !isOverLimit,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SkynetAccentGold,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("confirm_withdraw_button")
            ) {
                Text("Confirm Withdrawal", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text("Cancel", color = SkynetTextDim)
            }
        }
    )
}
