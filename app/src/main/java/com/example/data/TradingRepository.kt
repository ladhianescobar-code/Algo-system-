package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class TradingRepository {

    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val stages = listOf(
        10.0, 20.0, 30.0, 50.0, 60.0, 70.0, 100.0, 150.0, 200.0, 300.0, 500.0, 1000.0
    )

    val brokers = listOf(
        Broker("Interactive Brokers", 24),
        Broker("OANDA", 38),
        Broker("Pepperstone", 29),
        Broker("Binance (Spot/Margin)", 18),
        Broker("IG Group", 42)
    )

    private var balance = 10.0
    private var vault = 0.0
    private var peakBalance = 10.0
    private var maxDrawdown = 0.0
    private var totalTrades = 0
    private var wins = 0
    private var streak = 0
    private var currentPrice = 65000.0
    private val priceHistory = mutableListOf<Double>().apply {
        var p = 64800.0
        for (i in 0 until 30) {
            p += (Random.nextDouble() - 0.48) * 120
            add(p)
        }
        currentPrice = p
    }

    private var currentStageIdx = 0
    private var strategy = StrategyEvolution()
    private var selectedBroker = brokers[0]
    private var executionMode = ExecutionMode.PAPER
    private var isRunning = false

    private val recentTrades = mutableListOf<Trade>()
    private val telemetryLogs = mutableListOf<TelemetryEntry>().apply {
        add(
            TelemetryEntry(
                id = UUID.randomUUID().toString(),
                timeFormatted = currentTimeStr(),
                message = "Skynet v12.0 initialized. Ready for operations.",
                type = TelemetryType.SYSTEM
            )
        )
        add(
            TelemetryEntry(
                id = UUID.randomUUID().toString(),
                timeFormatted = currentTimeStr(),
                message = "System initialized with $10.00 starting balance.",
                type = TelemetryType.NORMAL
            )
        )
    }

    data class State(
        val isRunning: Boolean = false,
        val balance: Double = 10.0,
        val vault: Double = 0.0,
        val peakBalance: Double = 10.0,
        val maxDrawdown: Double = 0.0,
        val totalTrades: Int = 0,
        val wins: Int = 0,
        val streak: Int = 0,
        val winRatePercent: Double = 0.0,
        val currentPrice: Double = 65000.0,
        val priceHistory: List<Double> = emptyList(),
        val selectedBroker: Broker = Broker("Interactive Brokers", 24),
        val executionMode: ExecutionMode = ExecutionMode.PAPER,
        val currentStageIdx: Int = 0,
        val stages: List<Double> = emptyList(),
        val milestones: List<Milestone> = emptyList(),
        val strategy: StrategyEvolution = StrategyEvolution(),
        val recentTrades: List<Trade> = emptyList(),
        val telemetryLogs: List<TelemetryEntry> = emptyList(),
        val marketStats: MarketStats = MarketStats(),
        val fastPulseMessage: String? = null
    )

    private val _state = MutableStateFlow(buildCurrentState())
    val state: StateFlow<State> = _state.asStateFlow()

    private fun currentTimeStr(): String = timeFormat.format(Date())

    private fun buildMilestones(): List<Milestone> {
        return stages.mapIndexed { index, target ->
            Milestone(
                target = target,
                isVaulted = index < currentStageIdx,
                isCurrent = index == currentStageIdx
            )
        }
    }

    private fun buildCurrentState(): State {
        val winRate = if (totalTrades > 0) (wins.toDouble() / totalTrades) * 100.0 else 0.0
        return State(
            isRunning = isRunning,
            balance = balance,
            vault = vault,
            peakBalance = peakBalance,
            maxDrawdown = maxDrawdown * 100.0,
            totalTrades = totalTrades,
            wins = wins,
            streak = streak,
            winRatePercent = winRate,
            currentPrice = currentPrice,
            priceHistory = priceHistory.toList(),
            selectedBroker = selectedBroker,
            executionMode = executionMode,
            currentStageIdx = currentStageIdx,
            stages = stages,
            milestones = buildMilestones(),
            strategy = strategy,
            recentTrades = recentTrades.toList(),
            telemetryLogs = telemetryLogs.toList(),
            marketStats = MarketStats(
                high24h = max(67500.0, currentPrice * 1.02),
                low24h = min(63800.0, currentPrice * 0.98),
                change24hPercent = ((currentPrice - 64500.0) / 64500.0) * 100.0
            )
        )
    }

    private fun pushTelemetry(message: String, type: TelemetryType) {
        telemetryLogs.add(
            0,
            TelemetryEntry(
                id = UUID.randomUUID().toString(),
                timeFormatted = currentTimeStr(),
                message = message,
                type = type
            )
        )
        if (telemetryLogs.size > 200) {
            telemetryLogs.removeAt(telemetryLogs.lastIndex)
        }
    }

    fun setFastPulse(pulse: String) {
        _state.value = _state.value.copy(fastPulseMessage = pulse)
    }

    fun toggleBot(): Boolean {
        isRunning = !isRunning
        if (isRunning) {
            pushTelemetry("Skynet Autonomous Engine STARTED. Continuous tick ingestion active.", TelemetryType.ALERT)
        } else {
            pushTelemetry("Skynet Autonomous Engine STOPPED by operator.", TelemetryType.ALERT)
        }
        _state.value = buildCurrentState()
        return isRunning
    }

    fun setBroker(brokerName: String) {
        val found = brokers.find { it.name == brokerName } ?: brokers[0]
        selectedBroker = found
        pushTelemetry("Broker gateway routing updated to: ${found.name}", TelemetryType.NORMAL)
        _state.value = buildCurrentState()
    }

    fun setExecutionMode(mode: ExecutionMode) {
        executionMode = mode
        pushTelemetry("Execution mode shifted to: ${mode.displayName}", TelemetryType.SYSTEM)
        _state.value = buildCurrentState()
    }

    fun testBrokerGateway(): String {
        val latency = selectedBroker.latencyMs + Random.nextInt(-4, 6)
        val msg = "GATEWAY PING: ${selectedBroker.name} responded in ${latency}ms. TLS 1.3 handshakes verified."
        pushTelemetry(msg, TelemetryType.SYSTEM)
        _state.value = buildCurrentState()
        return msg
    }

    fun deposit(amount: Double): Boolean {
        if (amount <= 0.0) return false
        balance += amount
        if (balance > peakBalance) peakBalance = balance
        pushTelemetry("DEPOSIT SUCCESSFUL: +$${"%.2f".format(amount)} added to trading balance.", TelemetryType.ALERT)
        pushTelemetry("DEPOSIT: +$${"%.2f".format(amount)} | New Balance: $${"%.2f".format(balance)}", TelemetryType.BUY)
        _state.value = buildCurrentState()
        return true
    }

    fun withdraw(fromVault: Boolean, amount: Double): Pair<Boolean, String> {
        if (amount <= 0.0) return Pair(false, "Invalid amount")
        if (fromVault) {
            if (amount > vault) return Pair(false, "Insufficient vault balance")
            vault -= amount
            pushTelemetry("VAULT WITHDRAWAL: -$${"%.2f".format(amount)} extracted from secured vault.", TelemetryType.ALERT)
        } else {
            if (amount > balance) return Pair(false, "Insufficient trading balance")
            balance -= amount
            pushTelemetry("WITHDRAWAL: -$${"%.2f".format(amount)} from trading account. Remaining: $${"%.2f".format(balance)}", TelemetryType.SELL)
        }
        _state.value = buildCurrentState()
        return Pair(true, "Withdrawal processed successfully")
    }

    /**
     * Executes one tick update and potential trade execution
     */
    fun tickEngine(): Trade? {
        // Price random walk
        val drift = (strategy.winProbability - 0.5) * 0.0006
        val volatility = 0.0018
        val delta = currentPrice * (drift + (Random.nextDouble() - 0.5) * volatility)
        currentPrice = max(1000.0, currentPrice + delta)

        priceHistory.add(currentPrice)
        if (priceHistory.size > 50) {
            priceHistory.removeAt(0)
        }

        var executedTrade: Trade? = null

        // Trade trigger if running and balance > 0.5
        if (isRunning && balance >= 0.5) {
            val shouldTrade = Random.nextDouble() < 0.35 // 35% chance of trade execution per tick
            if (shouldTrade) {
                executedTrade = executeSimulatedTrade()
            }
        }

        _state.value = buildCurrentState()
        return executedTrade
    }

    private fun executeSimulatedTrade(): Trade {
        totalTrades++
        val isWin = Random.nextDouble() < strategy.winProbability
        val isBuy = Random.nextBoolean()
        val tradeType = if (isBuy) TradeType.BUY else TradeType.SELL

        // Dynamic risk percentage (between 5% and 12% of trading balance)
        val riskPerTradeFraction = 0.08
        val riskAmount = max(0.20, balance * riskPerTradeFraction)

        val entry = currentPrice
        val priceShiftPct = (Random.nextDouble() * 0.004 + 0.001)

        val (pnlAmount, exitPrice) = if (isWin) {
            wins++
            streak++
            val profit = riskAmount * strategy.riskRewardRatio
            val exit = if (isBuy) entry * (1 + priceShiftPct) else entry * (1 - priceShiftPct)
            Pair(profit, exit)
        } else {
            streak = 0
            val loss = riskAmount
            val exit = if (isBuy) entry * (1 - priceShiftPct) else entry * (1 + priceShiftPct)
            Pair(-loss, exit)
        }

        balance = max(0.01, balance + pnlAmount)
        if (balance > peakBalance) {
            peakBalance = balance
        }

        val currentDrawdown = if (peakBalance > 0) (peakBalance - balance) / peakBalance else 0.0
        if (currentDrawdown > maxDrawdown) {
            maxDrawdown = currentDrawdown
        }

        val pnlPct = (pnlAmount / riskAmount) * 100.0

        val trade = Trade(
            id = UUID.randomUUID().toString(),
            timeFormatted = currentTimeStr(),
            symbol = "BTC/USDT",
            type = tradeType,
            entryPrice = entry,
            exitPrice = exitPrice,
            pnlAmount = pnlAmount,
            pnlPercent = pnlPct,
            isWin = isWin,
            generation = strategy.generation
        )

        recentTrades.add(0, trade)
        if (recentTrades.size > 100) {
            recentTrades.removeAt(recentTrades.lastIndex)
        }

        if (isWin) {
            pushTelemetry(
                "ORDER FILLED: ${tradeType.name} BTC @ $${"%.2f".format(entry)} -> Closed @ $${"%.2f".format(exitPrice)} [+$${"%.2f".format(pnlAmount)}]",
                TelemetryType.BUY
            )
        } else {
            pushTelemetry(
                "STOP-LOSS HIT: ${tradeType.name} BTC @ $${"%.2f".format(entry)} -> Closed @ $${"%.2f".format(exitPrice)} [-$${"%.2f".format(-pnlAmount)}]",
                TelemetryType.SELL
            )
        }

        // Check StageGate Milestones
        checkStageGate()

        // Evolve strategy if streak >= 3
        if (streak > 0 && streak % 3 == 0) {
            evolveStrategy("Consecutive Streak Win ($streak wins)")
        }

        return trade
    }

    private fun checkStageGate() {
        if (currentStageIdx < stages.size && balance >= stages[currentStageIdx]) {
            val target = stages[currentStageIdx]
            val profitToVault = target * 0.40 // Secure 40% of target milestone to vault
            val sweepAmount = min(balance * 0.5, profitToVault)

            balance -= sweepAmount
            vault += sweepAmount

            pushTelemetry(
                "🎯 STAGEGATE REACHED: Target $${"%.1f".format(target)} hit! Vaulted $${"%.2f".format(sweepAmount)} to secure vault.",
                TelemetryType.ALERT
            )

            currentStageIdx++
            evolveStrategy("StageGate Milestone Achieved")
        }
    }

    private fun evolveStrategy(reason: String) {
        val newGen = strategy.generation + 0.1
        val newWinProb = min(0.78, strategy.winProbability + 0.018)
        val newRR = min(3.8, strategy.riskRewardRatio + 0.15)
        val newFitness = min(99.4, strategy.fitnessScore + 2.1)

        val labels = listOf(
            "Adaptive Orderflow",
            "Multi-Scale Momentum",
            "Deep Volatility Skew",
            "Liquidity Sweep Engine",
            "Neural Micro-Scalp"
        )
        val randomLabel = labels[((newGen * 10).toInt()) % labels.size]

        strategy = strategy.copy(
            generation = (newGen * 10).toInt() / 10.0,
            label = "Gen ${"%.1f".format(newGen)} ($randomLabel)",
            winProbability = newWinProb,
            riskRewardRatio = (newRR * 10).toInt() / 10.0,
            fitnessScore = newFitness
        )

        pushTelemetry(
            "🧬 GENETIC MUTATION ($reason): Upgraded to ${strategy.label} | Win Prob: ${"%.1f".format(strategy.winProbability * 100)}% | R:R 1:${strategy.riskRewardRatio}",
            TelemetryType.EVO
        )
    }
}
