package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_promiseroom.view.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import net.daum.mf.map.api.MapView

class PromiseRoomActivity : AppCompatActivity(), MapView.POIItemEventListener, MapView.MapViewEventListener, MapView.CurrentLocationEventListener

{
    val GPS_ENABLE_REQUEST_CODE : Int = 2001
    val PERMISSIONS_REQUEST_CODE : Int = 100
    val REQUIRED_PERMISSIONS : Array<String> = arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
    val LOG_TAG : String = "MainActivity"
    var mMapView : MapView? = null
    var clsPoint : ArrayList<MapPoint>? = ArrayList<MapPoint>()
    var myPoint : MapPoint? = null


    var mButtonSearch : Button? = null
    var mEditTextQuery : EditText? = null
    val mGeocoder : Geocoder = Geocoder(this)
    var Text_Participant : TextView? = null
    var Text_Place : TextView? = null
    var Text_Time : TextView? = null
    var Text_Content : TextView? = null
    //var Bu_ChatRoom : Button? = null
    val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    var poi1 : MapPOIItem = MapPOIItem()
    var poi2 : MapPOIItem = MapPOIItem()


    val ref : DatabaseReference = database.reference


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promiseroom)

        mMapView = findViewById(R.id.PromiseRoom_Map)


        Text_Content = findViewById(R.id.PromiseRoom_TextView_Content)
        Text_Participant = findViewById(R.id.PromiseRoom_TextView_Participant)
        Text_Place = findViewById(R.id.PromiseRoom_TextView_Place)
        Text_Time = findViewById(R.id.PromiseRoom_TextView_Time)
        //Bu_ChatRoom = findViewById(R.id.PromiseRoom_Button_ChatRoom)
        val Bu_ChatRoom : Button = findViewById(R.id.PromiseRoom_Button_ChatRoom)


        Bu_ChatRoom.setOnClickListener {
            val nextIntent = Intent(this, ChatRoom::class.java)
            startActivity(nextIntent)
        }


        //친구들 위치 넣기

        mMapView!!.setMapViewEventListener(this)
        mMapView!!.setPOIItemEventListener(this)





        //길찾기 Intent사용
        //daummaps://route?sp=37.537229,127.005515&ep=37.4979502,127.0276368&by=PUBLICTRANSIT
        //

        /* val url : String = "daummaps://storeview?id=659";
                            //  "daummaps://route?sp="+37.537229+","+127.005515+"&ep="+37.4979502+","+127.0276368+"&by=PUBLICTRANSIT"
         val intent:Intent = Intent(Intent.ACTION_VIEW, Uri.parse(url));
         startActivity(intent);*/


        mMapView!!.setCurrentLocationEventListener(this)

        if(!checkLocationServicesStatus())
        {
            showDialogForLocationServiceSetting()
        }
        else
        {
            checkRunTimePermission()
        }


        /* try {
             var info:PackageInfo  = getPackageManager().getPackageInfo("com.example.wlgusdn.mobile", PackageManager.GET_SIGNATURES);
             for ( signature in info.signatures) {
                 var md:MessageDigest  = MessageDigest.getInstance("SHA");
                 md.update(signature.toByteArray());
                 Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
             }
         } catch (e: PackageManager.NameNotFoundException) {
             e.printStackTrace();
         } catch (e: NoSuchAlgorithmException) {
             e.printStackTrace();
         }*/



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

        Toast.makeText(this@PromiseRoomActivity,p1!!.itemName, Toast.LENGTH_LONG).show()


        val intent : Intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01011111111"))
        startActivity(intent)

    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        mMapView!!.setShowCurrentLocationMarker(false);
    }
    override fun onCurrentLocationUpdate(p0: MapView?, p1: MapPoint?, p2: Float) {



        val mapPointGeo = p1!!.getMapPointGeoCoord()
        Log.i(
            LOG_TAG,
            String.format(
                "MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)",
                mapPointGeo.latitude,
                mapPointGeo.longitude,
                p2
            )
        )

        clsPoint!!.add( MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude-0.001,mapPointGeo.longitude-0.001))
        clsPoint!!.add( MapPoint.mapPointWithGeoCoord(mapPointGeo.latitude+0.001,mapPointGeo.longitude+0.001))



        for(i in 0..clsPoint!!.size-1)
        {




            if(i==0)
            {
                mMapView!!.removePOIItem(poi1)
                poi1.mapPoint= MapPoint.mapPointWithGeoCoord(clsPoint!![i].mapPointGeoCoord.latitude,clsPoint!![i].mapPointGeoCoord.longitude)
                poi1.markerType = net.daum.mf.map.api.MapPOIItem.MarkerType.CustomImage
                poi1.isShowCalloutBalloonOnTouch=true
                poi1.itemName = "친구1"
                poi1.customImageResourceId = R.drawable.cat
                poi1.leftSideButtonResourceIdOnCalloutBalloon = R.drawable.cat

                mMapView!!.addPOIItem(poi1)
            }
            else
            {
                mMapView!!.removePOIItem(poi2)
                poi2.mapPoint= MapPoint.mapPointWithGeoCoord(clsPoint!![i].mapPointGeoCoord.latitude,clsPoint!![i].mapPointGeoCoord.longitude)
                poi2.markerType = net.daum.mf.map.api.MapPOIItem.MarkerType.CustomImage
                poi2.isShowCalloutBalloonOnTouch=true
                poi2.itemName = "친구2"
                poi2.customImageResourceId = R.drawable.dog
                poi2.leftSideButtonResourceIdOnCalloutBalloon = R.drawable.dog

                mMapView!!.addPOIItem(poi2)
            }



        }


    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {

        //MapView 위를 클릭시 POI 깔기
        //현재 무한대로 늘어남
        //POI 클릭시 말풍선 나오고 말풍선 클릭시 다이얼로 intent 이동
        Log.d("ccheck",p1!!.mapPointGeoCoord.latitude.toString()+","+p1!!.mapPointGeoCoord.longitude.toString())

        var poi : MapPOIItem = MapPOIItem()

        poi.itemName = "click위치"
        poi.mapPoint= MapPoint.mapPointWithGeoCoord(p1!!.mapPointGeoCoord.latitude,p1!!.mapPointGeoCoord.longitude)
        poi.markerType = net.daum.mf.map.api.MapPOIItem.MarkerType.CustomImage
        poi.isShowCalloutBalloonOnTouch=true
        poi.customImageResourceId = R.drawable.cat
        poi.leftSideButtonResourceIdOnCalloutBalloon = R.drawable.cat

        mMapView!!.addPOIItem(poi)
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

    override fun onCurrentLocationUpdateCancelled(p0: MapView?) {


    }

    override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {


    }


    override fun onCurrentLocationUpdateFailed(p0: MapView?) {


    }


    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    override fun onRequestPermissionsResult(
        permsRequestCode: Int,
        permissions: Array<String>,
        grandResults: IntArray
    ) {




        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            var check_result = true


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }


            if (check_result) {
                Log.d("@@@", "start")
                //위치 값을 가져올 수 있음
                mMapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading)
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                ) {

                    Toast.makeText(
                        this@PromiseRoomActivity,
                        "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()


                } else {

                    Toast.makeText(
                        this@PromiseRoomActivity,
                        "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

        }
    }

    fun checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this@PromiseRoomActivity,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음
            mMapView!!.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading)


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@PromiseRoomActivity,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(this@PromiseRoomActivity, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG)
                    .show()
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@PromiseRoomActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(
                    this@PromiseRoomActivity, REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                )
            }

        }

    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private fun showDialogForLocationServiceSetting() {

        val builder = AlertDialog.Builder(this@PromiseRoomActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하실래요?")
        builder.setCancelable(true)
        builder.setPositiveButton("설정", DialogInterface.OnClickListener { dialog, id ->
            val callGPSSettingIntent =
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
        })
        builder.setNegativeButton("취소",
            DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })
        builder.create().show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            GPS_ENABLE_REQUEST_CODE ->

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음")
                        checkRunTimePermission()
                        return
                    }
                }
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }




}