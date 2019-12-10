package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.FirebaseDatabase
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import android.location.LocationManager
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.wlgusdn.mobile.PromiseRoom.Companion.roomId
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class service : Service(),LocationListener
{
    var isGPSEnable = false

    var isNetWorkEnable = false

    var isGetLocation = false

    var location : Location?=null
    var pd : PromiseRoomData?=null
    var fd : FriendData?=null
    override fun onLocationChanged(location: Location?) {



    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

     }

    override fun onProviderEnabled(provider: String?) {

     }

    override fun onProviderDisabled(provider: String?) {
    }



    @SuppressLint("MissingPermission")
    fun lo(): Location {

            var locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


            var isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

            var isNetWorkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)



            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0f, this)

            location=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        return location!!
    }

    var mp : MapPoint?=null



    var i=0
    val database = FirebaseDatabase.getInstance().getReference()

    override fun onBind(intent: Intent?): IBinder? {

        return null

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)



    }
    override fun onCreate() {
        super.onCreate()
        lo()

        startForeground()
    }


    fun startForeground()
    {
        sem=0
        Log.d("wlgusdn1","Start")
        val notificationIntent = Intent(this, PromiseRoom::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val remoteViews = RemoteViews(packageName, R.layout.notification_service)

        val builder: NotificationCompat.Builder
        if (Build.VERSION.SDK_INT >= 26) {
            Log.d("wlgusdn111","123")
            val CHANNEL_ID = "snwodeer_service_channel"
            val channel = NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT)

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(channel)

            builder = NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            Log.d("wlgusdn111","456")
            builder = NotificationCompat.Builder(this)
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContent(remoteViews)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)

        startForeground(1, builder.build())

        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            sercon=this@service
            notify(1, builder.build())
        }

            database.child("service").setValue(i)
            i += 1

         Thread(Runnable {

            run {
                while (true) {

                    lo()
                    LobbyActivity.mylati = location!!.latitude.toString()
                    LobbyActivity.mylong = location!!.longitude.toString()




                     database.child("PromiseRoom").child(roomId!!).child("Location").child(AccountActivity.myname!!)
                                      .setValue(NowLocationData(location!!.longitude,location!!.latitude))







                    Thread.sleep(2000)

                    if(sem==1)
                        break;
                }
            }
        }).start()



        }

    override fun onDestroy() {
        super.onDestroy()
        sem=1
        Log.d("wlgusdn1","des")
        with(NotificationManagerCompat.from(service.sercon!!)) {
            // notificationId is a unique int for each notification that you must define
            cancel(1)
        }

    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        with(NotificationManagerCompat.from(service.sercon!!)) {
            // notificationId is a unique int for each notification that you must define
            cancel(1)
        }

        stopSelf()
    }
    companion object
    {
        var sercon : Context?=null
        var  sem : Int?=null
    }
    }


