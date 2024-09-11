package com.example.composespeedtest

data class UiState(
    val arcValue: Float = 0f,
    val speed: String = "0.0 mbps",
    val ping: String = "-",
    val maxSpeed: String = "100",
    val inProgress: Boolean = false
)
