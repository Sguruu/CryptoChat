package com.example.skillboxcryptochat

import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skillboxcryptochat.model.DataMessage
import com.example.skillboxcryptochat.model.User
import com.example.skillboxcryptochat.model.UserStatus
import com.example.skillboxcryptochat.recycler.MessageController
import java.text.SimpleDateFormat
import java.util.*
import java.util.function.Consumer

class MainActivity : AppCompatActivity() {

    private lateinit var sendButton: Button
    private lateinit var userInput: EditText
    private lateinit var chatWindow: RecyclerView

    //статус бар
    private lateinit var actionBar: ActionBar

    private val messageController = MessageController()
    private val dataMessage = mutableListOf<DataMessage>()

    //сервер
    private lateinit var server: NewServer
    private var myUserName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        recyclerInit()
        connectServer()
        getUserName()
        welcomeMessage()


        sendButton.setOnClickListener(View.OnClickListener {
            dataMessageListAdd(userInput.text.toString(), myUserName, myMessage = true)
            //отправка на сервер
            server.sendMessage(userInput.text.toString())
            //очистка поля
            userInput.setText("")
            //для тестирования
            imitationMessage()
        })
    }

    private fun init() {
        sendButton = findViewById(R.id.sendButton)
        userInput = findViewById(R.id.userInput)
        chatWindow = findViewById(R.id.chatWindow)
        actionBar = this.supportActionBar!!
        actionBar.setTitle("Крипто чат")
    }

    private fun recyclerInit() {
        //список
        println("initRecyclerView: initializing staggered recyclerview")
        chatWindow = findViewById(R.id.chatWindow)
        // messageController.setData(dataMessage, context = this, )
        // val layoutManagerGird = StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)
        val layoutManager = LinearLayoutManager(this)
        //  layoutManager.reverseLayout =true
        //  layoutManager.stackFromEnd = true
        chatWindow.layoutManager = layoutManager
        // layoutManagerGird.reverseLayout = true
        //  layoutManagerGird.fr
        chatWindow.adapter = messageController
    }

    private fun connectServer() {
        //сервер
        server = NewServer(java.util.function.Consumer {
            //для того чтобы взаимодействовать с основным потокам нужно
            //позволяет из фонового потока выполнить в основном
            runOnUiThread(Runnable {
                //передача сообщения входящего Активити
                dataMessageListAdd(text = it.second, userName = it.first, myMessage = false)
                println("fun connectServer показ")
            })
        }, Consumer {
            //передача сообщения в Тоаст
            runOnUiThread({
                toast(connect = it.first, name = it.second)
                //количество пользователей онлайн
                actionBar.setTitle("Крипто чат | Онлайн: ${it.third}")
            })
        })

    }

    //добавление в базу данных
    private fun dataMessageListAdd(text: String, userName: String, myMessage: Boolean) {
        messageController.setMessage(myMessage)
        dataMessage.add(
            DataMessage(
                text = text, date = dateText(), userName = userName
                , isOutgoing = myMessage
            )
        )
        dispathAdapter(myMessage)
    }

    //диалог который узнает имя пользователя
    private fun getUserName() {
        //создания диалога
        val build: AlertDialog.Builder = AlertDialog.Builder(this)
        build.setTitle("Введите свое имя")
        val input: EditText = EditText(this)
        build.setView(input)
        build.setPositiveButton("Save", DialogInterface.OnClickListener { dialog, which ->
            myUserName = input.text.toString()
            //отослать на сервер
            server.sendUserName(myUserName)
        }).show()
    }

    private fun dateText(): String {
        //работа с датой
        //текущее время
        val currentDate = Date()
        //формирование времени
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val dateText = dateFormat.format(currentDate)
        //формирование времени часы минуты секунды
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val timeText = timeFormat.format(currentDate)
        return "$timeText $dateText"
    }

    //отправка в адаптер
    private fun dispathAdapter(myMessage: Boolean) {
        messageController.setData(dataMessage, context = this, myMessage = myMessage)
        println("Статус сообщения $myMessage")
        chatWindow.scrollToPosition(dataMessage.size - 1)
    }

    //имитация чужих сообщений
    private fun imitationMessage() {
        var imitationMessage = true
        runOnUiThread({
            imitationMessage = server.turnOffSimulation
        })
        if (!imitationMessage) {
            println("MainActivity  private fun imitationMessage() : имитация отключена ")
        } else {
            println("MainActivity  private fun imitationMessage() : имитация включена ")
            taimer()
        }

    }

    private fun taimer() {
        //объект таймера
        val timer = object : CountDownTimer(3000, 1000) {
            //действия каждый интервал
            override fun onTick(millisUntilFinished: Long) {
                println("проверка таймер")
            }

            //действия по заверению интервала
            override fun onFinish() {
                messageController.setMessage(false)
                dataMessageListAdd(
                    "Если вы видите это сообщение, значит сервер не доступен !",
                    "Тяпа",
                    myMessage = false
                )
            }
        }
        timer.start()
    }

    // первое приведственное сообщение (входящие)
    private fun welcomeMessage() {
        val message = """ 
            Информационное сообщение:
            Привет ! Ты находишься в зашифрованном чате.
            Сообщения отправленные тобой:
            1. Сообщения шифруются по алгоритму "SHA-256"
            2. Шифруются по алгортиму "AES"
            Сообщение которые ты видешь также дишефруются с участием секретного ключа, который 
            состоит из секретного слова !
            3. Использованы следующие технологии/библиотеки : 
            1.okhttp3, 2.gson, 3.работа с потоками, 4.сетевой протокол типа websocket
            4. Адрес сервера : ${server.URL}
            5. Секретное слово: ${Crypto.pass}
        """.trimIndent()
        dataMessageListAdd(message, "Бот", false)
    }


    /// Toast появление информации о подключившемся пользователе
    fun toast(connect: Boolean = true, name: String = "Пустое имя") {
        if (connect) {
            val text = "$name подключился к чату"
            val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
            toast.show()
        } else {
            val text = "$name отключился от чата"
            val toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
            toast.show()
        }
    }
}



