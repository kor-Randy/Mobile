package com.example.wlgusdn.mobile

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import com.google.firebase.database.*

class FriendPopup : Activity()
{

    var et  : EditText? = null
    var bu : Button? = null
    var list : ListView?=null
    var adapter : BaseAdapter?=null
    var arr : ArrayList<String>?= ArrayList<String>()
    val database = FirebaseDatabase.getInstance().getReference()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_friend)
        list = findViewById(R.id.Friend_list)
        bu = findViewById(R.id.Friend_bu)
        et = findViewById(R.id.Friend_et)

        bu!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                database.child("Account").addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {


                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for(data in p0.children)
                        {
                            if(data.child("name").value.toString().equals(et!!.text.toString()))
                            {
                                //찾은 이름의 id값을 add한다.
                                arr!!.add(data.key.toString())//id
                                Log.d("ccheckk",data.key.toString())

                            }

                        }

                    }
                })

            }
        })

    }

}