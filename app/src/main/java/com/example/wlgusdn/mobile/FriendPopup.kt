package com.example.wlgusdn.mobile

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.google.firebase.database.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.*


class FriendPopup : Activity()
{

    var et  : EditText? = null
    var bu_search : Button? = null
    var bu_add : Button? = null
    var list : ListView?=null
    var adapter : BaseAdapter?=null
    var arr : ArrayList<FriendData>?= ArrayList<FriendData>()
    var temp : ArrayList<FriendData>?= ArrayList<FriendData>()

    val database = FirebaseDatabase.getInstance().getReference()
    var adap : FriendListAdapter?= null
    var dataa : FriendData?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.popup_friend)
        list = findViewById(R.id.Friend_list)
        bu_search = findViewById(R.id.Friend_Search)
        bu_add = findViewById(R.id.Friend_Add)
        et = findViewById(R.id.Friend_et)

        bu_search!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                arr!!.clear()
                adap = FriendListAdapter(temp)
                list!!.adapter=adap
                adap!!.notifyDataSetChanged()
                setListViewHeightBasedOnChildren(list!!)
                database.child("Account").addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {


                    }

                    override fun onDataChange(p0: DataSnapshot) {

                        for(data in p0.children)
                        {
                            if(data.child("name").value.toString().equals(et!!.text.toString()))
                            {
                                val drawable = resources.getDrawable(R.drawable.cat)

                                val bitmap = (drawable as BitmapDrawable).bitmap

                                //찾은 이름의 id값을 add한다.
                                 dataa = FriendData(et!!.text.toString(),bitmap,data.key.toString())
                                arr!!.add(dataa!!)//id



                            }


                        }
                        adap = FriendListAdapter(arr!!)
                        Log.d("checkkkk",dataa!!.Name.toString())
                        list!!.adapter=adap
                        adap!!.notifyDataSetChanged()
                        setListViewHeightBasedOnChildren(list!!)

                    }
                })

            }
        })

       list!!.setOnItemClickListener(object: AdapterView.OnItemClickListener {
           override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

               val alt_bld = AlertDialog.Builder(this@FriendPopup)
               alt_bld!!.setMessage("친구 추가를 하시겠습니까?").setCancelable(true).setPositiveButton("네",
                       object: DialogInterface.OnClickListener {

                           override fun onClick(p0: DialogInterface?, p1: Int) {


                              database.child("Account")
                                      .addListenerForSingleValueEvent(object: ValueEventListener {
                                          override fun onCancelled(p0: DatabaseError) {


                                          }

                                          override fun onDataChange(p0: DataSnapshot) {

                                              val user = p0.child(arr?.get(position)!!.Id!!).getValue(UserData::class.java)
                                              var fr = user!!.Freinds
                                              val my = p0.child(LobbyActivity.auth!!.currentUser!!.uid).getValue(UserData::class.java)
                                              var myfr = my!!.Freinds

                                              fr!!.add(FriendData(my.Name.toString(),null,LobbyActivity.auth!!.currentUser!!.uid))
                                              user.Freinds=fr
                                              database.child("Account").child(arr?.get(position)!!.Id!!).setValue(user)


                                              myfr!!.add(FriendData(user.Name.toString(),null,arr?.get(position)!!.Id!!))
                                              my.Freinds=myfr
                                              database.child("Account").child(LobbyActivity.auth!!.currentUser!!.uid).setValue(my)


                                          }
                                      })

                           }
                       }).setNegativeButton("아니요", object : DialogInterface.OnClickListener{
                   override fun onClick(p0: DialogInterface?, p1: Int) {
                       p0!!.cancel()
                   }})
               alt_bld.create().show()

           }
       })





    }
    fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter
                ?: // pre-condition
                return

        var totalHeight = 0
        val desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.width, View.MeasureSpec.AT_MOST)

        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
            totalHeight += listItem.measuredHeight
        }

        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }
}