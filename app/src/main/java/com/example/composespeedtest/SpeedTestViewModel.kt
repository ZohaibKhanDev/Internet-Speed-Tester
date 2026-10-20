
package com.example.composespeedtest

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpeedTestViewModel(application: Application) : AndroidViewModel(application) {
    
    private val speedTestService = SpeedTestService(application)
    
    private val _uiState = MutableStateFlow(SpeedTestUiState())
    val uiState: StateFlow<SpeedTestUiState> = _uiState.asStateFlow()

    private var speedTestJob: Job? = null

    fun startSpeedTest() {
        if (_uiState.value.isTestRunning) return

        speedTestJob?.cancel()

        _uiState.value = _uiState.value.copy(
            isTestRunning = true,
            progress = 0f,
            currentTest = "Preparing measurement...",
            errorMessage = null,
            hasResults = false,
            instantSamples = emptyList(),
            minSpeed = 0f,
            avgSpeed = 0f,
            maxSpeed = 0f
        )

        speedTestJob = viewModelScope.launch {
            try {
                speedTestService.performSpeedTest(
                    onProgress = { progress ->
                        _uiState.value = _uiState.value.copy(
                            progress = progress.coerceIn(0f, 1f)
                        )
                    },
                    onResult = { result ->
                        when (result) {
                            is SpeedTestService.Result.Loading -> {
                                _uiState.value = _uiState.value.copy(
                                    currentTest = "Measuring latency..."
                                )
                            }

                            is SpeedTestService.Result.Success -> {
                                val data = result.data
                                val state = _uiState.value

                                _uiState.value = state.copy(
                                    isTestRunning = false,
                                    progress = 1f,
                                    currentTest = "Measurement complete",
                                    downloadSpeed = data.downloadSpeed,
                                    uploadSpeed = data.uploadSpeed,
                                    ping = data.ping,
                                    jitter = data.jitter,
                                    packetLoss = data.packetLoss,
                                    networkType = data.networkType,
                                    hasResults = true,
                                    errorMessage = null,
                                    minSpeed = state.instantSamples.minOrNull() ?: 0f,
                                    avgSpeed = state.instantSamples
                                        .average()
                                        .takeIf { state.instantSamples.isNotEmpty() }
                                        ?.toFloat() ?: 0f,
                                    maxSpeed = state.instantSamples.maxOrNull() ?: 0f
                                )

                                Log.d(
                                    "SpeedTestViewModel",
                                    "Speed test completed successfully"
                                )
                            }

                            is SpeedTestService.Result.Error -> {
                                Log.e(
                                    "SpeedTestViewModel",
                                    "Speed test failed: ${result.message}"
                                )

                                _uiState.value = _uiState.value.copy(
                                    isTestRunning = false,
                                    progress = 0f,
                                    currentTest = "Measurement failed",
                                    errorMessage = result.message
                                )
                            }
                        }
                    },
                    onInstantSpeed = { mbps ->
                        val samples = (
                                _uiState.value.instantSamples + mbps
                                ).takeLast(120)

                        _uiState.value = _uiState.value.copy(
                            instantSamples = samples,
                            minSpeed = samples.minOrNull() ?: 0f,
                            avgSpeed = if (samples.isNotEmpty()) {
                                samples.average().toFloat()
                            } else {
                                0f
                            },
                            maxSpeed = samples.maxOrNull() ?: 0f
                        )
                    }
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(
                    "SpeedTestViewModel",
                    "Exception during speed test",
                    exception
                )

                _uiState.value = _uiState.value.copy(
                    isTestRunning = false,
                    progress = 0f,
                    currentTest = "Test failed",
                    errorMessage = exception.message
                        ?: "Unable to complete speed test"
                )
            } finally {
                if (speedTestJob == kotlinx.coroutines.currentCoroutineContext()[Job]) {
                    speedTestJob = null
                }
            }
        }
    }

    fun resetTest() {
        speedTestJob?.cancel()
        speedTestJob = null
        _uiState.value = SpeedTestUiState()
    }

    override fun onCleared() {
        speedTestJob?.cancel()
        speedTestJob = null
        super.onCleared()
    }
}

data class SpeedTestUiState(
    val isTestRunning: Boolean = false,
    val progress: Float = 0f,
    val currentTest: String = "",
    val downloadSpeed: Float = 0f,
    val uploadSpeed: Float = 0f,
    val ping: Int = 0,
    val jitter: Float = 0f,
    val packetLoss: Float = 0f,
    val networkType: String = "",
    val hasResults: Boolean = false,
    val errorMessage: String? = null,
    val instantSamples: List<Float> = emptyList(),
    val minSpeed: Float = 0f,
    val avgSpeed: Float = 0f,
    val maxSpeed: Float = 0f
)
