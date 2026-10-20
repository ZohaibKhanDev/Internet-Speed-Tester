
package com.example.composespeedtest.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

private val Background = Color(0xFF070B16)
private val SurfaceDark = Color(0xFF111A2B)
private val SurfaceLight = Color(0xFF19263D)
private val AccentBlue = Color(0xFF65E1FF)
private val AccentPurple = Color(0xFF9585FF)
private val PrimaryText = Color(0xFFF5F7FF)
private val SecondaryText = Color(0xFF9CAAC3)
private val MutedColor = Color(0xFF35445E)
private val SuccessColor = Color(0xFF71E7B0)

private data class OnboardingPage(
    val title: String,
    val description: String,
    val eyebrow: String
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit = {}
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = remember {
        listOf(
            OnboardingPage(
                eyebrow = "YOUR INTERNET, DECODED",
                title = "Know Your Internet Speed",
                description = "Discover your download and upload speeds with a fast, accurate and effortless test."
            ),
            OnboardingPage(
                eyebrow = "SMART NETWORK INSIGHTS",
                title = "Understand Your Connection",
                description = "Check your network performance and discover how responsive your connection really is."
            ),
            OnboardingPage(
                eyebrow = "READY WHEN YOU ARE",
                title = "Stay Connected With Confidence",
                description = "Test your internet anytime and make every download, stream and game count."
            )
        )
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AccentBlue,
            onPrimary = Background,
            secondary = AccentPurple,
            background = Background,
            surface = SurfaceDark,
            onSurface = PrimaryText
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF14233E),
                                Color(0xFF0C1426),
                                Background
                            )
                        )
                    )
            ) {
                Canvas(
                    modifier = Modifier
                        .size(280.dp)
                        .align(Alignment.TopEnd)
                ) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0x183F8CFF),
                                Color.Transparent
                            )
                        ),
                        radius = size.minDimension * 0.5f,
                        center = Offset(size.width * 0.8f, size.height * 0.15f)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 26.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Header(
                        currentPage = currentPage,
                        totalPages = pages.size,
                        onSkip = onFinished
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (currentPage) {
                            0 -> SpeedIllustration()
                            1 -> ConnectionIllustration()
                            else -> WifiIllustration()
                        }
                    }

                    PageIndicators(
                        currentPage = currentPage,
                        totalPages = pages.size
                    )

                    Spacer(modifier = Modifier.height(27.dp))

                    Text(
                        text = pages[currentPage].eyebrow,
                        color = AccentBlue,
                        fontSize = 10.sp,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(13.dp))

                    Text(
                        text = pages[currentPage].title,
                        color = PrimaryText,
                        fontSize = 29.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.6).sp
                    )

                    Spacer(modifier = Modifier.height(13.dp))

                    Text(
                        text = pages[currentPage].description,
                        color = SecondaryText,
                        fontSize = 14.sp,
                        lineHeight = 23.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 5.dp)
                    )

                    Spacer(modifier = Modifier.height(27.dp))

                    Button(
                        onClick = {
                            if (currentPage < pages.lastIndex) {
                                currentPage++
                            } else {
                                onFinished()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        shape = RoundedCornerShape(19.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = Background
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 3.dp
                        )
                    ) {
                        Text(
                            text = if (currentPage == pages.lastIndex) {
                                "Get Started"
                            } else {
                                "Continue"
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(9.dp))

                    Button(
                        onClick = {
                            if (currentPage > 0) {
                                currentPage--
                            } else {
                                onFinished()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(47.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = SecondaryText
                        )
                    ) {
                        if (currentPage > 0) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Text(
                            text = if (currentPage == 0) "Skip introduction" else "Go back",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun Header(
    currentPage: Int,
    totalPages: Int,
    onSkip: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(AccentBlue, AccentPurple)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = Background,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "SpeedTest",
                    color = PrimaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                )

                Text(
                    text = "INTERNET ANALYZER",
                    color = SecondaryText,
                    fontSize = 9.sp,
                    letterSpacing = 1.3.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Surface(
            onClick = onSkip,
            shape = RoundedCornerShape(50),
            color = Color(0xFF1B2940),
            contentColor = SecondaryText
        ) {
            Text(
                text = "Skip",
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 9.dp
                ),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PageIndicators(
    currentPage: Int,
    totalPages: Int
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            Box(
                modifier = Modifier
                    .width(if (index == currentPage) 30.dp else 7.dp)
                    .height(7.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == currentPage) {
                            Brush.horizontalGradient(
                                listOf(AccentBlue, AccentPurple)
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(MutedColor, MutedColor)
                            )
                        }
                    )
            )
        }
    }
}

@Composable
private fun IllustrationPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x224A91FF),
                            Color(0x104A70FF),
                            Color.Transparent
                        )
                    )
                )
        )

        content()
    }
}

@Composable
private fun SpeedIllustration() {
    IllustrationPanel {
        Box(
            modifier = Modifier.size(270.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width * 0.37f

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x225AD8FF),
                            Color.Transparent
                        ),
                        center = center,
                        radius = radius * 1.8f
                    ),
                    radius = radius * 1.8f,
                    center = center
                )

                drawCircle(
                    color = Color(0x223F8CFF),
                    radius = radius + 26.dp.toPx(),
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                drawCircle(
                    color = Color(0x333F8CFF),
                    radius = radius + 15.dp.toPx(),
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )

                drawArc(
                    color = MutedColor,
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = 14.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            AccentBlue,
                            AccentPurple,
                            AccentBlue
                        ),
                        center = center
                    ),
                    startAngle = 135f,
                    sweepAngle = 240f,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = 14.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                for (index in 0..10) {
                    val angle = Math.toRadians(135.0 + index * 27.0)
                    val startRadius = radius - 25.dp.toPx()
                    val endRadius = radius - 17.dp.toPx()

                    drawLine(
                        color = Color(0xFFD9F7FF),
                        start = Offset(
                            center.x + cos(angle).toFloat() * startRadius,
                            center.y + sin(angle).toFloat() * startRadius
                        ),
                        end = Offset(
                            center.x + cos(angle).toFloat() * endRadius,
                            center.y + sin(angle).toFloat() * endRadius
                        ),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                val needleAngle = Math.toRadians(225.0)
                val needleLength = radius * 0.72f

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentBlue, AccentPurple),
                        start = center,
                        end = Offset(
                            center.x + cos(needleAngle).toFloat() * needleLength,
                            center.y + sin(needleAngle).toFloat() * needleLength
                        )
                    ),
                    start = center,
                    end = Offset(
                        center.x + cos(needleAngle).toFloat() * needleLength,
                        center.y + sin(needleAngle).toFloat() * needleLength
                    ),
                    strokeWidth = 5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                drawCircle(
                    color = PrimaryText,
                    radius = 9.dp.toPx(),
                    center = center
                )

                drawCircle(
                    color = AccentBlue,
                    radius = 4.dp.toPx(),
                    center = center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 14.dp)
            ) {
                Spacer(modifier = Modifier.height(55.dp))

                Text(
                    text = "INTERNET",
                    color = SecondaryText,
                    fontSize = 10.sp,
                    letterSpacing = 2.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "SPEED TEST",
                    color = PrimaryText,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                )
            }

            FloatingInfoCard(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 0.dp, bottom = 24.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = "DOWNLOAD",
                value = "Fast speeds"
            )

            FloatingInfoCard(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 18.dp, end = 0.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.NetworkCheck,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = "PRECISION",
                value = "Clear results"
            )
        }
    }
}

@Composable
private fun ConnectionIllustration() {
    IllustrationPanel {
        Box(
            modifier = Modifier.size(270.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(43.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF263E68),
                                Color(0xFF131E36)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF5AD8FF),
                                Color(0x449585FF)
                            )
                        ),
                        shape = RoundedCornerShape(43.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)

                    listOf(0.43f, 0.29f).forEach { factor ->
                        drawCircle(
                            color = Color(0x335AD8FF),
                            radius = size.width * factor,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    drawLine(
                        color = Color(0x5573DFFF),
                        start = Offset(center.x, 0f),
                        end = Offset(center.x, size.height),
                        strokeWidth = 1.dp.toPx()
                    )

                    drawLine(
                        color = Color(0x5573DFFF),
                        start = Offset(0f, center.y),
                        end = Offset(size.width, center.y),
                        strokeWidth = 1.dp.toPx()
                    )

                    drawCircle(
                        color = AccentBlue,
                        radius = 6.dp.toPx(),
                        center = center
                    )
                }

                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null,
                    tint = PrimaryText,
                    modifier = Modifier.size(50.dp)
                )
            }

            FloatingInfoCard(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 0.dp, top = 28.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = "NETWORK",
                value = "Analysis"
            )

            FloatingInfoCard(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 0.dp, bottom = 28.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = "LATENCY",
                value = "Connection"
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 1.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .width(if (index == 2) 23.dp else 7.dp)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == 2) AccentBlue else MutedColor
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun WifiIllustration() {
    IllustrationPanel {
        Box(
            modifier = Modifier.size(270.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(250.dp)
            ) {
                val centerX = size.width / 2f
                val centerY = size.height * 0.43f

                for (index in 1..3) {
                    val radius = index * 30.dp.toPx()

                    drawArc(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                AccentBlue,
                                AccentPurple
                            )
                        ),
                        startAngle = 225f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(
                            centerX - radius,
                            centerY - radius
                        ),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(
                            width = 9.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    )
                }

                drawCircle(
                    color = AccentBlue.copy(alpha = 0.16f),
                    radius = 24.dp.toPx(),
                    center = Offset(
                        centerX,
                        centerY + 35.dp.toPx()
                    )
                )

                drawCircle(
                    brush = Brush.linearGradient(
                        colors = listOf(AccentBlue, AccentPurple)
                    ),
                    radius = 11.dp.toPx(),
                    center = Offset(
                        centerX,
                        centerY + 35.dp.toPx()
                    )
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessColor,
                        modifier = Modifier.size(16.dp)
                    )

                    Text(
                        text = "CONNECTION READY",
                        color = SuccessColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ready to test",
                    color = PrimaryText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            FloatingInfoCard(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 0.dp, top = 20.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = AccentBlue,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = "WIFI STATUS",
                value = "Connected"
            )

            FloatingInfoCard(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 0.dp, top = 16.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = AccentPurple,
                        modifier = Modifier.size(18.dp)
                    )
                },
                label = "SPEED TEST",
                value = "One tap"
            )
        }
    }
}

@Composable
private fun FloatingInfoCard(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(17.dp),
        color = SurfaceDark.copy(alpha = 0.97f),
        shadowElevation = 9.dp,
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color(0xFF283750)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 11.dp,
                vertical = 11.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(35.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(SurfaceLight),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(9.dp))

            Column {
                Text(
                    text = label,
                    color = SecondaryText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    color = PrimaryText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
