package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import java.util.HashMap


@SuppressLint("ValidFragment")
class MainActivity(context : Context) : Fragment(){

    val thiscontext : Context = context
    var bu : Button? = null

    private lateinit var Promisebtn : Button
    private lateinit var promiseroombtn : Button
    private lateinit var promiselistbtn : Button
    private lateinit var friendbtn : Button

    private lateinit var et : EditText
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view = inflater!!.inflate(R.layout.fragment_main, container, false) as View








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
