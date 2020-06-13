package com.example.skillboxcryptochat

import com.example.skillboxcryptochat.model.Message
import com.example.skillboxcryptochat.model.UserName
import com.example.skillboxcryptochat.model.UserStatus
import com.google.gson.Gson
import okhttp3.*
import okio.ByteString
import java.lang.Exception
import java.util.function.Consumer

class NewServer(
    //для передачи между активити
    private var onMessagwReceive: Consumer<Pair<String, String>>
) {
    //для хранения пользователя и его переменно
    val names = mutableMapOf<Long, String>()
    // private lateinit var  onMessagwReceive : Consumer <Pair<String,String>>

    //создаем клиент
    val client = OkHttpClient.Builder().build()

    //создали запрос
    val request = Request.Builder()
        .url("ws://138.197.189.159:8881")
        .build()
    //отключение симулятора сообщений
    //true - включить false - отключить
    var turnOffSimulation: Boolean = true


    //создаем сокет
    val webSocket = client.newWebSocket(request, object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("подсоединение к серверу успешно !" + response)
           turnOffSimulation = false


        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            super.onFailure(webSocket, t, response)
            println("Хз что это наверно ошибка" + response)
            println(t.localizedMessage)
           turnOffSimulation = true

        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosing(webSocket, code, reason)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            super.onMessage(webSocket, text)
            //тут принимаем сообщения
            println(text)

            //получаем тип сообщения
            val type = NewProtocol().getType(text)
            if (type == NewProtocol().USER_STATUS) {
                //обработать факт подключения или отключения пользователя
                userStatusChanged(text)
            }
            if (type == NewProtocol().MESSAGE) {
                //показать сообщения на экране
                displayIncomingMessage(text)

            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            super.onMessage(webSocket, bytes)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            super.onClosed(webSocket, code, reason)

        }
    })

    //метод для отоюражения пришедшие сообщение
    fun displayIncomingMessage(json: String) {
        //распакуем тек сообщение
        val m: Message = NewProtocol().unpackMessage(json)
        //узнаем кто отправитель и что у него за текст
        m.encodedText //текст
        m.sender //кто отправитель

        //берем имя пользователя из карты имен
        var name: String
        if (names.get(m.sender) == null) {
            name = "Безымянный"
        } else
            name = names.get(m.sender).toString()
        var text = m.encodedText
        //шифруем сообщения
        try {
            text = Crypto.decrypt(text)
            println("Класс NewServer displayIncomingMessage шифр  успещен ")
        } catch (e: Exception) {
            println("Класс NewServer displayIncomingMessage шифр не успещен " + e.printStackTrace())
        }

        onMessagwReceive.accept(
            Pair(name, text)
        )//имя и сообщение
    }

    //метод для связи имени пользователя и его айди
    //при изменении статуса пользователя будет вызываться этот метод
    fun userStatusChanged(json: String) {
        //распаковка статуса пользователя
        val s: UserStatus = NewProtocol().UserStatus(json)
        //если пользователь подключился
        if (s.connected) {
            names.put(s.user.id, s.user.name)
        } else //если пользователь отключился
        //удаляем по id
            names.remove(s.user.id)
    }

    //отправка сообщений
    fun sendMessage(m: String) {
        //отправление сообщений
        //проверка успел ли клиент подключиться к серверу
        if (client == null) {
            return
        } else {
            var text = m
            try {
                //шифруем
                text = Crypto.encrypt(text)
                println("Класс NewServer  fun sendMessage шифр  успешен ! ")
                //
            } catch (e: Exception) {
                println("Класс NewServer  fun sendMessage шифр не успешен ! " + e.printStackTrace())

            }
            val message: String = NewProtocol()
                .packMessage(Message(encodedText = text, receiver = NewProtocol().GROUP_CHAT))
            println("messege: " + webSocket.send(message) + message)
        }
    }

    //метод для получения и отправки имени из диалога
    fun sendUserName(name: String) {
        //проверка что клиент работает и подключился к серверу
        if (client == null) {
            return
        } else {
            val myName: String = NewProtocol().packName(UserName(name))
            println("onOpen send " + webSocket.send(myName))
            println(myName)
        }
    }
}

//********************************************************************************************
//********************************************************************************************
//********************************************************************************************
//********************************************************************************************
//********************************************************************************************
//********************************************************************************************
class NewProtocol() {
    //кому отправить id группового чата
    val GROUP_CHAT: Long = 1

    //статические параметры которые зависят от сервера
    val USER_STATUS = 1
    val MESSAGE = 2
    val USER_NAME = 3
    //

    //класс которые работает с json
    //UserName -> "3{"name":"Морти"}"
    fun packName(name: UserName): String {
        val g = Gson()
        return USER_NAME.toString() + g.toJson(name)
    }

    //упаковка для отправлений сообщений
    fun packMessage(message: Message): String {
        val g = Gson()
        return MESSAGE.toString() + g.toJson(message) //"2{sender:1,receiver:4,encodedText:"привет"}"
    }

    //распаковка сообщений
    fun unpackMessage(json: String): Message {
        val g = Gson()
        //substring отрезает первую цифру
        return g.fromJson(json.substring(1), Message::class.java)
    }

    //функция для получения типа сообщения
    //-1 неизвестный тип
    fun getType(json: String?): Int {
        if (json == null || json.length == 0)
            return -1
        else
        //получаем 1 элемент
        //приводим это число к типу int
            return json.substring(0, 1).toInt()
    }

    //распаковка статуса пользователя
    fun UserStatus(json: String): UserStatus {
        val g = Gson()
        return g.fromJson(json.substring(1), UserStatus::class.java)
    }
}

