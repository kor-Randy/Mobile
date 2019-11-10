package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment


@SuppressLint("ValidFragment")
class MainActivity(context : Context) : Fragment() {

    val thiscontext : Context = context
    var bu : Button? = null

    private lateinit var navbtn : Button
    private lateinit var Promisebtn : Button
    private lateinit var promiseroombtn : Button
    private lateinit var promiselistbtn : Button
    private lateinit var friendbtn : Button

    private lateinit var et : EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater!!.inflate(R.layout.fragment_main, container, false) as View

        navbtn = view.findViewById(R.id.Main_Button_Navi)
        Promisebtn = view.findViewById(R.id.Main_Button_Promise)
        friendbtn = view.findViewById(R.id.Main_Friend)
        et = view.findViewById(R.id.Main_et)

        friendbtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                //친구추가 팝업
                val intent : Intent = Intent(thiscontext,FriendPopup::class.java)
                startActivity(intent)


            }
        })

        navbtn.setOnClickListener {
            val drawer = view.findViewById(R.id.drawer_layout) as DrawerLayout
            if(!drawer.isDrawerOpen(GravityCompat.START)){drawer.openDrawer(GravityCompat.START)}
            else drawer.closeDrawer(GravityCompat.END)
        }
        Promisebtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                LobbyActivity.Createcon!!.removeView(LobbyActivity.CreateMap!!)
                val intent : Intent = Intent(thiscontext,PromiseRoom::class.java)
                intent.putExtra("select",1)
                startActivityForResult(intent,0)

            }
        })




        return view

    }


}
