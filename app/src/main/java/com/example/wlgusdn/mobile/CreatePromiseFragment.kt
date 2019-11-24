package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.SupportMapFragment
import com.google.firebase.database.*
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import kotlinx.android.synthetic.main.fragment_createpromise.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView
import java.util.HashMap

@SuppressLint("ValidFragment")
class CreatePromiseFragment(context: Context) : Fragment(), MapView.POIItemEventListener, MapView.MapViewEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener {
    var thiscontext : Context = context
    val CheckFriendList = 0
    lateinit  var clsPoint : ArrayList<MapPoint>
    lateinit var bu_Date : TextView
    lateinit var bu_Time : TextView
    lateinit var bu_Invite : TextView
    lateinit var tv_Place : TextView
    lateinit var tv_Date : TextView
    lateinit var tv_Time : TextView
    lateinit var tv_Friends : TextView
    lateinit var et_ExtraAddress : EditText
    lateinit var et_Content : EditText
    lateinit var et_Name : EditText
    lateinit var bu_Create : Button
    lateinit var tv_Participant : TextView
    var arr : ArrayList<FriendData>? = ArrayList<FriendData>()


    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    val myRef : DatabaseReference = database.getReference("PromiseRoom")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater!!.inflate(R.layout.fragment_createpromise, container, false) as View

        LobbyActivity.Createcon = view.findViewById(R.id.Create_Con)
        LobbyActivity.CreateMap =view.findViewById(R.id.CreatePromise_Map)
        tv_Date = view.findViewById(R.id.CreatePromise_TextView_Date)
        tv_Friends = view.findViewById(R.id.CreatePromise_TextView_Participant)
        tv_Place = view.findViewById(R.id.CreatePromise_TextView_Place)
        tv_Time =view.findViewById(R.id.CreatePromise_TextView_Time)
        tv_Participant = view.findViewById(R.id.CreatePromise_TextView_Participant)
        et_Name = view.findViewById(R.id.CreatePromise_EditText_Name)
        et_ExtraAddress = view.findViewById(R.id.CreatePromise_EditText_ExtraAddress)
        et_Content = view.findViewById(R.id.CreatePromise_EditText_Content)

        bu_Date = view.findViewById(R.id.CreatePromise_Button_Date)
        bu_Invite = view.findViewById(R.id.CreatePromise_Button_Invite)
        bu_Time = view.findViewById(R.id.CreatePromise_Button_Time)
        bu_Create = view.findViewById(R.id.CreatePromise_Button_Create)


        /*
        val button_reco : Button = view.findViewById(R.id.CreatePromiseRoom_recommend)

        button_reco.setOnClickListener {
            val nextIntent = Intent(activity, Recommend::class.java)
            startActivity(nextIntent)
        }*/




        tv_Participant!!.hint="지현우,연송희,이유리"

        bu_Create!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                Log.d("wlgusdn111",AccountActivity.myname!!.toString())
                Log.d("wlgusdn111",LoginActivity.auth!!.toString())
                Log.d("wlgusdn111",LoginActivity.auth!!.currentUser!!.toString())
                Log.d("wlgusdn111",LoginActivity.auth!!.currentUser!!.uid.toString())

                arr!!.add(0,FriendData(AccountActivity.myname!!,null,LoginActivity.auth!!.currentUser!!.uid))



                val PRD : PromiseRoomData = PromiseRoomData(et_Name!!.text.toString(),tv_Date!!.text.toString(),tv_Time!!.text.toString(),tv_Place!!.text.toString(),
                        et_ExtraAddress!!.text.toString(),et_Content!!.text.toString(),arr!!)

               myRef.push().setValue(PRD)

                //여기서 초대한 친구들의 DB에 약속방List에 약속방 번호 추가



                        var RoomNum : String? = null
                        for(i in 0..arr!!.size-1)
                        {
                             database.reference.addListenerForSingleValueEvent(object: ValueEventListener {
                                 override fun onCancelled(p0: DatabaseError) {


                                 }

                                 override fun onDataChange(p0: DataSnapshot) {

                                              var ud =p0.child("Account").child(arr!![i].Id!!).getValue(UserData::class.java)

                                              /*   while(true)
                                                 {//Account를 찾아가던 중 내가 찾는 이름을 갖고 있지 않으면 계속해서 찾는다.
                                                     var aa = p0.child("Account").children.iterator().next()
                                                     if(aa.child("name").value.toString().equals(part[i]))
                                                     {
                                                         Log.d("ccheckk",aa.key.toString())
                                                     }

                                                 }*/

                                              Log.d("ccheckk",p0.child("PromiseRoom").children.last().key)
                                              RoomNum = p0.child("PromiseRoom").children.last().key


                                              ud!!.Promises!!.add(RoomNum!!)
                                              database.getReference("Account").child(arr!![i].Id!!).setValue(ud)
                                     for(i in 0..arr!!.size-1)
                                     {

                                         database.getReference("Account").child(arr!![i].Id!!).child("Location").child(arr!![i].Name!!).setValue(NowLocationData(0.0,0.0))

                                     }

                                          }


                             })






                        }





            }
                })



        bu_Date!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {




                val dialog = DatePickerDialog(thiscontext, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    CreatePromise_TextView_Date.setText(year.toString() + "."+(month + 1).toString() + "."+dayOfMonth.toString())
                }, 2019, 0, 1)


                dialog.show()



            }
        })

        bu_Time!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val timePickerDialog = TimePickerDialog(thiscontext, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                    tv_Time!!.text = hourOfDay.toString()+" : "+minute.toString()

                }, 18, 0, true)

                timePickerDialog.setMessage("메시지")
                timePickerDialog.show()
            }
        })

    bu_Invite.setOnClickListener(object: View.OnClickListener {
        override fun onClick(v: View?) {


                    //친구추가 팝업
                    val intent : Intent = Intent(thiscontext,FriendList::class.java)
                    startActivityForResult(intent, CheckFriendList)





        }
    })

        LobbyActivity.CreateMap!!.setMapViewEventListener(this)
        LobbyActivity.CreateMap!!.setPOIItemEventListener(this)


        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode== CheckFriendList)
        {
           arr = data!!.getParcelableArrayListExtra<FriendData>("arr")
            Log.d("ccheckk",arr.toString())
            var str : String=""
            for(i in 0..arr!!.size-1)
            {
                str += arr!![i].Name+","
            }
            tv_Participant.text = str

        }
    }




    override fun onResume() {
        super.onResume()
        Log.d("checkkk","resume")
        if(LobbyActivity.refresh==true) {

            val ft = fragmentManager!!.beginTransaction()
            if (Build.VERSION.SDK_INT >= 26) {
                ft.setReorderingAllowed(false);
            }
            ft.detach(this).attach(this).commit();//Fragment 재 실행
            LobbyActivity.refresh=false
        }

    }






    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {



    }

    override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
    ) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem) {

        val poi : MapPOIItem = p1
        val latitude = poi.mapPoint.mapPointGeoCoord.latitude
        val longitude = poi.mapPoint.mapPointGeoCoord.longitude
        val position : String = latitude.toString() + "," + longitude.toString()

        println("latitude ${latitude}, longitude ${longitude}")

        val nextIntent = Intent(activity, Recommend::class.java)
        nextIntent.putExtra("location", position)
        startActivity(nextIntent)






    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {

        //MapView 위를 클릭시 POI 깔기
        //현재 무한대로 늘어남
        //POI 클릭시 말풍선 나오고 말풍선 클릭시 다이얼로 intent 이동

        var poi : MapPOIItem = MapPOIItem()

        poi.itemName = "장소 추천"
        poi.mapPoint= MapPoint.mapPointWithGeoCoord(p1!!.mapPointGeoCoord.latitude,p1!!.mapPointGeoCoord.longitude)
        poi.markerType = MapPOIItem.MarkerType.CustomImage
        poi.isShowCalloutBalloonOnTouch=true
        poi.customImageResourceId = R.drawable.cat
        poi.leftSideButtonResourceIdOnCalloutBalloon = R.drawable.cat

        LobbyActivity.CreateMap!!.addPOIItem(poi)



        val reverseGeoCoder : MapReverseGeoCoder = MapReverseGeoCoder("4a70536a991d4cd7bd72f612b7ab81b8",poi.mapPoint,this,thiscontext as Activity)
        reverseGeoCoder.startFindingAddress()



    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewInitialized(p0: MapView?) {

    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }




    override fun onReverseGeoCoderFailedToFindAddress(p0: MapReverseGeoCoder?) {
        onFinishReverseGeoCoding("Fail")

    }

    override fun onReverseGeoCoderFoundAddress(p0: MapReverseGeoCoder?, p1: String?) {

        Log.d("check","Success to GeoCoder")
        p0.toString()
        onFinishReverseGeoCoding(p1!!)

    }

    private fun onFinishReverseGeoCoding(result: String) {
        Toast.makeText(thiscontext, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
        tv_Place!!.text = result
    }


}