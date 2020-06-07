package com.example.skillboxcryptochat.model

import com.google.gson.annotations.SerializedName

//хранится инфа о пользователе
data class User(
    @SerializedName("accessToken")
    val id: Long,
    @SerializedName("name")
    val name: String
)

//сообщает сервер
data class UserStatus(
    @SerializedName("user")
    val user: User,
    @SerializedName("connected")
    val connected: Boolean //true подключился, false
)

data class UserName(
    @SerializedName("name")
    private val user: String
)

//для входящих и исходящих сообщений
data class Message constructor(
    @SerializedName("sender")//кто отправитель, пишем 0 отправитель сервер сам
    //должен разобраться
    val sender: Long = 0,
    @SerializedName("encodedText")//текст сообщения
    val encodedText: String, //текст сообщения
    @SerializedName("receiver") // кто отправитель
    val receiver: Long
) {
    constructor(encodedText: String, receiver: Long) : this(
        sender = 0,
        encodedText = encodedText,
        receiver = receiver
    )
}