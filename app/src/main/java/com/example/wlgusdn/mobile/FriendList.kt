package com.example.wlgusdn.mobile

import android.app.Activity
import android.content.Intent
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
import android.util.Log
import android.widget.Button


class FriendList : Activity()
{
    lateinit var lv : ListView
    lateinit var bu : Button
    var arr = ArrayList<FriendData>()

    var checkedarr = ArrayList<FriendData>()
    var arrname = ArrayList<String>()
    var arrid  = ArrayList<String>()

    val database = FirebaseDatabase.getInstance().getReference()
    var adap : FriendListAdapter?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_friendlist)

        lv = findViewById(R.id.friendlist_lv)
        bu = findViewById(R.id.friendlist_bu)
        val drawable = resources.getDrawable(R.drawable.cat)

        val bitmap = (drawable as BitmapDrawable).bitmap
        database.child("Account").child(LobbyActivity.auth!!.currentUser!!.uid).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {


            }

            override fun onDataChange(p0: DataSnapshot) {

                val me : UserData =p0.getValue(UserData::class.java) as UserData


                //사진 받아오기


                for(i in 1..me.Freinds!!.size-1)
                {
                    arr!!.add(FriendData(me.Freinds!![i].Name,bitmap, me.Freinds!![i].Id))

                }

                adap = FriendListAdapter(arr)
                lv.adapter=adap
                adap!!.notifyDataSetChanged()

            }
        })


        lv.setOnItemClickListener(object: AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                lv.setSelector(PaintDrawable(-0x10000))

                checkedarr.add(FriendData(arr!![position].Name,null,arr!![position].Id))
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



}