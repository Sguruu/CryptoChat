package com.example.skillboxcryptochat.recycler

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skillboxcryptochat.model.DataMessage
import com.example.skillboxcryptochat.R

class MessageController(
    //для отображения входящих
    //0 -  мои сообщения
    //1- чужие
    //RowType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var messageList: List<DataMessage> = listOf()
    private var mComtext: Context? = null
    private var myMessage: Boolean = false

    override fun getItemViewType(position: Int): Int {
        if (messageList.get(position).isOutgoing == false) {
            return RowType.INCOMING_VIEW_MESSAGE_CONTROLLER
        } else if (messageList.get(position).isOutgoing == true) {
            return RowType.VIEW_MESSAGE_CONTROLLER
        } else
            return -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == RowType.INCOMING_VIEW_MESSAGE_CONTROLLER) {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.incoming_messages,
                parent,
                false
            )
            return HolderIncomingMessage(view)
        } else {
            val view: View = LayoutInflater.from(parent.context).inflate(
                R.layout.message,
                parent,
                false
            )
            return HolderMyMessage(view)
        }
    }


    override fun getItemCount(): Int {
        return messageList.size
    }


    fun setData(messageList: List<DataMessage>, context: Context?, myMessage: Boolean) {
        this.myMessage = myMessage
        this.messageList = messageList
        mComtext = context
        //обновление данных
        notifyDataSetChanged()
    }

    fun setMessage(myMessage: Boolean) {
        this.myMessage = myMessage
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is HolderMyMessage) {
            holder.userInput.setText(messageList.get(position).text)
            holder.userName.setText(messageList.get(position).userName)
            holder.messageData.setText(messageList.get(position).date)
            holder.imageView
        } else if (holder is HolderIncomingMessage) {
            holder.incomingUserInput.setText(messageList.get(position).text)
            holder.incomingUserName.setText(messageList.get(position).userName)
            holder.incomingMessageData.setText(messageList.get(position).date)
            holder.incomingImageView
        }
    }


    inner class HolderMyMessage(view: View) : RecyclerView.ViewHolder(view) {
        //отправка
        val userInput: TextView = view.findViewById(R.id.userInput)
        val userName: TextView = view.findViewById(R.id.userName)
        val messageData: TextView = view.findViewById(R.id.messageDate)
        val imageView: ImageView = view.findViewById(R.id.imageView)

    }

    inner class HolderIncomingMessage(view: View) : RecyclerView.ViewHolder(view) {
        //получить данные
        val incomingUserInput: TextView = view.findViewById(R.id.incoming_userInput)
        val incomingUserName: TextView = view.findViewById(R.id.incoming_userName)
        val incomingMessageData: TextView = view.findViewById(R.id.incoming_messageDate)
        val incomingImageView: ImageView = view.findViewById(R.id.incoming_imageView)
    }


}


