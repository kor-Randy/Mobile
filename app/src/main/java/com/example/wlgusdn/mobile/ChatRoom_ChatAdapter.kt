package com.example.wlgusdn.mobile

//import android.support.v7.widget.RecyclerView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.storage.FirebaseStorage


class Chatroom_ChatAdapter (userid: String, chatList : MutableList<ChatRoom_Chat>) : RecyclerView.Adapter<Chatroom_ChatAdapter.ChatViewHolder>(){

    private var chatList : MutableList<ChatRoom_Chat>? = chatList
    private var userid : String = userid
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

                try{
                    val check = chat.text.split(".")
                    if (check[1] == "jpg"){
                        println("adapter_mytext is image")
                        p0.myText.visibility = View.INVISIBLE
                    }
                }catch (e : Exception){}
                if (chat.image != null){
                    p0.myImage.setImageBitmap(chat.image)
                    p0.myText.visibility = View.INVISIBLE

                }

            }


            is OtherViewHolder ->{
                p0.otherUser.text = chat.who
                p0.otherTextTime.text = chat.time

                try {
                    var check = chat.text.split(".")
                    if(check[1] == "jpg"){
                        p0.otherText.text = "image"


                        val storage = FirebaseStorage.getInstance()
                        var storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Photoroom/").child(chat.text)

                        storageRef.getBytes(1024 * 1024).addOnSuccessListener{
                            println("adapter_image downloaded")

                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

                            var resizedbitmap = resizeBitmap(bitmap)
                            p0.otherImage.setImageBitmap(resizedbitmap)
                            p0.otherText.visibility = View.INVISIBLE

                        }.addOnFailureListener {
                            //Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show()
                        }




                    }
                }catch (e : Exception){p0.otherText.text = chat.text}
            }
        }

    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val w : Int = bitmap.width
        val h : Int = bitmap.height
        //println("w: ${w}, h: ${h}")


        var ratio = 400.0/w
        var width = w * ratio
        var height = h * ratio

        //println("w: ${width}, h: ${height}")
        return Bitmap.createScaledBitmap(
                bitmap,
                width.toInt(),
                height.toInt(),
                false
        )
    }

    override fun getItemCount(): Int {
        return chatList!!.size
    }

    override fun getItemViewType(position: Int): Int {
        val chat = chatList!![position]
        val who = chat.who

        return if(who == userid) {
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
        var myImage : ImageView = view.findViewById(R.id.ChatRoom_MyImage)



    }

    class OtherViewHolder(view: View) : ChatViewHolder(view){

        var otherUser : TextView = view.findViewById(R.id.ChatRoom_OtherUser)
        var otherText : TextView = view.findViewById(R.id.ChatRoom_OtherText)
        var otherTextTime : TextView = view.findViewById(R.id.ChatRoom_OtherTextTime)
        var otherImage : ImageView = view.findViewById(R.id.ChatRoom_OtherImage)

    }


}