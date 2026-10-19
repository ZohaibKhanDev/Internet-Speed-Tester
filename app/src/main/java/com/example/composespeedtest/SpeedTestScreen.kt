package com.example.composespeedtest

import androidx.compose.ui.geometry.Size
import kotlin.math.cos
import kotlin.math.sin
import kotlin.text.format
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composespeedtest.data.Prefs
import kotlinx.coroutines.launch

private val AppBackground = Color(0xFF080D19)
private val AppSurface = Color(0xFF111A2B)
private val AppSurfaceLight = Color(0xFF19253A)
private val AppAccent = Color(0xFF36E0C1)
private val AppBlue = Color(0xFF59A9FF)
private val AppPurple = Color(0xFFAA8BFF)
private val AppOrange = Color(0xFFFFB45E)
private val AppRed = Color(0xFFFF6B81)
private val AppText = Color(0xFFF3F6FC)
private val AppMuted = Color(0xFF8E9BB1)
private val AppBorder = Color(0xFF26334A)

@Composable
fun SpeedTestScreen() {
    val viewModel: SpeedTestViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val prefs = remember { Prefs(context.applicationContext) }
    val scope = rememberCoroutineScope()

    var darkMode by remember { mutableStateOf(true) }
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(prefs) {
        prefs.darkTheme.collect { darkMode = it }
    }

    val screenBackground = if (darkMode) AppBackground else Color(0xFFF2F5FA)
    val surfaceColor = if (darkMode) AppSurface else Color.White
    val primaryText = if (darkMode) AppText else Color(0xFF182237)
    val secondaryText = if (darkMode) AppMuted else Color(0xFF69768C)

    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            primary = AppAccent,
            secondary = AppBlue,
            background = screenBackground,
            surface = surfaceColor,
            onPrimary = AppBackground,
            onSecondary = Color.White,
            onBackground = primaryText,
            onSurface = primaryText
        ),
        typography = MaterialTheme.typography
    ) {
        Scaffold(
            backgroundColor = screenBackground,
            topBar = {
                TopAppBar(
                    backgroundColor = screenBackground,
                    elevation = 0.dp,
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(AppAccent, AppBlue)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = AppBackground,
                                    modifier = Modifier.size(25.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = when (selectedTab) {
                                        0 -> "Speed Test"
                                        1 -> "Statistics"
                                        else -> "Settings"
                                    },
                                    color = primaryText,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )

                                Text(
                                    text = when (selectedTab) {
                                        0 -> "INTERNET PERFORMANCE"
                                        1 -> "YOUR TEST RESULTS"
                                        else -> "PERSONALIZE YOUR APP"
                                    },
                                    color = secondaryText,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.4.sp
                                )
                            }
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavigation(
                    backgroundColor = surfaceColor,
                    contentColor = primaryText,
                    elevation = 12.dp
                ) {
                    BottomNavigationItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        },
                        label = { Text("Home", fontSize = 11.sp) },
                        selectedContentColor = AppAccent,
                        unselectedContentColor = secondaryText
                    )

                    BottomNavigationItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = {
                            Icon(Icons.Default.ShowChart, contentDescription = "Statistics")
                        },
                        label = { Text("Stats", fontSize = 11.sp) },
                        selectedContentColor = AppAccent,
                        unselectedContentColor = secondaryText
                    )

                    BottomNavigationItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        },
                        label = { Text("Settings", fontSize = 11.sp) },
                        selectedContentColor = AppAccent,
                        unselectedContentColor = secondaryText
                    )
                }
            }
        ) { padding ->
            when (selectedTab) {
                0 -> SpeedTestScreenContent(
                    uiState = uiState,
                    onStartTest = { viewModel.startSpeedTest() },
                    onResetTest = { viewModel.resetTest() },
                    modifier = Modifier.padding(padding),
                    useDarkGradient = darkMode
                )

                1 -> StatusScreenContent(
                    uiState = uiState,
                    modifier = Modifier.padding(padding),
                    darkMode = darkMode
                )

                else -> SettingsScreenContent(
                    darkMode = darkMode,
                    onDarkModeChange = { enabled ->
                        darkMode = enabled
                        scope.launch {
                            prefs.setDarkTheme(enabled)
                        }
                    },
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun SpeedTestScreenContent(
    uiState: SpeedTestUiState,
    onStartTest: () -> Unit,
    onResetTest: () -> Unit,
    modifier: Modifier = Modifier,
    useDarkGradient: Boolean = true
) {
    val background = if (useDarkGradient) {
        Brush.verticalGradient(
            listOf(
                Color(0xFF0D1728),
                AppBackground,
                Color(0xFF0A1220)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color(0xFFF5F8FD),
                Color(0xFFEAF0F8)
            )
        )
    }

    val surface = if (useDarkGradient) AppSurface else Color.White
    val textColor = if (useDarkGradient) AppText else Color(0xFF182237)
    val mutedColor = if (useDarkGradient) AppMuted else Color(0xFF69768C)

    val currentSpeed = when {
        uiState.isTestRunning && uiState.instantSamples.isNotEmpty() ->
            uiState.instantSamples.last()

        uiState.hasResults -> uiState.downloadSpeed
        else -> 0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your connection",
                    color = textColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Measure your internet performance",
                    color = mutedColor,
                    fontSize = 13.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (uiState.isTestRunning) {
                            AppOrange.copy(alpha = 0.14f)
                        } else {
                            AppAccent.copy(alpha = 0.12f)
                        }
                    )
                    .padding(horizontal = 11.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.isTestRunning) AppOrange else AppAccent
                            )
                    )

                    Text(
                        text = if (uiState.isTestRunning) "Testing" else "Ready",
                        color = if (uiState.isTestRunning) AppOrange else AppAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = surface,
            shape = RoundedCornerShape(28.dp),
            elevation = if (useDarkGradient) 0.dp else 5.dp,
            border = BorderStroke(
                1.dp,
                if (useDarkGradient) AppBorder else Color(0xFFE0E7F0)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DOWNLOAD SPEED",
                            color = mutedColor,
                            fontSize = 10.sp,
                            letterSpacing = 1.6.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(5.dp))

                        Text(
                            text = if (uiState.hasResults) {
                                "Test completed"
                            } else if (uiState.isTestRunning) {
                                uiState.currentTest
                            } else {
                                "Ready when you are"
                            },
                            color = textColor,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = null,
                        tint = AppAccent,
                        modifier = Modifier.size(23.dp)
                    )
                }

                Spacer(Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.08f),
                    contentAlignment = Alignment.Center
                ) {
                    SpeedometerGauge(
                        currentSpeedMbps = currentSpeed,
                        maxSpeedMbps = 200f,
                        progress = uiState.progress,
                        isRunning = uiState.isTestRunning
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(top = 15.dp)
                    ) {
                        Text(
                            text = "%.1f".format(currentSpeed),
                            color = textColor,
                            fontSize = 43.sp,
                            lineHeight = 48.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = "Mbps",
                            color = AppAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(7.dp))

                        Text(
                            text = when {
                                uiState.isTestRunning -> "MEASURING"
                                uiState.hasResults -> "YOUR RESULT"
                                else -> "SPEED METER"
                            },
                            color = mutedColor,
                            fontSize = 9.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (uiState.isTestRunning) {
                    LinearProgressIndicator(
                        progress = uiState.progress.coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(CircleShape),
                        color = AppAccent,
                        backgroundColor = if (useDarkGradient) {
                            Color.White.copy(alpha = 0.08f)
                        } else {
                            Color(0xFFE4EAF2)
                        }
                    )

                    Spacer(Modifier.height(12.dp))
                }

                Button(
                    onClick = {
                        if (uiState.hasResults && !uiState.isTestRunning) {
                            onResetTest()
                        } else if (!uiState.isTestRunning) {
                            onStartTest()
                        }
                    },
                    enabled = !uiState.isTestRunning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppAccent,
                        contentColor = Color(0xFF07121B),
                        disabledBackgroundColor = AppAccent.copy(alpha = 0.4f),
                        disabledContentColor = Color(0xFF07121B).copy(alpha = 0.6f)
                    ),
                    elevation = ButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Icon(
                        imageVector = if (uiState.hasResults) {
                            Icons.Default.Refresh
                        } else {
                            Icons.Default.Speed
                        },
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(10.dp))

                    Text(
                        text = when {
                            uiState.isTestRunning -> "Test in progress"
                            uiState.hasResults -> "Run Test Again"
                            else -> "Start Speed Test"
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.hasResults && !uiState.isTestRunning) {
                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = "Run another test to refresh your results",
                        color = mutedColor,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Text(
            text = "PERFORMANCE OVERVIEW",
            color = mutedColor,
            fontSize = 10.sp,
            letterSpacing = 1.8.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Download",
                value = if (uiState.hasResults) {
                    "%.1f".format(uiState.downloadSpeed)
                } else {
                    "--"
                },
                unit = "Mbps",
                icon = "↓",
                accent = AppAccent,
                surface = surface,
                textColor = textColor,
                mutedColor = mutedColor,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Upload",
                value = if (uiState.hasResults) {
                    "%.1f".format(uiState.uploadSpeed)
                } else {
                    "--"
                },
                unit = "Mbps",
                icon = "↑",
                accent = AppBlue,
                surface = surface,
                textColor = textColor,
                mutedColor = mutedColor,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Ping",
                value = if (uiState.hasResults) "${uiState.ping}" else "--",
                unit = "ms",
                icon = "◉",
                accent = AppOrange,
                surface = surface,
                textColor = textColor,
                mutedColor = mutedColor,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "Jitter",
                value = if (uiState.hasResults) {
                    "%.1f".format(uiState.jitter)
                } else {
                    "--"
                },
                unit = "ms",
                icon = "≈",
                accent = AppPurple,
                surface = surface,
                textColor = textColor,
                mutedColor = mutedColor,
                modifier = Modifier.weight(1f)
            )
        }

        if (uiState.instantSamples.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = surface,
                shape = RoundedCornerShape(24.dp),
                elevation = 0.dp,
                border = BorderStroke(
                    1.dp,
                    if (useDarkGradient) AppBorder else Color(0xFFE0E7F0)
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Speed history",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "Real-time download performance",
                                color = mutedColor,
                                fontSize = 11.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = AppAccent,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(Modifier.height(22.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(125.dp)
                    ) {
                        EnhancedSpeedSparkline(
                            samples = uiState.instantSamples,
                            lineColor = AppAccent
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = surface,
            shape = RoundedCornerShape(22.dp),
            elevation = 0.dp,
            border = BorderStroke(
                1.dp,
                if (useDarkGradient) AppBorder else Color(0xFFE0E7F0)
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Additional details",
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                DetailRow(
                    label = "Minimum speed",
                    value = if (uiState.hasResults) {
                        "%.1f Mbps".format(uiState.minSpeed)
                    } else {
                        "--"
                    },
                    textColor = textColor,
                    mutedColor = mutedColor
                )

                Divider(color = AppBorder.copy(alpha = 0.7f))

                DetailRow(
                    label = "Average speed",
                    value = if (uiState.hasResults) {
                        "%.1f Mbps".format(uiState.avgSpeed)
                    } else {
                        "--"
                    },
                    textColor = textColor,
                    mutedColor = mutedColor
                )

                Divider(color = AppBorder.copy(alpha = 0.7f))

                DetailRow(
                    label = "Maximum speed",
                    value = if (uiState.hasResults) {
                        "%.1f Mbps".format(uiState.maxSpeed)
                    } else {
                        "--"
                    },
                    textColor = textColor,
                    mutedColor = mutedColor
                )

                Divider(color = AppBorder.copy(alpha = 0.7f))

                DetailRow(
                    label = "Packet loss",
                    value = if (uiState.hasResults) {
                        "%.1f%%".format(uiState.packetLoss)
                    } else {
                        "--"
                    },
                    textColor = textColor,
                    mutedColor = mutedColor
                )

                if (uiState.hasResults && uiState.networkType.isNotEmpty()) {
                    Divider(color = AppBorder.copy(alpha = 0.7f))

                    DetailRow(
                        label = "Connection type",
                        value = uiState.networkType,
                        textColor = textColor,
                        mutedColor = mutedColor
                    )
                }
            }
        }

        if (uiState.isTestRunning) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = surface,
                shape = RoundedCornerShape(20.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, AppAccent.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = AppAccent,
                            strokeWidth = 2.dp
                        )

                        Column {
                            Text(
                                text = "Testing your connection",
                                color = textColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = uiState.currentTest,
                                color = mutedColor,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(Modifier.weight(1f))

                        Text(
                            text = "${(uiState.progress.coerceIn(0f, 1f) * 100).toInt()}%",
                            color = AppAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = AppRed.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AppRed.copy(alpha = 0.3f)),
                elevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = AppRed
                    )

                    Text(
                        text = uiState.errorMessage.orEmpty(),
                        color = if (useDarkGradient) AppText else Color(0xFF8A2434),
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(10.dp))
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    unit: String,
    icon: String,
    accent: Color,
    surface: Color,
    textColor: Color,
    mutedColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = surface,
        shape = RoundedCornerShape(22.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = mutedColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(accent.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        color = accent,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    text = value,
                    color = textColor,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )

                Text(
                    text = unit,
                    color = accent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    textColor: Color,
    mutedColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = mutedColor,
            fontSize = 13.sp
        )

        Text(
            text = value,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun StatusScreenContent(
    uiState: SpeedTestUiState,
    modifier: Modifier = Modifier,
    darkMode: Boolean = true
) {
    val surface = if (darkMode) AppSurface else Color.White
    val textColor = if (darkMode) AppText else Color(0xFF182237)
    val mutedColor = if (darkMode) AppMuted else Color(0xFF69768C)
    val background = if (darkMode) AppBackground else Color(0xFFF2F5FA)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column {
            Text(
                text = "Network statistics",
                color = textColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Review your latest connection measurements",
                color = mutedColor,
                fontSize = 13.sp
            )
        }

        if (!uiState.hasResults) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = surface,
                shape = RoundedCornerShape(24.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, AppBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(AppAccent.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShowChart,
                            contentDescription = null,
                            tint = AppAccent,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    Text(
                        text = "No test results yet",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Run your first speed test to see your download speed, upload speed, latency and connection quality.",
                        color = mutedColor,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Download",
                    value = "%.1f".format(uiState.downloadSpeed),
                    unit = "Mbps",
                    icon = "↓",
                    accent = AppAccent,
                    surface = surface,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Upload",
                    value = "%.1f".format(uiState.uploadSpeed),
                    unit = "Mbps",
                    icon = "↑",
                    accent = AppBlue,
                    surface = surface,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Latency",
                    value = "${uiState.ping}",
                    unit = "ms",
                    icon = "◉",
                    accent = AppOrange,
                    surface = surface,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Jitter",
                    value = "%.1f".format(uiState.jitter),
                    unit = "ms",
                    icon = "≈",
                    accent = AppPurple,
                    surface = surface,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    modifier = Modifier.weight(1f)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = surface,
                shape = RoundedCornerShape(22.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, AppBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Connection quality",
                        color = textColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    DetailRow(
                        label = "Minimum speed",
                        value = "%.1f Mbps".format(uiState.minSpeed),
                        textColor = textColor,
                        mutedColor = mutedColor
                    )

                    DetailRow(
                        label = "Average speed",
                        value = "%.1f Mbps".format(uiState.avgSpeed),
                        textColor = textColor,
                        mutedColor = mutedColor
                    )

                    DetailRow(
                        label = "Maximum speed",
                        value = "%.1f Mbps".format(uiState.maxSpeed),
                        textColor = textColor,
                        mutedColor = mutedColor
                    )

                    DetailRow(
                        label = "Packet loss",
                        value = "%.1f%%".format(uiState.packetLoss),
                        textColor = textColor,
                        mutedColor = mutedColor
                    )

                    if (uiState.networkType.isNotEmpty()) {
                        DetailRow(
                            label = "Network type",
                            value = uiState.networkType,
                            textColor = textColor,
                            mutedColor = mutedColor
                        )
                    }
                }
            }

            if (uiState.instantSamples.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    backgroundColor = surface,
                    shape = RoundedCornerShape(22.dp),
                    elevation = 0.dp,
                    border = BorderStroke(1.dp, AppBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Text(
                            text = "Speed history",
                            color = textColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(20.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            EnhancedSpeedSparkline(
                                samples = uiState.instantSamples,
                                lineColor = AppAccent
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsScreenContent(
    darkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs = remember { Prefs(context.applicationContext) }
    val scope = rememberCoroutineScope()

    val background = if (darkMode) AppBackground else Color(0xFFF2F5FA)
    val surface = if (darkMode) AppSurface else Color.White
    val textColor = if (darkMode) AppText else Color(0xFF182237)
    val mutedColor = if (darkMode) AppMuted else Color(0xFF69768C)

    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showPrivacyDetails by remember { mutableStateOf(false) }
    var showAdsSettings by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column {
            Text(
                text = "Settings",
                color = textColor,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Make the app work the way you like",
                color = mutedColor,
                fontSize = 13.sp
            )
        }

        SettingsSection(title = "APPEARANCE", textColor = mutedColor) {
            Card(
                backgroundColor = surface,
                shape = RoundedCornerShape(22.dp),
                elevation = 0.dp,
                border = BorderStroke(1.dp, AppBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(AppPurple.copy(alpha = 0.13f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = AppPurple
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "App theme",
                                color = textColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(3.dp))

                            Text(
                                text = "Choose light or dark appearance",
                                color = mutedColor,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ThemeChoice(
                            title = "Light",
                            selected = !darkMode,
                            modifier = Modifier.weight(1f),
                            onClick = { onDarkModeChange(false) }
                        )

                        ThemeChoice(
                            title = "Dark",
                            selected = darkMode,
                            modifier = Modifier.weight(1f),
                            onClick = { onDarkModeChange(true) }
                        )
                    }
                }
            }
        }

        SettingsSection(title = "PRIVACY & DATA", textColor = mutedColor) {
            SettingsGroup(surface = surface) {
                SettingsItem(
                    title = "Privacy policy",
                    subtitle = "Learn how your data is handled",
                    accent = AppBlue,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    onClick = { showPrivacyPolicy = true }
                )

                SettingsDivider()

                SettingsItem(
                    title = "Data usage",
                    subtitle = "Learn how speed tests use data",
                    accent = AppAccent,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    onClick = { showPrivacyDetails = true }
                )
            }
        }

        SettingsSection(title = "PERSONALIZATION", textColor = mutedColor) {
            SettingsGroup(surface = surface) {
                SettingsItem(
                    title = "Advertising preferences",
                    subtitle = "Manage your ad preferences",
                    accent = AppOrange,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    onClick = { showAdsSettings = true }
                )
            }
        }

        SettingsSection(title = "ABOUT", textColor = mutedColor) {
            SettingsGroup(surface = surface) {
                SettingsItem(
                    title = "About Speed Test",
                    subtitle = "Version 1.0.0",
                    accent = AppPurple,
                    textColor = textColor,
                    mutedColor = mutedColor,
                    onClick = { showAboutDialog = true }
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = surface,
            shape = RoundedCornerShape(20.dp),
            elevation = 0.dp,
            border = BorderStroke(1.dp, AppBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(AppAccent, AppBlue)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = AppBackground,
                        modifier = Modifier.size(27.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Speed Test",
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Simple. Fast. Reliable.",
                    color = mutedColor,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(Modifier.height(8.dp))
    }

    if (showPrivacyPolicy) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyPolicy = false })
    }

    if (showPrivacyDetails) {
        PrivacyDetailsDialog(onDismiss = { showPrivacyDetails = false })
    }

    if (showAdsSettings) {
        AdsSettingsDialog(
            prefs = prefs,
            onDismiss = { showAdsSettings = false }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

@Composable
private fun SettingsSection(
    title: String,
    textColor: Color,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 10.sp,
            letterSpacing = 1.6.sp,
            fontWeight = FontWeight.Bold
        )

        content()
    }
}

@Composable
private fun SettingsGroup(
    surface: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = surface,
        shape = RoundedCornerShape(22.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, AppBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            content = content
        )
    }
}

@Composable
private fun SettingsDivider() {
    Divider(
        color = AppBorder.copy(alpha = 0.7f),
        thickness = 1.dp
    )
}

@Composable
private fun SettingsItem(
    title: String,
    subtitle: String,
    accent: Color,
    textColor: Color,
    mutedColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = mutedColor,
                fontSize = 11.sp
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForwardIos,
            contentDescription = null,
            tint = mutedColor,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun ThemeChoice(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        color = if (selected) AppAccent.copy(alpha = 0.13f) else Color.Transparent,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (selected) AppAccent else AppBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = AppAccent,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(Modifier.width(7.dp))
            }

            Text(
                text = title,
                color = if (selected) AppAccent else AppMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun GaugeStat(label: String, value: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = AppMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "%.1f".format(value),
            color = AppText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EnhancedGaugeStat(
    label: String,
    value: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = label,
            color = color.copy(alpha = 0.85f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Text(
            text = "%.1f".format(value),
            color = color,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SpeedSparkline(samples: List<Float>) {
    EnhancedSpeedSparkline(samples = samples, lineColor = AppAccent)
}

@Composable
fun EnhancedSpeedSparkline(
    samples: List<Float>,
    lineColor: Color = AppAccent
) {
    val maxValue = (samples.maxOrNull() ?: 1f).coerceAtLeast(1f)

    Canvas(modifier = Modifier.fillMaxSize()) {
        if (samples.size < 2) return@Canvas

        val widthStep = size.width / (samples.size - 1)
        val points = samples.mapIndexed { index, sample ->
            Offset(
                x = index * widthStep,
                y = size.height -
                        (sample.coerceAtLeast(0f) / maxValue) * size.height * 0.88f -
                        size.height * 0.06f
            )
        }

        val areaPath = Path().apply {
            moveTo(points.first().x, size.height)
            lineTo(points.first().x, points.first().y)

            for (index in 1 until points.size) {
                lineTo(points[index].x, points[index].y)
            }

            lineTo(points.last().x, size.height)
            close()
        }

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    lineColor.copy(alpha = 0.28f),
                    lineColor.copy(alpha = 0.02f)
                )
            )
        )

        val linePath = Path().apply {
            moveTo(points.first().x, points.first().y)

            for (index in 1 until points.size) {
                lineTo(points[index].x, points[index].y)
            }
        }

        drawPath(
            path = linePath,
            color = lineColor,
            style = Stroke(
                width = 3.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            color = lineColor.copy(alpha = 0.2f),
            radius = 7.dp.toPx(),
            center = points.last()
        )

        drawCircle(
            color = lineColor,
            radius = 3.5.dp.toPx(),
            center = points.last()
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = AppAccent,
            contentColor = AppBackground
        ),
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        ),
        modifier = Modifier
            .height(52.dp)
            .shadow(3.dp, RoundedCornerShape(18.dp))
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 18.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = AppSurface,
        shape = RoundedCornerShape(18.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, AppBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                color = AppMuted,
                fontSize = 12.sp
            )

            Text(
                text = value,
                color = AppText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EnhancedStatCard(
    title: String,
    value: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        backgroundColor = AppSurface,
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    fontSize = 20.sp
                )
            }

            Text(
                text = title,
                color = AppMuted,
                fontSize = 12.sp
            )

            Text(
                text = value,
                color = AppText,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TestProgressCard(state: SpeedTestUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = AppSurface,
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, AppAccent.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = AppAccent,
                    strokeWidth = 2.dp
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Test in progress",
                        color = AppText,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = state.currentTest,
                        color = AppMuted,
                        fontSize = 12.sp
                    )
                }

                Text(
                    text = "${(state.progress.coerceIn(0f, 1f) * 100).toInt()}%",
                    color = AppAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            LinearProgressIndicator(
                progress = state.progress.coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = AppAccent,
                backgroundColor = Color.White.copy(alpha = 0.08f)
            )
        }
    }
}

@Composable
fun ErrorMessage(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        backgroundColor = AppRed.copy(alpha = 0.1f),
        shape = RoundedCornerShape(18.dp),
        elevation = 0.dp,
        border = BorderStroke(1.dp, AppRed.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = AppRed
            )

            Text(
                text = message,
                color = AppText,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun SpeedometerGauge(
    currentSpeedMbps: Float,
    maxSpeedMbps: Float,
    progress: Float,
    isRunning: Boolean
) {
    val targetRatio = (currentSpeedMbps / maxSpeedMbps.coerceAtLeast(1f))
        .coerceIn(0f, 1f)

    val animatedRatio by animateFloatAsState(
        targetValue = targetRatio,
        animationSpec = tween(
            durationMillis = if (isRunning) 350 else 650
        )
    )

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val strokeWidth = 15.dp.toPx()
        val inset = strokeWidth / 2f + 8.dp.toPx()
        val arcSize = Size(
            width = size.width - inset * 2,
            height = size.height - inset * 2
        )
        val topLeft = Offset(inset, inset)
        val startAngle = 135f
        val sweepAngle = 270f

        drawArc(
            color = Color(0xFF26344B),
            startAngle = startAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawArc(
            brush = Brush.sweepGradient(
                colors = listOf(
                    AppAccent,
                    AppBlue,
                    AppPurple,
                    AppAccent
                )
            ),
            startAngle = startAngle,
            sweepAngle = sweepAngle * animatedRatio,
            useCenter = false,
            topLeft = topLeft,
            size = arcSize,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        val center = Offset(
            x = topLeft.x + arcSize.width / 2f,
            y = topLeft.y + arcSize.height / 2f
        )
        val radius = arcSize.width / 2f

        repeat(41) { index ->
            val angleDegrees = startAngle + sweepAngle * index / 40f
            val angle = Math.toRadians(angleDegrees.toDouble())
            val major = index % 5 == 0
            val innerRadius = radius - if (major) 29.dp.toPx() else 23.dp.toPx()
            val outerRadius = radius - 17.dp.toPx()

            val start = Offset(
                x = center.x + cos(angle).toFloat() * innerRadius,
                y = center.y + sin(angle).toFloat() * innerRadius
            )

            val end = Offset(
                x = center.x + cos(angle).toFloat() * outerRadius,
                y = center.y + sin(angle).toFloat() * outerRadius
            )

            drawLine(
                color = if (major) {
                    AppText.copy(alpha = 0.75f)
                } else {
                    AppMuted.copy(alpha = 0.45f)
                },
                start = start,
                end = end,
                strokeWidth = if (major) 2.dp.toPx() else 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        val needleAngle = Math.toRadians(
            (startAngle + sweepAngle * animatedRatio).toDouble()
        )
        val needleLength = radius - 38.dp.toPx()

        val tip = Offset(
            x = center.x + cos(needleAngle).toFloat() * needleLength,
            y = center.y + sin(needleAngle).toFloat() * needleLength
        )

        val leftAngle = needleAngle + Math.PI / 2
        val rightAngle = needleAngle - Math.PI / 2
        val baseWidth = 5.dp.toPx()

        val left = Offset(
            x = center.x + cos(leftAngle).toFloat() * baseWidth,
            y = center.y + sin(leftAngle).toFloat() * baseWidth
        )

        val right = Offset(
            x = center.x + cos(rightAngle).toFloat() * baseWidth,
            y = center.y + sin(rightAngle).toFloat() * baseWidth
        )

        val needle = Path().apply {
            moveTo(tip.x, tip.y)
            lineTo(left.x, left.y)
            lineTo(right.x, right.y)
            close()
        }

        drawPath(
            path = needle,
            color = AppAccent.copy(alpha = 0.2f)
        )

        drawPath(
            path = needle,
            color = AppText
        )

        drawCircle(
            color = AppAccent.copy(alpha = 0.15f),
            radius = 14.dp.toPx(),
            center = center
        )

        drawCircle(
            color = AppText,
            radius = 8.dp.toPx(),
            center = center
        )

        drawCircle(
            color = AppAccent,
            radius = 4.dp.toPx(),
            center = center
        )

        if (isRunning) {
            val phase = progress.coerceIn(0f, 1f)

            repeat(8) { index ->
                val angle = Math.toRadians(
                    (startAngle + index * 33f + phase * 180f).toDouble()
                )

                val dot = Offset(
                    x = center.x + cos(angle).toFloat() * (radius - 3.dp.toPx()),
                    y = center.y + sin(angle).toFloat() * (radius - 3.dp.toPx())
                )

                drawCircle(
                    color = AppAccent.copy(alpha = 0.65f),
                    radius = 2.dp.toPx(),
                    center = dot
                )
            }
        }
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Privacy Policy",
                color = AppText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Review the privacy practices that apply to your app.",
                    color = AppMuted,
                    fontSize = 12.sp
                )

                DialogHeading("Information and test results")

                DialogBody(
                    "Your speed test may measure download speed, upload speed, latency, jitter, packet loss and network type. The information actually collected depends on your app's implementation and the services it uses."
                )

                DialogHeading("How information is used")

                DialogBody(
                    "Speed test measurements can be used to display results and troubleshoot connection performance. Network requests may send data to the selected test servers."
                )

                DialogHeading("Data sharing and storage")

                DialogBody(
                    "Check the app's actual network code, analytics setup and storage implementation to determine whether any measurements are uploaded, shared or stored remotely."
                )

                DialogHeading("Privacy questions")

                DialogBody(
                    "Provide a contact email or an official privacy policy URL in your published app so users can ask questions about data handling."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppAccent)
            }
        },
        backgroundColor = AppSurface,
        contentColor = AppText,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun PrivacyDetailsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Data Usage Details",
                color = AppText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DialogHeading("Download test")

                DialogBody(
                    "A download test receives data from a test server to estimate how quickly your connection can download information."
                )

                DialogHeading("Upload test")

                DialogBody(
                    "An upload test sends data to a test endpoint to estimate your outgoing connection speed."
                )

                DialogHeading("Latency and jitter")

                DialogBody(
                    "Latency estimates how long a network request takes. Jitter describes variation in latency measurements."
                )

                DialogHeading("Data consumption")

                DialogBody(
                    "Speed tests can consume mobile or Wi-Fi data. The amount depends on the test duration, transfer size and implementation."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppAccent)
            }
        },
        backgroundColor = AppSurface,
        contentColor = AppText,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun AdsSettingsDialog(
    prefs: Prefs,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var personalizedAds by remember { mutableStateOf(true) }
    var adFrequency by remember { mutableStateOf("Normal") }

    LaunchedEffect(prefs) {
        prefs.personalizedAds.collect {
            personalizedAds = it
        }
    }

    LaunchedEffect(prefs) {
        prefs.adFrequency.collect {
            adFrequency = it
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Advertising Preferences",
                color = AppText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 430.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Personalized ads",
                            color = AppText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = "Allow ads to be personalized where supported",
                            color = AppMuted,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = personalizedAds,
                        onCheckedChange = {
                            personalizedAds = it
                            scope.launch {
                                prefs.setPersonalizedAds(it)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AppAccent,
                            checkedTrackColor = AppAccent.copy(alpha = 0.4f)
                        )
                    )
                }

                Divider(color = AppBorder)

                Text(
                    text = "Preferred ad frequency",
                    color = AppText,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Low", "Normal", "High").forEach { frequency ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    adFrequency = frequency
                                    scope.launch {
                                        prefs.setAdFrequency(frequency)
                                    }
                                },
                            color = if (adFrequency == frequency) {
                                AppAccent.copy(alpha = 0.12f)
                            } else {
                                Color.Transparent
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.dp,
                                if (adFrequency == frequency) AppAccent else AppBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = frequency,
                                    color = if (adFrequency == frequency) {
                                        AppAccent
                                    } else {
                                        AppText
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )

                                if (adFrequency == frequency) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = AppAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = "These settings save your preferences. Actual ad behavior depends on the advertising SDK and its configuration.",
                    color = AppMuted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = AppAccent)
            }
        },
        backgroundColor = AppSurface,
        contentColor = AppText,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "About Speed Test",
                color = AppText,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(AppAccent, AppBlue)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = AppBackground,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Text(
                    text = "Speed Test",
                    color = AppText,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Version 1.0.0",
                    color = AppAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "A simple interface for measuring internet performance and viewing connection statistics.",
                    color = AppMuted,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Divider(color = AppBorder)

                DialogBody(
                    "Features include download and upload measurements, latency statistics, jitter, packet loss information and speed history when supported by the test implementation."
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppAccent)
            }
        },
        backgroundColor = AppSurface,
        contentColor = AppText,
        shape = RoundedCornerShape(24.dp)
    )
}

@Composable
private fun DialogHeading(text: String) {
    Text(
        text = text,
        color = AppText,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
}

@Composable
private fun DialogBody(text: String) {
    Text(
        text = text,
        color = AppMuted,
        fontSize = 12.sp,
        lineHeight = 19.sp
    )
}