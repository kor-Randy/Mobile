package com.example.wlgusdn.mobile

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_createpromise.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

class CreatePromiseActivity : AppCompatActivity(), MapView.POIItemEventListener, MapView.MapViewEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener
{

    var mMapView : MapView? = null
    var clsPoint : ArrayList<MapPoint>? = ArrayList<MapPoint>()
    var bu_Date : Button? = null
    var bu_Time : Button? = null
    var bu_Invite : Button? = null
    var tv_Place : TextView? = null
    var tv_Date : TextView? = null
    var tv_Time : TextView? = null
    var tv_Friends : TextView? = null
    var et_ExtraAddress : EditText? = null
    var et_Content : EditText? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createpromise)

        mMapView =findViewById(R.id.CreatePromise_Map)
        tv_Date = findViewById(R.id.CreatePromise_TextView_Date)
        tv_Friends = findViewById(R.id.CreatePromise_TextView_Participant)
        tv_Place = findViewById(R.id.CreatePromise_TextView_Place)
        tv_Time = findViewById(R.id.CreatePromise_TextView_Time)

        et_ExtraAddress = findViewById(R.id.CreatePromise_EditText_ExtraAddress)
        et_Content = findViewById(R.id.CreatePromise_EditText_Content)

        bu_Date = findViewById(R.id.CreatePromise_Button_Date)
        bu_Invite = findViewById(R.id.CreatePromise_Button_Invite)
        bu_Time = findViewById(R.id.CreatePromise_Button_Time)

        bu_Date!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {




                val dialog = DatePickerDialog(this@CreatePromiseActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->

                    CreatePromise_TextView_Date.setText(year.toString() + "."+(month + 1).toString() + "."+dayOfMonth.toString())
                }, 2019, 0, 1)


                dialog.show()



            }
        })

        bu_Time!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val timePickerDialog = TimePickerDialog(this@CreatePromiseActivity, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->

                    tv_Time!!.text = hourOfDay.toString()+" : "+minute.toString()

                }, 18, 0, true)

                timePickerDialog.setMessage("메시지")
                timePickerDialog.show()
        }
        })



        mMapView!!.setMapViewEventListener(this)
        mMapView!!.setPOIItemEventListener(this)

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

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {


    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {

        //MapView 위를 클릭시 POI 깔기
        //현재 무한대로 늘어남
        //POI 클릭시 말풍선 나오고 말풍선 클릭시 다이얼로 intent 이동

        var poi : MapPOIItem = MapPOIItem()

        poi.itemName = "click위치"
        poi.mapPoint= MapPoint.mapPointWithGeoCoord(p1!!.mapPointGeoCoord.latitude,p1!!.mapPointGeoCoord.longitude)
        poi.markerType = net.daum.mf.map.api.MapPOIItem.MarkerType.CustomImage
        poi.isShowCalloutBalloonOnTouch=true
        poi.customImageResourceId = R.drawable.cat
        poi.leftSideButtonResourceIdOnCalloutBalloon = R.drawable.cat

        mMapView!!.addPOIItem(poi)

        val reverseGeoCoder : MapReverseGeoCoder = MapReverseGeoCoder("4a70536a991d4cd7bd72f612b7ab81b8",poi.mapPoint,this,this)
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
        Toast.makeText(this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
        tv_Place!!.text = result
    }


}