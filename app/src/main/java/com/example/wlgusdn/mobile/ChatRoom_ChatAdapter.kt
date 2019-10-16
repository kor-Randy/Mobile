package com.example.wlgusdn.mobile

//import android.support.v7.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class Chatroom_ChatAdapter (chatList : MutableList<ChatRoom_Chat>) : RecyclerView.Adapter<Chatroom_ChatAdapter.ChatViewHolder>(){

    private var chatList : MutableList<ChatRoom_Chat>? = chatList
    private val VIEW_TYPE_MY_MESSAGE = 1
    private val VIEW_TYPE_OTHER_MESSAGE = 2


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ChatViewHolder {

        return if (viewType == VIEW_TYPE_MY_MESSAGE) {
            MyViewHolder(
                LayoutInflater.from(p0.context).inflate(R.layout.activity_chatroom_my, p0, false)
            )
        } else {
            OtherViewHolder(
                LayoutInflater.from(p0.context).inflate(R.layout.activity_chatroom_other, p0, false)
            )
        }

    }

    override fun onBindViewHolder(p0: ChatViewHolder, position: Int) {

        var chat = chatList!![position]

        when (p0){
            is MyViewHolder -> {
                p0.myText.text = chat.text
                p0.myTextTime.text = chat.time

            }
            is OtherViewHolder ->{
                p0.otherText.text = chat.text
                p0.otherTextTime.text = chat.time
            }
        }

    }

    override fun getItemCount(): Int {
        return chatList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        val chat = chatList!![position]
        val who = chat.who

        return if(who == "user") {
            VIEW_TYPE_MY_MESSAGE
        }
        else {
            VIEW_TYPE_OTHER_MESSAGE
        }
    }


    open class ChatViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        //open fun bind(message:Message) {}
    }

    class MyViewHolder(view: View) : ChatViewHolder(view){

        var myText : TextView = view.findViewById(R.id.ChatRoom_MyText)
        var myTextTime : TextView = view.findViewById(R.id.ChatRoom_MyTextTime)

    }

    class OtherViewHolder(view: View) : ChatViewHolder(view){

        var otherText : TextView = view.findViewById(R.id.ChatRoom_OtherText)
        var otherTextTime : TextView = view.findViewById(R.id.ChatRoom_OtherTextTime)

    }


}