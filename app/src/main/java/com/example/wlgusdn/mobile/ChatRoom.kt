package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chatroom.*


class ChatRoom : AppCompatActivity(){

    val database = FirebaseDatabase.getInstance().reference
    var dbcount : Int = 0
    var ChatEditText : EditText? = null

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatroom)


        ChatEditText = findViewById(R.id.ChatRoom_ChatEditText)
        //val ChatEditText : EditText = findViewById(R.id.ChatRoom_ChatEditText)
        val ChatButton : Button = findViewById(R.id.ChatRoom_ChatButton)
        val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()


        val GalleryButton : Button = findViewById(R.id.ChatRoom_GalleryButton)
        val OtherButton : Button = findViewById(R.id.ChatRoom_OtherButton)



        ChatButton.setOnClickListener {
            val who = "user"
            val time : String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)
            val text : String = ChatEditText?.text.toString()

            addRecyclerChat(who, text, time, ChatList)
            writeNewMessage(who, text, time)
            ChatEditText?.setText("")
        }

        OtherButton.setOnClickListener {
            val who = "other"
            val time : String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)
            val text : String = ChatEditText?.text.toString()

            addRecyclerChat(who , text, time, ChatList)
            writeNewMessage(who, text, time)
            ChatEditText?.setText("")
        }


        GalleryButton.setOnClickListener{
            selectImageInAlbum()
            //takePhoto()

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

    private fun writeNewMessage(userId: String, text: String, time: String) {
        dbcount = dbcount + 1

        database.child(dbcount.toString()).child("who").setValue(userId)
        database.child(dbcount.toString()).child("text").setValue(text)
        database.child(dbcount.toString()).child("time").setValue(time)

    }


    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
        //ChatEditText?.setText(intent.toString())
    }

    fun takePhoto() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }
    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }



}