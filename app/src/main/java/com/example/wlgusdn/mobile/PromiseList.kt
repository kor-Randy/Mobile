package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_promiselist.*


class PromiseList() : AppCompatActivity() {

    val database = FirebaseDatabase.getInstance().getReference()

    val part : ArrayList<FriendData> =  ArrayList<FriendData>()
    var Promiselist : MutableList<PromiseRoomData> = arrayListOf()



    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promiselist)

        val adapter = PromiseList_Adapter(this, Promiselist)
        Promiselist_Recyclerview.adapter = adapter

        Promiselist_Recyclerview.layoutManager = LinearLayoutManager(this)
        Promiselist_Recyclerview.setHasFixedSize(true)


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children){
                    var data = PromiseRoomData("","", "","","","",  part,0.0,0.0)
                    var roomName = postSnapshot.key.toString()
                    data.Date = dataSnapshot.child(roomName).child("date").value.toString()
                    data.Time = dataSnapshot.child(roomName).child("time").value.toString()
                    data.Address = dataSnapshot.child(roomName).child("address").value.toString()
                    data.EtcAddress = dataSnapshot.child(roomName).child("etcAddress").value.toString()
                    data.Long = dataSnapshot.child(roomName).child("long").value.toString().toDouble()
                    data.Lati = dataSnapshot.child(roomName).child("lati").value.toString().toDouble()
                    //data.Participants = dataSnapshot.child(roomName).child("participants").value
                    //println("${roomName}")

                    Promiselist.add(data)

                    adapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        database.child("PromiseRoom").addValueEventListener(postListener)


    }


}