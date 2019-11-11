package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
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
import com.example.wlgusdn.mobile.R.id.ChatRoom_RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_chatroom.*
import java.lang.Exception


@SuppressLint("ValidFragment")
class ChatRoom(context : Context) : Fragment(){

    var thiscontext = context
    val database = FirebaseDatabase.getInstance().getReference()
    var ChatEditText : EditText? = null
    var filePath : Uri? = null
    val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_chatroom, container, false) as View

        ChatEditText = view.findViewById(R.id.ChatRoom_ChatEditText)
        //val ChatEditText : EditText = findViewById(R.id.ChatRoom_ChatEditText)
        val ChatButton : Button = view.findViewById(R.id.ChatRoom_ChatButton)
        //val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()

        val GalleryButton : ImageButton = view.findViewById(R.id.ChatRoom_GalleryButton)
        val OtherButton : Button = view.findViewById(R.id.ChatRoom_OtherButton)



        ChatButton.setOnClickListener {
            val who = "user"
            val time : String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)
            val text : String = ChatEditText?.text.toString()

            addRecyclerChat(who, text, time, null ,ChatList)
            writeNewMessage(who, text, time)
            ChatEditText?.setText("")

        }


        OtherButton.setOnClickListener {
            val who = "other"
            val time : String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)
            val text : String = ChatEditText?.text.toString()

            addRecyclerChat(who , text, time, null, ChatList)
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
    fun addRecyclerChat(who : String, text : String, time : String, image : Bitmap?= null, chatList : MutableList<ChatRoom_Chat>) : MutableList<ChatRoom_Chat>{

        chatList.add(ChatRoom_Chat(who, text, time, image))

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
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM){
                var url : Uri?= data?.data
                filePath = url
                var time: String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)

                try{
                    val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(thiscontext.contentResolver, url)
                    //val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(thiscontext.contentResolver)
                    addPhotoDB("user", time, bitmap)
                    val resizedBitmap = resizeBitmap(bitmap)
                    //imageView.setImageBitmap(bitmap)
                    addRecyclerChat("user", "", time, resizedBitmap, ChatList)
                    //addGridPhoto("user", time, resizedBitmap)

                }catch (e: Exception){
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


        val photo = PhotoRoom_Photo(who, time, image, image)
        //database.child("PromiseRoom").child("PromiseNumber").child("photoroom").push().setValue(photo)

        if(filePath != null){
            var storage = FirebaseStorage.getInstance()
            val format = SimpleDateFormat("yyyyMMHHmmss", Locale.getDefault())
            var now = Date()
            var filename = format.format(now) + "_"+ who + ".jpg"
            var storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Photoroom/" + filename)
            println("filename: ${filename} filePath ${filePath}")
            storageRef.putFile(filePath!!)
                .addOnSuccessListener( OnSuccessListener<UploadTask.TaskSnapshot>() {

                    Toast.makeText(thiscontext, "Saved to DB", Toast.LENGTH_LONG).show()
                })
                .addOnFailureListener( OnFailureListener() {
                    Toast.makeText(getApplicationContext(), "업로드 실패!", Toast.LENGTH_SHORT).show()
                })

        }else{
            Toast.makeText(thiscontext, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
        filePath = null

    }


}