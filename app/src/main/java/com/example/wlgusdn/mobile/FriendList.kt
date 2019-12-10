package com.example.wlgusdn.mobile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.ListView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.graphics.drawable.PaintDrawable
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_promiselist.*


class FriendList : Activity()
{
    lateinit var lv : RecyclerView
    lateinit var bu : Button
    //var arr = ArrayList<FriendData>()
    var array : MutableList<FriendData> = arrayListOf()

    var checkedarr = ArrayList<FriendData>()
    var arrname = ArrayList<String>()
    var arrid  = ArrayList<String>()

    val database = FirebaseDatabase.getInstance().getReference()
    var adapter : FriendList_Adapter?= null

    val storage = FirebaseStorage.getInstance()
    var storageRef = storage.getReferenceFromUrl("gs://mobilesw-8dd3b.appspot.com").child("Account/")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_friendlist)

        lv = findViewById(R.id.friendlist_lv)
        bu = findViewById(R.id.friendlist_bu)

        val adapter = FriendList_Adapter(this,array)

        adapter.itemClick = object: FriendList_Adapter.ItemClick {
            override fun onClick(view: View, position: Int) {

                view.setBackgroundColor(Color.BLUE)
                checkedarr.add(FriendData(array[position].Name,null,array[position].Id))
                println("arr size ${checkedarr.size}")


            }
        }


        lv.adapter = adapter

        lv.layoutManager = LinearLayoutManager(this)
        lv.setHasFixedSize(true)


        database.child("Account").child(LoginActivity.auth!!.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(p0: DataSnapshot) {

                val me : UserData =p0.getValue(UserData::class.java) as UserData


                //사진 받아오기


                for(i in 1..me.Freinds!!.size-1)
                {
                    var bitmap : Bitmap ?= null
                    storageRef.child(me.Freinds!![i].Id!!).listAll()
                            .addOnSuccessListener { listResult ->
                                listResult.items.forEach { item ->

                                    item.getBytes(2048 * 4096).addOnSuccessListener  {

                                        bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                                        bitmap = resizeBitmap(bitmap!!)
                                        array.add(FriendData(me.Freinds!![i].Name, bitmap, me.Freinds!![i].Id))
                                        adapter.notifyDataSetChanged()
                                        println("size ${array.size}")
                                        //var resize = resizeBitmap(bitmap)
                                        println("image selected")

                                    }.addOnFailureListener {
                                        println("no account image")
                                    }
                                }
                            }
                            .addOnFailureListener {
                                println("no account image")
                            }

                }


            }
        })


        bu.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val intent = Intent()
                Log.d("ccheckk",checkedarr.size.toString())
                intent.putParcelableArrayListExtra("arr",checkedarr)
                setResult(RESULT_OK,intent)
                finish()

            }
        })

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



}