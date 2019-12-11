package com.example.wlgusdn.mobile

import android.Manifest
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*
//import android.support.v7.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.getExternalFilesDirs
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.wlgusdn.mobile.R.id.ChatRoom_RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_chatroom.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files.createFile


@SuppressLint("ValidFragment")
class ChatRoom(context : Context) : Fragment(){

    var thiscontext = context
    var database = FirebaseDatabase.getInstance().getReference()

    var ChatEditText : EditText? = null
    var filePath : Uri? = null
    val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()

    val username : String = AccountActivity.myname!!
    var prd : PromiseRoomData?=null
    var tokenarr : ArrayList<FriendData> = ArrayList<FriendData>()
    var userarr : ArrayList<UserData> = ArrayList<UserData>()
    var roomnumber : String = "PromiseNumber"
    var chatAdapter : Chatroom_ChatAdapter ?= null
    private val FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send"
    private val SERVER_KEY = "AIzaSyCAnCD8LTr3J6nBTBNCW2ciCBj5NOYNfHQ"
    private val PERMISSION_REQUEST_CODE: Int = 101
    private var mCurrentPhotoPath: String? = null;



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_chatroom, container, false) as View

        //ChatEditText  = findViewById(R.id.ChatRoom_ChatEditText)
        val ChatEditText : EditText = view.findViewById(R.id.ChatRoom_ChatEditText)

        val ChatButton : Button = view.findViewById(R.id.ChatRoom_ChatButton)
        //val ChatList : MutableList<ChatRoom_Chat> = arrayListOf()

        val GalleryButton : ImageButton = view.findViewById(R.id.ChatRoom_GalleryButton)
        val CameraButton : ImageButton = view.findViewById(R.id.ChatRoom_CameraButton)




        try{
            roomnumber= PromiseRoomActivity.roomId!!


            database.child("PromiseRoom").child(roomnumber).addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {


                }

                override fun onDataChange(p0: DataSnapshot) {

                    Log.d("wlgusdn111",roomnumber.toString())

                    prd = p0.getValue(PromiseRoomData::class.java)


                    tokenarr = prd!!.Participants!!
                    for(i in 0..tokenarr.size-1) {

                        database.child("Account").child(tokenarr[i].Id!!).addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {


                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                userarr.add(p0.getValue(UserData::class.java)!!)

                            }
                        })
                    }

                }
            })

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
            sendPostToFCM(text)

        }




        GalleryButton.setOnClickListener{
            selectImageInAlbum()


        }


        CameraButton.setOnClickListener(View.OnClickListener {

            if (checkPersmission()) takePicture()
            else requestPermission()
        })










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


        database.child("PromiseRoom").child(roomnumber).child("chatroom").push().setValue(chat)

    }



    private fun sendPostToFCM(message: String) {


        Thread(object : Runnable {

            override fun run() {
                try {
                    for(i in 0..tokenarr.size-1) {
                        // FMC 메시지 생성 start
                        val root = JSONObject()
                        val notification = JSONObject()
                        val data = JSONObject()
                        notification.put("body", message)
                        notification.put("title", getString(R.string.app_name))

                        data.put("score","5x1")
                        data.put("time","20:33")

                        root.put("data",data)
                        root.put("notification", notification)
                        root.put("collapse_key", "Chat")
                        root.put("time_to_live",10)
                        root.put("to",userarr[i].Token )   // FMC 메시지 생성 end

                        Log.d("wlgusdn111",userarr[i].Token.toString())

                        val Url = URL(FCM_MESSAGE_URL)
                        val conn = Url.openConnection() as HttpURLConnection

                        conn.requestMethod = "POST"
                        conn.doOutput = true
                        conn.doInput = true

                        conn.addRequestProperty("Authorization", "key=$SERVER_KEY")
                        conn.setRequestProperty("Accept", "application/json")
                        conn.setRequestProperty("Content-type", "application/json")
                        val os = conn.outputStream
                        os.write(root.toString().toByteArray(charset("utf-8")))
                        os.flush()
                        conn.responseCode

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }).start()

    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(thiscontext.packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }


    private fun takePicture() {

        val intent: Intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val file: File = createFile()

        val uri: Uri = FileProvider.getUriForFile(
                thiscontext,
                BuildConfig.APPLICATION_ID,
                file
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)

    }




    companion object {
        private val REQUEST_IMAGE_CAPTURE = 0
        private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    takePicture()

                } else {
                    Toast.makeText(thiscontext, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }

            else -> {

            }
        }
    }






    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == REQUEST_SELECT_IMAGE_IN_ALBUM)
            {
                val url : Uri ?= data?.data
                filePath = url
                val time: String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)


                try{
                    val bitmap : Bitmap = MediaStore.Images.Media.getBitmap(thiscontext.contentResolver, url)

                    val resizedBitmap = resizeBitmap(bitmap)
                    addPhotoDB(username, time, resizedBitmap)

                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            else if (requestCode == REQUEST_IMAGE_CAPTURE) {

                val auxFile = File(mCurrentPhotoPath)

                val time: String = DateUtils.fromMillisToTimeString(Calendar.getInstance().timeInMillis)

                var bitmap: Bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath)
                val resizedBitmap = resizeBitmap(bitmap)


                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100 , auxFile.outputStream())
                filePath = Uri.fromFile(auxFile)
                addPhotoDB(username, time, resizedBitmap)

            }


            else{
            }


        }




    }


    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(thiscontext, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(thiscontext,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }



    private fun requestPermission() {
        ActivityCompat.requestPermissions(thiscontext as Activity, arrayOf(READ_EXTERNAL_STORAGE, CAMERA), PERMISSION_REQUEST_CODE)
    }



    @Throws(IOException::class)
    private fun createFile(): File {

        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = activity!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(
                "JPEG_${timeStamp}_", /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        ).apply {
            mCurrentPhotoPath = absolutePath


        }

    }






    private fun resizeBitmap(bitmap:Bitmap):Bitmap{
        val w : Int = bitmap.width
        val h : Int = bitmap.height


        var ratio = 400.0/w
        var width = w * ratio
        var height = h * ratio

        return Bitmap.createScaledBitmap(
                bitmap,
                width.toInt(),
                height.toInt(),
                false
        )
    }



    private fun addPhotoDB(who : String, time : String, image : Bitmap){


        val photo = PhotoRoom_Photo(who, time, image)

        println("filePath: ${filePath}")

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


