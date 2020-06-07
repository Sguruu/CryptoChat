package com.example.skillboxcryptochat.model

data class DataMessage(
    val text: String,
    val date: String,
    val userName: String,
    val isOutgoing: Boolean
)