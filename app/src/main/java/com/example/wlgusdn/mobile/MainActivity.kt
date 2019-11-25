package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import kotlinx.android.synthetic.main.activity_promiselist.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.Exception
import java.util.EnumSet.range
import java.util.HashMap


@SuppressLint("ValidFragment")
class MainActivity(context : Context) : Fragment(){

    val thiscontext : Context = context
    var bu : Button? = null
    val database = FirebaseDatabase.getInstance().getReference()
    val promise_db = database.child("PromiseRoom")

    val userid : String = LoginActivity.auth!!.currentUser!!.uid
    val username : String = AccountActivity.myname!!
    var promiselist : MutableList<MainActivity_listData> = arrayListOf()

    private lateinit var Promisebtn : Button
    private lateinit var promiseroombtn : Button
    private lateinit var promiselistbtn : Button
    private lateinit var friendbtn : Button
    private lateinit var calendarview:CalendarView
    private lateinit var showDate:TextView
    private lateinit var et : EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater!!.inflate(R.layout.fragment_main, container, false) as View








        Promisebtn = view.findViewById(R.id.Main_Button_Promise)
        friendbtn = view.findViewById(R.id.Main_Friend)
        et = view.findViewById(R.id.Main_et)
        calendarview = view.findViewById(R.id.calendarView2)
        showDate = view.findViewById(R.id.today)





        //promiselist.add(MainActivity_listData("roomname", "18:00"))


        //val adapter = MainActivity_Adapter(promiselist)
        //Main_listview.adapter = adapter

        //Main_listview.layoutManager = LinearLayoutManager(context)
        //Main_listview.setHasFixedSize(true)


        val list : ListView= view.findViewById(R.id.Main_listview)
        val adapter  = ArrayAdapter (thiscontext, android.R.layout.simple_list_item_1, promiselist)
        list.adapter = adapter
        list.setOnItemClickListener { parent, view, position, id ->

            LobbyActivity.Createcon!!.removeView(LobbyActivity.CreateMap!!)
            val intent : Intent = Intent(thiscontext,PromiseRoom::class.java)
            intent.putExtra("selected", promiselist[position].roomId)



            startActivityForResult(intent,0)

        }




        friendbtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                //친구추가 팝업
                val intent : Intent = Intent(thiscontext,FriendPopup::class.java)
                startActivity(intent)


            }
        })


        Promisebtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                LobbyActivity.Createcon!!.removeView(LobbyActivity.CreateMap!!)
                val intent : Intent = Intent(thiscontext,PromiseRoom::class.java)
                intent.putExtra("select",1)
                startActivityForResult(intent,0)

            }
        })

        calendarview.setOnDateChangeListener(object:CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
                var p2 = p2 + 1
                var date : String = "${p1}년 ${p2}월 ${p3}일"
                showDate.text = date
                Log.d("TAG","onSelectedDayChange:" + date)

                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })



        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (postSnapshot in dataSnapshot.children){
                    println("userid ${userid} username ${username}")
                    var promise = postSnapshot.value.toString()

                    if(promise == "약속리스트 초기화"){

                    }
                    else{

                        var roomname = ""
                        var time : String = ""
                        promise_db.child(promise)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onCancelled(p0: DatabaseError) {
                                    }
                                    override fun onDataChange(p0: DataSnapshot) {
                                        roomname = p0.child("name").value.toString()
                                        time = p0.child("time").value.toString()
                                        promiselist.add(MainActivity_listData(roomname, time, promise))

                                        println("added ${time} ${roomname}")
                                        adapter.notifyDataSetChanged()
                                    }
                                })


                    }


                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }



        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {


                println("datasnap ${dataSnapshot}")

                var promise = dataSnapshot.value.toString()

                if(promise == "약속리스트 초기화"){

                }
                else{

                    var roomname = ""
                    var time : String = ""
                    promise_db.child(promise)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(p0: DataSnapshot) {
                                    roomname = p0.child("name").value.toString()
                                    time = p0.child("time").value.toString()
                                    promiselist.add(MainActivity_listData(roomname, time, promise))

                                    println("added ${time} ${roomname}")
                                    adapter.notifyDataSetChanged()
                                }
                            })


                }



            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {


            }
            override fun onChildRemoved(dataSnapshot: DataSnapshot) {

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {}
        }

        //database.child("PromiseRoom").child(roomnumber).child("chatroom").addChildEventListener(childEventListener)




        //database.child("Account").child(userid).child("promises").addValueEventListener(postListener)
        database.child("Account").child(userid).child("promises").addChildEventListener(childEventListener)








        return view

    }










}
