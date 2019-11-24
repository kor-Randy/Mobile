package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wlgusdn.mobile.R.id.ChatRoom_RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_chatroom.*
import java.lang.Exception


@SuppressLint("ValidFragment")
class ChatRoom(context : Context) : Fragment(){

    var thiscontext = context
    var database = FirebaseDatabase.getInstance().getReference()

    var ChatEditText : EditText? = null
    var filePath : Uri? = null
    val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()


    val username : String = AccountActivity.myname!!
    var roomnumber : String = "PromiseNumber"
    var chatAdapter : Chatroom_ChatAdapter ?= null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_chatroom, container, false) as View

        //ChatEditText  = findViewById(R.id.ChatRoom_ChatEditText)
        val ChatEditText : EditText = view.findViewById(R.id.ChatRoom_ChatEditText)

        val ChatButton : Button = view.findViewById(R.id.ChatRoom_ChatButton)
        //val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()

        val GalleryButton : ImageButton = view.findViewById(R.id.ChatRoom_GalleryButton)

        //var useredit : EditText = view.findViewById(R.id.ChatRoom_user)
        //userid = useredit.text.toString()


        try{
            roomnumber= PromiseRoom.roomId!!
        }catch (e: Exception){
            println("no room selected")
        }

        println("roomid: ${roomnumber}")
        ChatButton.setOnClickListener {

            //val who = userid
            //userid = useredit.text.toString()
            val who : String = username
            val time : String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)
            val text : String = ChatEditText.text.toString()

            //println("write button who ${who}, text ${text}  time ${time}")
            //addRecyclerChat(who, text, time, null ,ChatList)
            writeNewMessage(who, text, time)
            ChatEditText.setText("")

        }




        GalleryButton.setOnClickListener{
            selectImageInAlbum()
            //takePhoto()

        }


        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                println("datasnap ${dataSnapshot}")
                val who = dataSnapshot.child("who").value.toString()
                val text = dataSnapshot.child("text").value.toString()
                val time = dataSnapshot.child("time").value.toString()
                var resizedbitmap : Bitmap ?= null



                println("read who ${who}, text ${text}  time ${time}")
                addRecyclerChat(username, who, text, time, resizedbitmap, ChatList)


            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        database.child("PromiseRoom").child(roomnumber).child("chatroom").addChildEventListener(childEventListener)


        return view
    }



    //입력 받은 텍스트 chatList에 추가하기
    fun addRecyclerChat(username : String, who : String, text : String, time : String, image : Bitmap ?= null, chatList : MutableList<ChatRoom_Chat>) : MutableList<ChatRoom_Chat>{


        chatList.add(ChatRoom_Chat(who, text, time, image))

        chatAdapter = Chatroom_ChatAdapter(roomnumber, username, chatList)
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

        //database.child("message").push().setValue(chat)

        database.child("PromiseRoom").child(roomnumber).child("chatroom").push().setValue(chat)

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
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM){
                val url : Uri ?= data?.data
                filePath = url
                val time: String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)


                try{
                    val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(thiscontext.contentResolver, url)

                    val resizedBitmap = resizeBitmap(bitmap)
                    addPhotoDB(username, time, resizedBitmap)
                    //addRecyclerChat(username, username, "", time, resizedBitmap, ChatList)
                    //writeNewMessage(userid, filename, time)
                    //addGridPhoto("user", time, resizedBitmap)

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
            else{
                Log.d("Activity result", "sth wrong")
            }
        }

    }

    private fun resizeBitmap(bitmap:Bitmap):Bitmap{
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

    private fun addPhotoDB(who : String, time : String, image : Bitmap){


        val photo = PhotoRoom_Photo(who, time, image)
        //databaseRef.child("photoroom").push().setValue(photo)
        //database.child("PromiseRoom").child("PromiseNumber").child("chatroom").push().setValue(chat)



        if(filePath != null){
            val storage = FirebaseStorage.getInstance()
            val format = SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault())
            val now = Date()
            val filename = format.format(now) + "_"+ who + ".jpg"
            val storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Photoroom/" + roomnumber + "/" + filename)
            println("filename: ${filename} filePath ${filePath}")
            storageRef.putFile(filePath!!)
                .addOnSuccessListener( OnSuccessListener<UploadTask.TaskSnapshot>() {

                    Toast.makeText(thiscontext, "Saved to DB", Toast.LENGTH_LONG).show()
                    chatAdapter?.notifyDataSetChanged()


                })
                .addOnFailureListener( OnFailureListener() {
                    Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show()
                })



            writeNewMessage(who, filename, time)

        }else{
            Toast.makeText(thiscontext, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
        filePath = null

    }


}