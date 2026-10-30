package com.example.data

enum class TradeType {
    BUY, SELL
}

enum class TelemetryType {
    NORMAL, BUY, SELL, ALERT, EVO, SYSTEM
}

enum class ExecutionMode(val displayName: String) {
    PAPER("Paper Trading (Simulated)"),
    LIVE("Live Webhook API")
}

data class Broker(
    val name: String,
    val latencyMs: Int,
    val isVerified: Boolean = true
)

data class StrategyEvolution(
    val generation: Double = 1.0,
    val label: String = "Gen 1.0 (Baseline)",
    val winProbability: Double = 0.52,
    val riskRewardRatio: Double = 2.0,
    val learningRate: Double = 0.045,
    val fitnessScore: Double = 81.4
)

data class Milestone(
    val target: Double,
    val isVaulted: Boolean,
    val isCurrent: Boolean
)

data class Trade(
    val id: String,
    val timeFormatted: String,
    val symbol: String,
    val type: TradeType,
    val entryPrice: Double,
    val exitPrice: Double,
    val pnlAmount: Double,
    val pnlPercent: Double,
    val isWin: Boolean,
    val generation: Double
)

data class TelemetryEntry(
    val id: String,
    val timeFormatted: String,
    val message: String,
    val type: TelemetryType
)

data class MarketStats(
    val high24h: Double = 67200.0,
    val low24h: Double = 64100.0,
    val change24hPercent: Double = 2.45,
    val volume24h: String = "$1.82B"
)
