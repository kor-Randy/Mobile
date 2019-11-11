package com.example.wlgusdn.mobile



import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.viewpager.widget.ViewPager
import com.example.wlgusdn.mobile.R.id.ChatRoom_RecyclerView
import com.facebook.FacebookSdk.getApplicationContext
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_photoroom_grid.view.*
import kotlinx.android.synthetic.main.fragment_chatroom.*
import java.io.File


@SuppressLint("ValidFragment")
class PhotoRoom(context : Context) : Fragment(){
    var thiscontext = context
    val PhotoList: MutableList<PhotoRoom_Photo> = arrayListOf()
    //val photo by lazy {intent.extras[]}
    val database = FirebaseDatabase.getInstance().getReference()
    val storage = FirebaseStorage.getInstance()
    var gridview: GridView ?= null
    var imageview : ViewPager?= null
    var image: ImageView ?= null
    var view_change : Int = 1
    //val download : Button ?= null
    var storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Photoroom/")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_photoroom, container, false) as View


        gridview = view.findViewById(R.id.PhotoRoom_Gridview)
        //imageview = findViewById(R.id.PhotoRoom_ImagePage)
        image = view.findViewById(R.id.PhotoRoom_ImageView)
        val download : Button= view.findViewById(R.id.PhotoRoom_download)

        image?.visibility = View.INVISIBLE
        //imageview?.visibility = View.INVISIBLE

        val adapter = PhotoRoom_Adapter(PhotoList)
        gridview?.adapter = adapter
        adapter.notifyDataSetChanged()


        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.items.forEach { item ->
                    // All the items under listRef.
                    println("item ${item.name}")

                    item.getBytes(1024 * 1024).addOnSuccessListener  {
                        // Got the download URL for 'users/me/profile.png'
                        //Toast.makeText(this, "download : ${it}", Toast.LENGTH_LONG).show()
                        val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        var resizedbitmap = resizeBitmap(bitmap)
                        PhotoList.add(PhotoRoom_Photo(" "," ", bitmap, resizedbitmap))
                        adapter.notifyDataSetChanged()

                    }.addOnFailureListener {
                        Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(getApplicationContext(), "read-fail", Toast.LENGTH_SHORT).show()
            }

        image?.setOnClickListener {
            view_change = 1
            onclick()
        }

        download.setOnClickListener {
            when(view_change){
                1 -> {
                    storageRef.listAll()
                        .addOnSuccessListener { listResult ->
                            listResult.items.forEach { item ->
                                // All the items under listRef.
                                println("item ${item.name}")
                                val localFile: File = File.createTempFile(item.name,"jpg")
                                item.getFile(localFile).addOnSuccessListener  {
                                    Toast.makeText(thiscontext, "downloaded", Toast.LENGTH_LONG).show()

                                }.addOnFailureListener {
                                    Toast.makeText(getApplicationContext(), "fail", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(getApplicationContext(), "read-fail", Toast.LENGTH_SHORT).show()
                        }

                }
                2 -> {}
            }
        }

        return view
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        var w : Int = bitmap.width
        var h : Int = bitmap.height
        println("w: ${w}, h: ${h}")

        h = w

        println("w: ${w}, h: ${h}")
        return Bitmap.createScaledBitmap(
            bitmap,
            w,
            h,
            false
        )
    }


    fun onclick(){
        gridview?.visibility = View.INVISIBLE
        image?.visibility = View.INVISIBLE
        //imageview?.visibility = View.INVISIBLE

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
                onclick()
            }

            return view
        }

    }





}