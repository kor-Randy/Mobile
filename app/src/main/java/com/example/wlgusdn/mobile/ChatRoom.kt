package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_chatroom.*


class ChatRoom : AppCompatActivity(){

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)


        val ChatEditText : EditText = findViewById(R.id.ChatRoom_ChatEditText)
        val ChatButton : Button = findViewById(R.id.ChatRoom_ChatButton)
        val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()


        val OtherButton : Button = findViewById(R.id.ChatRoom_OtherButton)



        ChatButton.setOnClickListener {
            val who = "my"
            val time : String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)
            val text : String = ChatEditText.text.toString()

            addRecyclerChat(who, text, time, ChatList)
            ChatEditText.setText("")
        }

        OtherButton.setOnClickListener {
            val who = "other"
            val time : String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)
            val text : String = ChatEditText.text.toString()

            addRecyclerChat(who , text, time, ChatList)
            ChatEditText.setText("")
        }

    }


    //입력 받은 텍스트 chatList에 추가하기
    fun addRecyclerChat(who : String, text : String, time : String, chatList : MutableList<ChatRoom_Chat>) : MutableList<ChatRoom_Chat>{

        chatList.add(ChatRoom_Chat(who, text, time))

        val chatAdapter = Chatroom_ChatAdapter(chatList)
        ChatRoom_RecyclerView.adapter = chatAdapter
        ChatRoom_RecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        return chatList
    }


    //현시간 가져오기
    object DateUtils {
        fun fromMillisToTimeString(millis: Long) : String {
            val format = SimpleDateFormat("hh:mm a", Locale.getDefault())
            return format.format(millis)
        }
    }



}