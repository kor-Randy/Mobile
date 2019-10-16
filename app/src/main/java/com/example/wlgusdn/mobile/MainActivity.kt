package com.example.wlgusdn.mobile

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.app.AlertDialog;
import android.os.Bundle
import android.util.Base64
import android.util.Log
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapReverseGeoCoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.jar.Manifest
import android.location.LocationManager
import android.R.string.cancel
import android.content.DialogInterface
import android.content.Intent
import android.widget.Toast
import net.daum.mf.map.n.api.internal.NativeMapLocationManager.setCurrentLocationTrackingMode
import android.Manifest.permission
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapView


class MainActivity : AppCompatActivity() {

    var bu : Button? = null

    private lateinit var navbtn : Button
    private lateinit var createbtn : Button
    private lateinit var promiseroombtn : Button

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navbtn = findViewById(R.id.Main_Button_Navi)
        createbtn = findViewById(R.id.Main_Button_CreatePromise)
        promiseroombtn = findViewById(R.id.Main_Button_PromiseRoom)

        navbtn.setOnClickListener {
            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            if(!drawer.isDrawerOpen(GravityCompat.START)){drawer.openDrawer(GravityCompat.START)}
            else drawer.closeDrawer(GravityCompat.END)
        }

        promiseroombtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val intent : Intent = Intent(this@MainActivity,ChatRoom::class.java)

                startActivity(intent)

            }
        })

        createbtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val intent : Intent = Intent(this@MainActivity,CreatePromiseActivity::class.java)

                startActivity(intent)

            }
        })

        /*
        val button_chat : Button = findViewById(R.id.ChatRoom)


        button_chat.setOnClickListener {
            val nextIntent = Intent(this, ChatRoom::class.java)
            startActivity(nextIntent)
        }
*/

    }

}
