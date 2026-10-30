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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Milestone
import com.example.data.TradingRepository
import com.example.ui.theme.SkynetAccentCyan
import com.example.ui.theme.SkynetAccentGold
import com.example.ui.theme.SkynetAccentGreen
import com.example.ui.theme.SkynetDarkBg
import com.example.ui.theme.SkynetPanelBg
import com.example.ui.theme.SkynetPanelBorder
import com.example.ui.theme.SkynetTextDim
import com.example.ui.theme.SkynetTextMain

@Composable
fun StageGateMilestones(
    state: TradingRepository.State,
    modifier: Modifier = Modifier
) {
    val currentTarget = state.stages.getOrNull(state.currentStageIdx) ?: 1000.0
    val prevTarget = if (state.currentStageIdx > 0) state.stages[state.currentStageIdx - 1] else 0.0
    val progress = ((state.balance - prevTarget) / (currentTarget - prevTarget).coerceAtLeast(1.0))
        .coerceIn(0.0, 1.0)
        .toFloat()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SkynetPanelBg)
            .border(1.dp, SkynetPanelBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .testTag("stagegate_milestones_container")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrackChanges,
                    contentDescription = null,
                    tint = SkynetAccentGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STAGEGATE MILESTONES (AUTO-VAULT)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SkynetTextDim,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "Next: $${"%.1f".format(currentTarget)}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = SkynetAccentGold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Progress towards next milestone
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = SkynetAccentGreen,
            trackColor = SkynetDarkBg
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Milestone chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(state.milestones) { index, milestone ->
                MilestoneItemChip(milestone = milestone, index = index)
            }
        }
    }
}

@Composable
fun MilestoneItemChip(
    milestone: Milestone,
    index: Int
) {
    val borderColor = when {
        milestone.isCurrent -> SkynetAccentGreen
        milestone.isVaulted -> SkynetAccentGold.copy(alpha = 0.5f)
        else -> SkynetPanelBorder
    }

    val bgColor = when {
        milestone.isCurrent -> SkynetAccentGreen.copy(alpha = 0.12f)
        milestone.isVaulted -> SkynetAccentGold.copy(alpha = 0.08f)
        else -> SkynetDarkBg
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Target: $${"%.1f".format(milestone.target)}",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (milestone.isCurrent) SkynetAccentGreen else SkynetTextMain
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = when {
                        milestone.isVaulted -> Icons.Default.CheckCircle
                        milestone.isCurrent -> Icons.Default.TrackChanges
                        else -> Icons.Default.Lock
                    },
                    contentDescription = null,
                    tint = when {
                        milestone.isVaulted -> SkynetAccentGold
                        milestone.isCurrent -> SkynetAccentGreen
                        else -> SkynetTextDim
                    },
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when {
                        milestone.isVaulted -> "Vaulted"
                        milestone.isCurrent -> "Active"
                        else -> "Pending"
                    },
                    fontSize = 10.sp,
                    color = when {
                        milestone.isVaulted -> SkynetAccentGold
                        milestone.isCurrent -> SkynetAccentGreen
                        else -> SkynetTextDim
                    }
                )
            }
        }
    }
}
