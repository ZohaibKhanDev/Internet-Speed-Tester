
package com.example.composespeedtest

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.SystemClock
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.IOException
import java.util.Collections
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs
import kotlin.math.roundToInt

class SpeedTestService(context: Context) {

    private val appContext = context.applicationContext

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .callTimeout(15, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .build()

    private val downloadUrl = "https://speed.cloudflare.com/__down"
    private val uploadUrl = "https://speed.cloudflare.com/__up"
    private val latencyUrl = "https://www.cloudflare.com/cdn-cgi/trace"

    data class SpeedTestResult(
        val downloadSpeed: Float,
        val uploadSpeed: Float,
        val ping: Int,
        val jitter: Float,
        val packetLoss: Float,
        val networkType: String
    )

    sealed class Result {
        data object Loading : Result()
        data class Success(val data: SpeedTestResult) : Result()
        data class Error(val message: String) : Result()
    }

    private data class LatencyResult(
        val samples: List<Long>,
        val packetLoss: Float
    )

    fun isNetworkAvailable(): Boolean {
        val manager = appContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = manager.activeNetwork ?: return false
        val capabilities = manager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        ) && capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_VALIDATED
        )
    }

    fun getNetworkType(): String {
        val manager = appContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val network = manager.activeNetwork ?: return "Unknown"
        val capabilities = manager.getNetworkCapabilities(network)
            ?: return "Unknown"

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ->
                "WiFi"

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ->
                "Mobile"

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ->
                "Ethernet"

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ->
                "VPN"

            else -> "Unknown"
        }
    }

    suspend fun performSpeedTest(
        onProgress: (Float) -> Unit,
        onResult: (Result) -> Unit,
        onInstantSpeed: (Float) -> Unit
    ) {
        try {
            currentCoroutineContext().ensureActive()

            if (!isNetworkAvailable()) {
                onResult(Result.Error("No validated internet connection"))
                return
            }

            onResult(Result.Loading)

            val networkType = getNetworkType()

            onProgress(0.02f)

            val latency = measureLatency()
            val ping = latency.samples.average().roundToInt()
            val jitter = calculateJitter(latency.samples)

            onProgress(0.15f)

            val downloadSpeed = measureDownloadSpeed(
                durationMs = 5_000L,
                parallelStreams = 3,
                onProgress = { progress ->
                    onProgress(0.15f + progress * 0.45f)
                },
                onInstant = onInstantSpeed
            )

            currentCoroutineContext().ensureActive()

            if (downloadSpeed <= 0f) {
                onResult(Result.Error("Download test failed"))
                return
            }

            onProgress(0.60f)

            val uploadSpeed = measureUploadSpeed(
                durationMs = 4_000L,
                onProgress = { progress ->
                    onProgress(0.60f + progress * 0.35f)
                },
                onInstant = onInstantSpeed
            )

            currentCoroutineContext().ensureActive()

            if (uploadSpeed <= 0f) {
                onResult(Result.Error("Upload test failed"))
                return
            }

            onProgress(0.95f)

            onResult(
                Result.Success(
                    SpeedTestResult(
                        downloadSpeed = downloadSpeed,
                        uploadSpeed = uploadSpeed,
                        ping = ping,
                        jitter = jitter,
                        packetLoss = latency.packetLoss,
                        networkType = networkType
                    )
                )
            )

            onProgress(1f)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            onResult(
                Result.Error(
                    exception.message ?: "Unable to complete speed test"
                )
            )
        }
    }

    private suspend fun measureLatency(): LatencyResult =
        withContext(Dispatchers.IO) {
            val samples = mutableListOf<Long>()
            var failedRequests = 0
            val totalRequests = 5

            repeat(totalRequests) {
                currentCoroutineContext().ensureActive()

                val request = Request.Builder()
                    .url(latencyUrl)
                    .header("Cache-Control", "no-cache")
                    .get()
                    .build()

                val start = SystemClock.elapsedRealtime()

                try {
                    client.newCall(request).execute().use { response ->
                        if (response.isSuccessful) {
                            response.body?.byteStream()?.use { input ->
                                val buffer = ByteArray(256)
                                input.read(buffer)
                            }

                            samples.add(
                                SystemClock.elapsedRealtime() - start
                            )
                        } else {
                            failedRequests++
                        }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    failedRequests++
                }

                delay(100)
            }

            if (samples.isEmpty()) {
                throw IOException("Latency measurement failed")
            }

            LatencyResult(
                samples = samples,
                packetLoss = failedRequests * 100f / totalRequests
            )
        }

    private fun calculateJitter(samples: List<Long>): Float {
        if (samples.size < 2) return 0f

        val differences = samples.zipWithNext { first, second ->
            abs(second - first).toFloat()
        }

        return differences.average().toFloat()
    }

    private suspend fun measureDownloadSpeed(
        durationMs: Long,
        parallelStreams: Int,
        onProgress: (Float) -> Unit,
        onInstant: (Float) -> Unit
    ): Float = coroutineScope {
        val startTime = SystemClock.elapsedRealtime()
        val endTime = startTime + durationMs
        val totalBytes = AtomicLong(0L)
        val calls = Collections.synchronizedList(
            mutableListOf<okhttp3.Call>()
        )

        val reporter = launch(Dispatchers.Default) {
            var previousBytes = 0L
            var previousTime = SystemClock.elapsedRealtime()

            while (isActive && SystemClock.elapsedRealtime() < endTime) {
                delay(200)

                val now = SystemClock.elapsedRealtime()
                val bytes = totalBytes.get()
                val elapsed = now - previousTime
                val delta = bytes - previousBytes

                if (elapsed > 0 && delta > 0) {
                    onInstant(
                        delta * 8f / elapsed / 1_000f
                    )
                }

                previousBytes = bytes
                previousTime = now

                onProgress(
                    ((now - startTime).toFloat() / durationMs)
                        .coerceIn(0f, 1f)
                )
            }
        }

        val jobs = (0 until parallelStreams).map {
            launch(Dispatchers.IO) {
                val request = Request.Builder()
                    .url("$downloadUrl?bytes=100000000")
                    .header("Cache-Control", "no-cache")
                    .get()
                    .build()

                val call = client.newCall(request)
                calls.add(call)

                try {
                    call.execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException(
                                "Download server returned ${response.code}"
                            )
                        }

                        val input = response.body?.byteStream()
                            ?: throw IOException("Empty download response")

                        val buffer = ByteArray(64 * 1024)

                        while (
                            SystemClock.elapsedRealtime() < endTime &&
                            isActive
                        ) {
                            currentCoroutineContext().ensureActive()

                            val read = input.read(buffer)

                            if (read == -1) break
                            if (read == 0) continue

                            totalBytes.addAndGet(read.toLong())
                        }
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: Exception) {
                    if (isActive && SystemClock.elapsedRealtime() < endTime) {
                        throw exception
                    }
                } finally {
                    call.cancel()
                    calls.remove(call)
                }
            }
        }

        try {
            delay(durationMs)
        } finally {
            calls.toList().forEach { it.cancel() }
            jobs.forEach { it.cancel() }
            reporter.cancel()
            jobs.forEach { it.join() }
            reporter.join()
        }

        val elapsed = SystemClock.elapsedRealtime() - startTime
        val bytes = totalBytes.get()

        if (bytes <= 0L || elapsed <= 0L) {
            throw IOException("No download data received")
        }

        onProgress(1f)

        bytes * 8f / elapsed / 1_000f
    }

    private suspend fun measureUploadSpeed(
        durationMs: Long,
        onProgress: (Float) -> Unit,
        onInstant: (Float) -> Unit
    ): Float = coroutineScope {
        val startTime = SystemClock.elapsedRealtime()
        val endTime = startTime + durationMs
        val sentBytes = AtomicLong(0L)
        val calls = Collections.synchronizedList(
            mutableListOf<okhttp3.Call>()
        )

        val requestBody = object : RequestBody() {

            override fun contentType() =
                "application/octet-stream".toMediaType()

            override fun contentLength(): Long = -1L

            override fun writeTo(sink: BufferedSink) {
                val data = ByteArray(64 * 1024)

                while (SystemClock.elapsedRealtime() < endTime) {
                    sink.write(data)
                    sentBytes.addAndGet(data.size.toLong())
                }
            }
        }

        val reporter = launch(Dispatchers.Default) {
            var previousBytes = 0L
            var previousTime = SystemClock.elapsedRealtime()

            while (isActive && SystemClock.elapsedRealtime() < endTime) {
                delay(200)

                val now = SystemClock.elapsedRealtime()
                val bytes = sentBytes.get()
                val elapsed = now - previousTime
                val delta = bytes - previousBytes

                if (elapsed > 0 && delta > 0) {
                    onInstant(
                        delta * 8f / elapsed / 1_000f
                    )
                }

                previousBytes = bytes
                previousTime = now

                onProgress(
                    ((now - startTime).toFloat() / durationMs)
                        .coerceIn(0f, 1f)
                )
            }
        }

        val request = Request.Builder()
            .url(uploadUrl)
            .header("Cache-Control", "no-cache")
            .post(requestBody)
            .build()

        val call = client.newCall(request)
        calls.add(call)

        val requestJob = launch(Dispatchers.IO) {
            try {
                call.execute().use { response ->
                    if (!response.isSuccessful) {
                        throw IOException(
                            "Upload server returned ${response.code}"
                        )
                    }

                    response.body?.close()
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (isActive && SystemClock.elapsedRealtime() < endTime) {
                    throw exception
                }
            }
        }

        try {
            delay(durationMs)
        } finally {
            calls.toList().forEach { it.cancel() }
            requestJob.cancel()
            reporter.cancel()
            requestJob.join()
            reporter.join()
        }

        val elapsed = SystemClock.elapsedRealtime() - startTime
        val bytes = sentBytes.get()

        if (bytes <= 0L || elapsed <= 0L) {
            throw IOException("No upload data sent")
        }

        onProgress(1f)

        bytes * 8f / elapsed / 1_000f
    }
}
