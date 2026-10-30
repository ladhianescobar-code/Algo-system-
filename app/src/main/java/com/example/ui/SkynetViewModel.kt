package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiTradingAdvisor
import com.example.data.ExecutionMode
import com.example.data.TradingRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

sealed interface AiAuditState {
    data object Idle : AiAuditState
    data class Streaming(val thoughtsAndContent: String) : AiAuditState
    data class Completed(val report: String) : AiAuditState
    data class Error(val message: String) : AiAuditState
}

data class DialogState(
    val showDepositDialog: Boolean = false,
    val showWithdrawDialog: Boolean = false,
    val showAiAuditSheet: Boolean = false,
    val showOptimizerDialog: Boolean = false,
    val toastMessage: String? = null
)

class SkynetViewModel(
    private val repository: TradingRepository = TradingRepository(),
    private val aiAdvisor: GeminiTradingAdvisor = GeminiTradingAdvisor()
) : ViewModel() {

    val tradingState: StateFlow<TradingRepository.State> = repository.state

    private val _dialogState = MutableStateFlow(DialogState())
    val dialogState: StateFlow<DialogState> = _dialogState.asStateFlow()

    private val _aiAuditState = MutableStateFlow<AiAuditState>(AiAuditState.Idle)
    val aiAuditState: StateFlow<AiAuditState> = _aiAuditState.asStateFlow()

    private val _optimizerResult = MutableStateFlow<String?>(null)
    val optimizerResult: StateFlow<String?> = _optimizerResult.asStateFlow()

    private var engineJob: Job? = null
    private var auditJob: Job? = null

    init {
        startTickTimer()
    }

    private fun startTickTimer() {
        engineJob?.cancel()
        engineJob = viewModelScope.launch {
            while (isActive) {
                // Ticks occur every 800ms for lively chart updates and trading cadence
                repository.tickEngine()
                delay(800L)
            }
        }
    }

    fun toggleBot() {
        repository.toggleBot()
    }

    fun selectBroker(brokerName: String) {
        repository.setBroker(brokerName)
    }

    fun selectExecutionMode(mode: ExecutionMode) {
        repository.setExecutionMode(mode)
    }

    fun testBrokerGateway() {
        val result = repository.testBrokerGateway()
        _dialogState.value = _dialogState.value.copy(toastMessage = result)
    }

    fun openDepositDialog() {
        _dialogState.value = _dialogState.value.copy(showDepositDialog = true)
    }

    fun closeDepositDialog() {
        _dialogState.value = _dialogState.value.copy(showDepositDialog = false)
    }

    fun executeDeposit(amount: Double) {
        val success = repository.deposit(amount)
        if (success) {
            _dialogState.value = _dialogState.value.copy(
                showDepositDialog = false,
                toastMessage = "Deposit of $${"%.2f".format(amount)} completed."
            )
        }
    }

    fun openWithdrawDialog() {
        _dialogState.value = _dialogState.value.copy(showWithdrawDialog = true)
    }

    fun closeWithdrawDialog() {
        _dialogState.value = _dialogState.value.copy(showWithdrawDialog = false)
    }

    fun executeWithdraw(fromVault: Boolean, amount: Double) {
        val (success, msg) = repository.withdraw(fromVault, amount)
        if (success) {
            _dialogState.value = _dialogState.value.copy(
                showWithdrawDialog = false,
                toastMessage = msg
            )
        } else {
            _dialogState.value = _dialogState.value.copy(toastMessage = msg)
        }
    }

    fun openAiAuditSheet() {
        _dialogState.value = _dialogState.value.copy(showAiAuditSheet = true)
        if (_aiAuditState.value is AiAuditState.Idle) {
            startDeepNeuralAudit()
        }
    }

    fun closeAiAuditSheet() {
        _dialogState.value = _dialogState.value.copy(showAiAuditSheet = false)
    }

    fun startDeepNeuralAudit(userPrompt: String? = null) {
        auditJob?.cancel()
        auditJob = viewModelScope.launch {
            _aiAuditState.value = AiAuditState.Streaming("Connecting to Gemini-4-Argon High Thinking neural network...")
            val state = repository.state.value
            val buffer = StringBuilder()

            try {
                aiAdvisor.streamDeepNeuralAudit(
                    balance = state.balance,
                    vault = state.vault,
                    winRate = if (state.totalTrades > 0) state.wins.toDouble() / state.totalTrades else 0.52,
                    maxDrawdown = state.maxDrawdown / 100.0,
                    currentPrice = state.currentPrice,
                    strategy = state.strategy,
                    recentTrades = state.recentTrades,
                    userQuery = userPrompt
                ).collect { chunk ->
                    buffer.append(chunk)
                    _aiAuditState.value = AiAuditState.Streaming(buffer.toString())
                }
                if (buffer.isNotEmpty()) {
                    _aiAuditState.value = AiAuditState.Completed(buffer.toString())
                } else {
                    _aiAuditState.value = AiAuditState.Error("Neural audit returned empty response.")
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _aiAuditState.value = AiAuditState.Error(e.localizedMessage ?: "Neural audit encountered an error.")
            }
        }
    }

    fun runStrategyOptimizer() {
        viewModelScope.launch {
            val state = repository.state.value
            _dialogState.value = _dialogState.value.copy(
                showOptimizerDialog = true,
                toastMessage = "Optimizing genetic parameters with Gemini-3.8-Flash..."
            )
            _optimizerResult.value = "Analyzing parameter convergence..."
            val result = aiAdvisor.optimizeStrategy(
                balance = state.balance,
                winRate = if (state.totalTrades > 0) state.wins.toDouble() / state.totalTrades else 0.52,
                strategy = state.strategy
            )
            _optimizerResult.value = result
        }
    }

    fun closeOptimizerDialog() {
        _dialogState.value = _dialogState.value.copy(showOptimizerDialog = false)
        _optimizerResult.value = null
    }

    fun runFastMarketPulse() {
        viewModelScope.launch {
            val state = repository.state.value
            val lastType = state.recentTrades.firstOrNull()?.type?.name
            val pulse = aiAdvisor.getFastPulse(state.currentPrice, lastType)
            repository.setFastPulse(pulse)
        }
    }

    fun clearToast() {
        _dialogState.value = _dialogState.value.copy(toastMessage = null)
    }

    override fun onCleared() {
        super.onCleared()
        engineJob?.cancel()
        auditJob?.cancel()
    }
}
