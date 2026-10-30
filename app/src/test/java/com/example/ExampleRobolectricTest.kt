package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ExecutionMode
import com.example.data.TradingRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Skynet", appName)
  }

  @Test
  fun `trading repository deposit and toggle bot`() {
    val repository = TradingRepository()
    val initialState = repository.state.value

    assertEquals(10.0, initialState.balance, 0.001)
    assertEquals(0.0, initialState.vault, 0.001)
    assertFalse(initialState.isRunning)

    // Deposit $25
    val deposited = repository.deposit(25.0)
    assertTrue(deposited)
    assertEquals(35.0, repository.state.value.balance, 0.001)

    // Toggle Bot
    val isRunning = repository.toggleBot()
    assertTrue(isRunning)
    assertTrue(repository.state.value.isRunning)

    // Withdraw test
    val (success, _) = repository.withdraw(fromVault = false, amount = 5.0)
    assertTrue(success)
    assertEquals(30.0, repository.state.value.balance, 0.001)

    // Broker test
    repository.setBroker("Binance (Spot/Margin)")
    assertEquals("Binance (Spot/Margin)", repository.state.value.selectedBroker.name)

    // Execution mode test
    repository.setExecutionMode(ExecutionMode.LIVE)
    assertEquals(ExecutionMode.LIVE, repository.state.value.executionMode)
  }
}
