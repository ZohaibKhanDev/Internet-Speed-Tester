package com.example.composespeedtest.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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

private val BackgroundTop = Color(0xFF101D35)
private val BackgroundBottom = Color(0xFF07101F)
private val AccentBlue = Color(0xFF65D6FF)
private val AccentPurple = Color(0xFF8585FF)
private val PrimaryText = Color(0xFFF4F7FF)
private val SecondaryText = Color(0xFFA8B5CC)
private val CardColor = Color(0xFF172641)

private data class OnboardingPage(
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(
    onFinished: () -> Unit = {}
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = remember {
        listOf(
            OnboardingPage(
                title = "Know Your Internet Speed",
                description = "Measure your download and upload speeds with a clean, simple experience."
            ),
            OnboardingPage(
                title = "Check Your Connection",
                description = "Understand your network performance and see how responsive your connection is."
            ),
            OnboardingPage(
                title = "Stay Connected With Confidence",
                description = "Run a quick test whenever you want to know how your internet is performing."
            )
        )
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AccentBlue,
            onPrimary = BackgroundBottom,
            background = BackgroundBottom,
            surface = CardColor,
            onSurface = PrimaryText
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BackgroundBottom
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                BackgroundTop,
                                Color(0xFF0C172B),
                                BackgroundBottom
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 28.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(AccentBlue, AccentPurple)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = BackgroundBottom,
                                    modifier = Modifier.size(25.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(11.dp))

                            Column {
                                Text(
                                    text = "SpeedTest",
                                    color = PrimaryText,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "INTERNET ANALYZER",
                                    color = SecondaryText,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }

                        Text(
                            text = "${currentPage + 1} / ${pages.size}",
                            color = SecondaryText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

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

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(pages.size) { index ->
                            Box(
                                modifier = Modifier
                                    .width(if (index == currentPage) 28.dp else 7.dp)
                                    .height(7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (index == currentPage) {
                                            AccentBlue
                                        } else {
                                            Color(0xFF35435C)
                                        }
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = pages[currentPage].title,
                        color = PrimaryText,
                        fontSize = 28.sp,
                        lineHeight = 35.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = pages[currentPage].description,
                        color = SecondaryText,
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(32.dp))

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
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentBlue,
                            contentColor = BackgroundBottom
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

                    Spacer(modifier = Modifier.height(12.dp))

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
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = SecondaryText
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = if (currentPage == 0) "Skip" else "Back",
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
private fun SpeedIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(270.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x333F9DFF),
                            Color(0x114F72FF),
                            Color.Transparent
                        )
                    )
                )
        )

        Canvas(
            modifier = Modifier.size(260.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.width * 0.39f

            drawCircle(
                color = Color(0x223F9DFF),
                radius = radius + 27.dp.toPx(),
                center = center
            )

            drawCircle(
                color = Color(0x334F83FF),
                radius = radius + 12.dp.toPx(),
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            drawArc(
                brush = Brush.sweepGradient(
                    listOf(AccentBlue, AccentPurple, AccentBlue)
                ),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(
                    width = 13.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                color = Color(0xFF30405D),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = Offset(
                    center.x - radius + 22.dp.toPx(),
                    center.y - radius + 22.dp.toPx()
                ),
                size = Size(
                    (radius - 22.dp.toPx()) * 2,
                    (radius - 22.dp.toPx()) * 2
                ),
                style = Stroke(width = 2.dp.toPx())
            )

            for (index in 0..10) {
                val angle = Math.toRadians((135 + index * 27.0))
                val startRadius = radius - 25.dp.toPx()
                val endRadius = radius - 16.dp.toPx()

                val start = Offset(
                    center.x + kotlin.math.cos(angle).toFloat() * startRadius,
                    center.y + kotlin.math.sin(angle).toFloat() * startRadius
                )

                val end = Offset(
                    center.x + kotlin.math.cos(angle).toFloat() * endRadius,
                    center.y + kotlin.math.sin(angle).toFloat() * endRadius
                )

                drawLine(
                    color = Color(0xFFB9EFFF),
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            val needleAngle = Math.toRadians(225.0)
            val needleLength = radius * 0.68f

            drawLine(
                color = AccentBlue,
                start = center,
                end = Offset(
                    center.x + kotlin.math.cos(needleAngle).toFloat() * needleLength,
                    center.y + kotlin.math.sin(needleAngle).toFloat() * needleLength
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
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "SPEED",
                color = SecondaryText,
                fontSize = 10.sp,
                letterSpacing = 3.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "TEST",
                color = PrimaryText,
                fontSize = 25.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }

        FloatingInfoCard(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 4.dp, bottom = 28.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(17.dp)
                )
            },
            label = "DOWNLOAD",
            value = "Fast"
        )

        FloatingInfoCard(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 2.dp, top = 24.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(17.dp)
                )
            },
            label = "ACCURATE",
            value = "Results"
        )
    }
}

@Composable
private fun ConnectionIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x334B76FF),
                            Color(0x115D66FF),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(42.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF203B65),
                                Color(0xFF142540)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp)
                ) {
                    val center = Offset(size.width / 2f, size.height / 2f)

                    drawCircle(
                        color = Color(0x335DD8FF),
                        radius = size.width * 0.43f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    drawCircle(
                        color = Color(0x335DD8FF),
                        radius = size.width * 0.29f,
                        center = center,
                        style = Stroke(width = 2.dp.toPx())
                    )

                    drawCircle(
                        color = AccentBlue,
                        radius = 7.dp.toPx(),
                        center = center
                    )

                    drawLine(
                        color = AccentBlue,
                        start = Offset(center.x, 0f),
                        end = Offset(center.x, size.height),
                        strokeWidth = 1.dp.toPx()
                    )

                    drawLine(
                        color = AccentBlue,
                        start = Offset(0f, center.y),
                        end = Offset(size.width, center.y),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                Icon(
                    imageVector = Icons.Default.NetworkCheck,
                    contentDescription = null,
                    tint = PrimaryText,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .width(26.dp)
                            .height(if (index == 2) 8.dp else 5.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == 2) AccentBlue
                                else Color(0xFF405574)
                            )
                    )
                }
            }
        }

        FloatingInfoCard(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 2.dp, top = 30.dp),
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
                .padding(end = 2.dp, bottom = 30.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(18.dp)
                )
            },
            label = "CONNECTION",
            value = "Quality"
        )
    }
}

@Composable
private fun WifiIllustration() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(270.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x3341D8FF),
                            Color(0x114C78FF),
                            Color.Transparent
                        )
                    )
                )
        )

        Canvas(
            modifier = Modifier.size(250.dp)
        ) {
            val centerX = size.width / 2f
            val centerY = size.height * 0.45f

            for (index in 1..3) {
                val radius = index * 31.dp.toPx()

                drawArc(
                    color = when (index) {
                        3 -> Color(0xFF5278D9)
                        2 -> Color(0xFF65CFFF)
                        else -> AccentBlue
                    },
                    startAngle = 225f,
                    sweepAngle = 90f,
                    useCenter = false,
                    topLeft = Offset(centerX - radius, centerY - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(
                        width = 9.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            drawCircle(
                color = AccentBlue,
                radius = 11.dp.toPx(),
                center = Offset(centerX, centerY + 35.dp.toPx())
            )

            drawCircle(
                color = Color(0x4465D6FF),
                radius = 22.dp.toPx(),
                center = Offset(centerX, centerY + 35.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CONNECTED",
                color = AccentBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Ready to test",
                color = PrimaryText,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        FloatingInfoCard(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 4.dp, top = 22.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = AccentBlue,
                    modifier = Modifier.size(17.dp)
                )
            },
            label = "WIFI",
            value = "Connected"
        )

        FloatingInfoCard(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 2.dp, bottom = 64.dp),
            icon = {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(17.dp)
                )
            },
            label = "SPEED",
            value = "Measure"
        )
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
        shape = RoundedCornerShape(16.dp),
        color = CardColor.copy(alpha = 0.96f),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 12.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(Color(0xFF253957)),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = label,
                    color = SecondaryText,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = value,
                    color = PrimaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}