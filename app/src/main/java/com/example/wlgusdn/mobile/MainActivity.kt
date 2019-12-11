package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.time.format.DateTimeFormatter
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.*
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import kotlinx.android.synthetic.main.activity_promiselist.*
import kotlinx.android.synthetic.main.fragment_createpromise.*
import kotlinx.android.synthetic.main.fragment_main.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.util.*
import java.util.EnumSet.range


@SuppressLint("ValidFragment")
class MainActivity(context : Context) : Fragment(){

    val thiscontext : Context = context
    var bu : Button? = null
    val database = FirebaseDatabase.getInstance().getReference()
    val promise_db = database.child("PromiseRoom")

    val userid : String = LoginActivity.auth!!.currentUser!!.uid
    val username : String = AccountActivity.myname!!
    var promiselist : MutableList<MainActivity_listData> = arrayListOf()
    var promiselist_show : MutableList<MainActivity_listData> = arrayListOf()


    private lateinit var Promisebtn : Button
    private lateinit var promiseroombtn : Button
    private lateinit var promiselistbtn : Button
    private lateinit var friendbtn : Button
    private lateinit var calendarview:CalendarView
    private lateinit var showDate:TextView
    private lateinit var et : EditText
    lateinit var compactCalendarView: CompactCalendarView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater!!.inflate(R.layout.fragment_main, container, false) as View


        compactCalendarView = view.findViewById(R.id.calendarView)
        showDate = view.findViewById(R.id.today)

        //오늘 날짜 받기
        val calendar = Calendar.getInstance()
        val Year = calendar.get(Calendar.YEAR)
        val Month = calendar.get(Calendar.MONTH) + 1
        val Day = calendar.get(Calendar.DAY_OF_MONTH)




        val finalDate = "${Year} 년 ${Month}월 ${Day}일"
        showDate.text = finalDate

        //선택된 날짜의 약속방만 띄우기
        val list : ListView= view.findViewById(R.id.Main_listview)
        val adapter  = ArrayAdapter (thiscontext, android.R.layout.simple_list_item_1, promiselist_show)
        list.adapter = adapter
        list.setOnItemClickListener { parent, view, position, id ->

            LobbyActivity.Createcon!!.removeView(LobbyActivity.CreateMap!!)
            val intent : Intent = Intent(thiscontext,PromiseRoomActivity::class.java)
            intent.putExtra("selected", promiselist_show[position].roomId)



            startActivityForResult(intent,0)

        }



        compactCalendarView.setListener(object: CompactCalendarView.CompactCalendarViewListener{
            //날짜 클릭할 때
            override fun onDayClick(dateClicked: Date?) {
                var date = dateClicked
                val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                val stringdate = sdf.format(dateClicked)
                var today= stringdate
                var showDate = view.findViewById<TextView>(R.id.today)
                showDate.text = today + "의 약속"
                var date_check : String = stringdate
                var promiselist_now : MutableList<MainActivity_listData> = arrayListOf()

                for (current in promiselist) {

                    if(date_check == current.date){
                        promiselist_now.add(current)
                        println("date correct")
                    }

                }

                promiselist_show.clear()
                promiselist_show.addAll(promiselist_now)

                for(i in promiselist_show){
                    println("show ${i.name}")
                }
                adapter.notifyDataSetChanged()

            }
            //달력 넘길 때
            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                var thismonth = compactCalendarView.firstDayOfCurrentMonth.toString()
                val result = thismonth.subSequence(30,34).toString() + " " +thismonth.subSequence(4,7).toString()
                Toast.makeText(context, result,Toast.LENGTH_SHORT).show()
            }

        })

        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {


                println("datasnap ${dataSnapshot}")

                var promise = dataSnapshot.value.toString()

                if(promise == "약속리스트 초기화"){

                }
                else{

                    var roomname = ""
                    var time : String = ""
                    var date : String = ""



                    promise_db.child(promise)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(p0: DataSnapshot) {


                                    roomname = p0.child("name").value.toString()
                                    time = p0.child("time").value.toString()
                                    date = p0.child("date").value.toString()

                                    val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                                    val todate_date = sdf.parse(date)
                                    val epoch = todate_date.time
                                    println(epoch)

                                    promiselist.add(MainActivity_listData(roomname, date, time, promise))

                                    if("${Year}.${Month}.${Day}" == date){
                                        promiselist_show.add(MainActivity_listData(roomname, date, time, promise))
                                        println("date correct")
                                    }
                                    val today = calendar.get(Calendar.DATE)
                                    val sdff = SimpleDateFormat("dd", Locale.KOREA)
                                    val todate = sdff.format(todate_date).toString()
                                    val intdate = todate.toInt()


                                    if(intdate-today == 1 || intdate-today == 2)
                                    {
                                        val ev = Event(Color.RED,epoch,"promise")
                                        compactCalendarView.addEvent(ev)
                                    }
                                    else{
                                        val ev = Event(Color.BLUE,epoch,"promise")
                                        compactCalendarView.addEvent(ev)
                                    }


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


        database.child("Account").child(userid).child("promises").addChildEventListener(childEventListener)

        return view

    }

}
