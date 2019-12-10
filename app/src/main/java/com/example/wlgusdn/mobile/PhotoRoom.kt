package com.example.wlgusdn.mobile



import android.annotation.SuppressLint
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
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.wlgusdn.mobile.R.id.ChatRoom_RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import kotlinx.android.synthetic.main.activity_photoroom_grid.view.*
import kotlinx.android.synthetic.main.fragment_chatroom.*
import java.io.File
import java.io.IOException
import java.lang.Exception


@SuppressLint("ValidFragment")
class PhotoRoom(context : Context) : Fragment(){
    var thiscontext = context
    val PhotoList: MutableList<PhotoRoom_Photo> = arrayListOf()
    //val photo by lazy {intent.extras[]}
    val database = FirebaseDatabase.getInstance().getReference()
    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Photoroom/")

    var auth = FirebaseAuth.getInstance()

    var gridview: GridView ?= null
    var imageview : ViewPager?= null
    var image: ImageView ?= null
    var view_change : Int = 1
    var selected_photo : String = ""
    val adapter = PhotoRoom_Adapter(PhotoList)
    var roomnumber : String = "PromiseNumber"


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_photoroom, container, false) as View



        //auth.signInAnonymously()

        val user = auth.currentUser



        try{
            roomnumber= PromiseRoomActivity.roomId!!
        }catch (e: Exception){
            println("no room selected")
        }

        storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Photoroom/" + roomnumber + "/")

        gridview = view.findViewById(R.id.PhotoRoom_Gridview)
        //imageview = findViewById(R.id.PhotoRoom_ImagePage)
        image = view.findViewById(R.id.PhotoRoom_ImageView)
        val download : Button= view.findViewById(R.id.PhotoRoom_download)

        image?.visibility = View.INVISIBLE

        gridview?.adapter = adapter




        image?.setOnClickListener {
            view_change = 1
            onclick()
        }

        download.setOnClickListener {



            when(view_change){
                1 -> {

                    val builder = AlertDialog.Builder(thiscontext)
                    builder.setTitle("모든 사진을 저장하시겠습니까?")
                    builder.setMessage("")

                    builder.setPositiveButton("YES"){dialog, which ->

                        storageRef.listAll()
                                .addOnSuccessListener { listResult ->
                                    listResult.items.forEach { item ->

                                        item.getBytes(2048 * 4096).addOnSuccessListener  {
                                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

                                            val savedImageURL = MediaStore.Images.Media.insertImage(
                                                    thiscontext.contentResolver,
                                                    bitmap,
                                                    item.name,
                                                    "Image of ${item.name}"
                                            )

                                            val uri = Uri.parse(savedImageURL)
                                            println("saved : $uri")


                                        }.addOnFailureListener {
                                            println("download fail - each item")
                                        }
                                    }
                                }
                                .addOnFailureListener {
                                    println("download fail")
                                }

                        Toast.makeText(thiscontext,"저장되었습니다",Toast.LENGTH_SHORT).show()

                    }

                    builder.setNeutralButton("Cancel"){_,_ ->
                        Toast.makeText(thiscontext,"취소되었습니다",Toast.LENGTH_SHORT).show()
                    }

                    val dialog: AlertDialog = builder.create()

                    dialog.show()




                }

                2 -> {

                    val builder = AlertDialog.Builder(thiscontext)
                    builder.setTitle("선택하신 사진을 저장하시겠습니까?")
                    builder.setMessage("")

                    builder.setPositiveButton("YES"){dialog, which ->
                        val item = selected_photo

                        storageRef.child(item).getBytes(2048 * 4096).addOnSuccessListener  {
                            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

                            val savedImageURL = MediaStore.Images.Media.insertImage(
                                    thiscontext.contentResolver,
                                    bitmap,
                                    item,
                                    "Image of ${item}"
                            )

                            val uri = Uri.parse(savedImageURL)
                            println("saved : $uri")


                        }.addOnFailureListener {
                            println("download fail - each item")
                        }

                        Toast.makeText(thiscontext,"저장되었습니다",Toast.LENGTH_SHORT).show()

                    }

                    builder.setNeutralButton("Cancel"){_,_ ->
                        Toast.makeText(thiscontext,"취소되었습니다",Toast.LENGTH_SHORT).show()
                    }

                    val dialog: AlertDialog = builder.create()

                    dialog.show()

                }
            }
        }

        return view
    }



    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        var w : Int = bitmap.width
        var h : Int = bitmap.height

        h = w

        return Bitmap.createScaledBitmap(
            bitmap,
            w,
            h,
            false
        )
    }

    fun getlist(){
        PhotoList.clear()
        println("getlist")
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->

                    item.getBytes(2048 * 4096).addOnSuccessListener  {

                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        var resizedbitmap = resizeBitmap(bitmap)

                        PhotoList.add(PhotoRoom_Photo(item.name ," ", bitmap, resizedbitmap))
                        adapter.notifyDataSetChanged()

                    }.addOnFailureListener {
                        println("storage-read-fail-each")

                    }
                }
            }
            .addOnFailureListener {
                println("storage-read-fail")

            }


    }

    override fun onResume() {
        super.onResume()
        getlist()
    }



    fun onclick(){
        gridview?.visibility = View.INVISIBLE
        image?.visibility = View.INVISIBLE


        when(view_change){
            1 -> gridview?.visibility = View.VISIBLE

            2 -> image?.visibility = View.VISIBLE
        }
    }


    inner class PhotoRoom_Adapter (photoList: MutableList<PhotoRoom_Photo>): BaseAdapter() {

        private var photoList : MutableList<PhotoRoom_Photo>? = photoList
        //var context: Context = context



        override fun getCount(): Int {
            return photoList!!.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return photoList!![position]
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val photo = this.photoList!![position]
            val view = LayoutInflater.from(parent?.context).inflate(R.layout.activity_photoroom_grid, null)
            //val photoview : ImageView = view.findViewById(R.layout.PhotoRoom_Photo)

            view.PhotoRoom_Photo.setImageBitmap(photo.resizedImage)
            view.PhotoRoom_Photo.setOnClickListener{
                view_change = 2
                image?.setImageBitmap(photo.image)
                selected_photo = photo.who
                onclick()
            }

            return view
        }

    }



}