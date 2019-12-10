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

        //Promisebtn = view.findViewById(R.id.Main_Button_Promise)
        //friendbtn = view.findViewById(R.id.Main_Friend)
        //et = view.findViewById(R.id.Main_et)
        compactCalendarView = view.findViewById(R.id.calendarView)
        showDate = view.findViewById(R.id.today)


        //showDate.text = finalDate
        val calendar = Calendar.getInstance()
        //val currentCalendar = Calendar.getInstance().timeInMillis
        val Year = calendar.get(Calendar.YEAR)
        val Month = calendar.get(Calendar.MONTH) + 1
        val Day = calendar.get(Calendar.DAY_OF_MONTH)

        //val date = calendarview.getDate()


        //val finalDate = "${Year} 년 ${Month}월 ${Day}일"
        // var millis = currentCalendar*1000
        //var mil = compactCalendarView.setCurrentDate()

        //var ev = Event(Color.RED,1576116457000,"promise")
        //compactCalendarView.addEvent(ev)

        val list : ListView= view.findViewById(R.id.Main_listview)
        val adapter  = ArrayAdapter (thiscontext, android.R.layout.simple_list_item_1, promiselist_show)
        list.adapter = adapter
        list.setOnItemClickListener { parent, view, position, id ->

            LobbyActivity.Createcon!!.removeView(LobbyActivity.CreateMap!!)
            val intent : Intent = Intent(thiscontext,PromiseRoomActivity::class.java)
            intent.putExtra("selected", promiselist_show[position].roomId)



            startActivityForResult(intent,0)

        }

        /*
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
        })*/

        /*calendarview.setOnDateChangeListener(object:CalendarView.OnDateChangeListener{
            override fun onSelectedDayChange(p0: CalendarView, p1: Int, p2: Int, p3: Int) {
                var p2 = p2 + 1
                var date : String = "${p1}년 ${p2}월 ${p3}일"
                showDate.text = date
                Log.d("TAG","onSelectedDayChange:" + date)

                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.


            }
        })*/

        compactCalendarView.setListener(object: CompactCalendarView.CompactCalendarViewListener{
            //날짜 클릭할 때
            override fun onDayClick(dateClicked: Date?) {
                //TODO("not implemented") To change body of created functions use File | Settings | File Templates.
                var date = dateClicked
                val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                val stringdate = sdf.format(dateClicked)
                //val parse = sdf.parse(stringdate)
                //Log.d("Tag",parse.toString())
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
                //TODO("not implemented") To change body of created functions use File | Settings | File Templates.
                var thismonth = compactCalendarView.firstDayOfCurrentMonth.toString()
                val result = thismonth.subSequence(30,34).toString() + " " +thismonth.subSequence(4,7).toString()
                Toast.makeText(context, result,Toast.LENGTH_SHORT).show()
                //Log.d("TAG",firstDayOfNewMonth.toString())
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

                    //val todate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE)


                    promise_db.child(promise)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                }
                                override fun onDataChange(p0: DataSnapshot) {


                                    roomname = p0.child("name").value.toString()
                                    time = p0.child("time").value.toString()
                                    date = p0.child("date").value.toString()
                                    //println(date)

                                    /*val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd", Locale.KOREA)
                                    val todate = LocalDate.parse(date,formatter)

                                    val epoch_ = todate.toEpochDay()
                                    println(epoch_)*/

                                    //string 형식으로 받아 온 date를 Date 형식으로 변환 후 millisecond 값 뽑기
                                    val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
                                    //val stringdate = sdf.format(date)
                                    val todate_date = sdf.parse(date)
                                    val epoch = todate_date.time
                                    println(epoch)

                                    promiselist.add(MainActivity_listData(roomname, date, time, promise))

                                    if("${Year}.${Month}.${Day}" == date){
                                        promiselist_show.add(MainActivity_listData(roomname, date, time, promise))
                                        println("date correct")
                                    }


                                    //약속 생성한 날짜에 맞춰 달력에 점 찍기
                                    val ev = Event(Color.BLUE,epoch,"promise")
                                    compactCalendarView.addEvent(ev)

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
