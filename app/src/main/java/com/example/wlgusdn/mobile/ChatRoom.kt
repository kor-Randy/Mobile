package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wlgusdn.mobile.R.id.ChatRoom_RecyclerView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_chatroom.*


@SuppressLint("ValidFragment")
class ChatRoom(context : Context) : Fragment(){

    var thiscontext = context
    val database = FirebaseDatabase.getInstance().getReference()
    var ChatEditText : EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_chatroom, container, false) as View

        ChatEditText = view.findViewById(R.id.ChatRoom_ChatEditText)
        //val ChatEditText : EditText = findViewById(R.id.ChatRoom_ChatEditText)
        val ChatButton : Button = view.findViewById(R.id.ChatRoom_ChatButton)
        val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()


        val GalleryButton : ImageButton = view.findViewById(R.id.ChatRoom_GalleryButton)
        val OtherButton : Button = view.findViewById(R.id.ChatRoom_OtherButton)



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
        return view
    }



    //입력 받은 텍스트 chatList에 추가하기
    fun addRecyclerChat(who : String, text : String, time : String, chatList : MutableList<ChatRoom_Chat>) : MutableList<ChatRoom_Chat>{

        chatList.add(ChatRoom_Chat(who, text, time))

        val chatAdapter = Chatroom_ChatAdapter(chatList)
        ChatRoom_RecyclerView.adapter = chatAdapter
        ChatRoom_RecyclerView.layoutManager = LinearLayoutManager(context)

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
        val chat : ChatRoom_Chat = ChatRoom_Chat(userId, text, time)
        database.child("PromiseRoom").child("PromiseNumber").child("chatroom").push().setValue(chat)


    }


    fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(thiscontext.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
        //ChatEditText?.setText(intent.toString())
    }

    fun takePhoto() {
        val intent1 = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent1.resolveActivity(thiscontext.packageManager) != null) {
            startActivityForResult(intent1, REQUEST_TAKE_PHOTO)
        }
    }
    companion object {
        private val REQUEST_TAKE_PHOTO = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode  == -1){
            val imageURL : String = data?.data.toString()
            println("${imageURL}")
            ChatEditText?.setText(imageURL)

        }
    }


}