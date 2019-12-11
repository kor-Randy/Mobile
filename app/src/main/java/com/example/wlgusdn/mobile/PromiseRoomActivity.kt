package com.example.wlgusdn.mobile

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import net.daum.mf.map.api.MapView
import java.lang.Exception


class PromiseRoomActivity : AppCompatActivity()
{
    private lateinit var adapter : ViewPagerAdapter
    internal val tabIcons = intArrayOf(R.mipmap.promiseroom,R.mipmap.chat,R.drawable.picture)
    private var viewPager: ViewPager? = null
    //private var viewPager2: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var goPromiseRoom : Button? = null
    var intentt : Intent? = null
    var update : ToggleButton?=null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promiseroom)

        try
        {
            roomId = intent.extras["selected"] as String
        }
        catch (e : Exception){
            println("room not selected")
        }

        //roomId = intent.extras["selected"] as String
        //println("roomId ${roomId}")


       update = findViewById(R.id.toggle)

        update!!.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                if(update!!.isChecked)
                {
                    val intent = Intent(this@PromiseRoomActivity,service::class.java)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent)

                    }
                    else
                    {
                        startService(intent)
                    }
                }
                else
                {
                    val intent = Intent(this@PromiseRoomActivity,service::class.java)

                    stopService(intent)

                    with(NotificationManagerCompat.from(service.sercon!!)) {
                        // notificationId is a unique int for each notification that you must define
                        cancel(1)
                    }
                }

            }
        })





        intentt = intent
        viewPager = findViewById(R.id.viewpager1)
        setupViewPager(viewPager)
        tabLayout = findViewById(R.id.tabs1)
        tabLayout!!.setupWithViewPager(viewPager)
        setupTabIcons()


    }
    private fun setupTabIcons() {
        for(i in 0..2) {
            val view1 = layoutInflater.inflate(R.layout.customtab, null) as View
            view1.findViewById<ImageView>(R.id.icon).setBackgroundResource(tabIcons[i])
            tabLayout!!.getTabAt(i)!!.setCustomView(view1)
        }

    }

    override fun onResume() {
        super.onResume()

        if(service.sem==0)
        {
            update!!.isChecked=true
        }
        else
        {
            update!!.isChecked=false

        }
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(PromiseRoomFragment(this), "Chat")
        adapter.addFrag(ChatRoom(this), "Chat")
        adapter.addFrag(PhotoRoom(this), "Photo")

        viewPager!!.adapter = adapter
        viewPager.offscreenPageLimit= 7         // 한번에 5개의 ViewPager를 띄우겠다 -> 성능향상


        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }
            override fun onPageSelected(position: Int) {
                println("position: ${position}")
                if(position == 2){
                    val frag = adapter.getItem(position)
                    frag.onResume()
                }


            }

        })

    }    //ADAPT FRAGMENT

    override fun onBackPressed() {
        super.onBackPressed()
        LobbyActivity.Promisecon!!.removeView(LobbyActivity.PromiseMap)
        Log.d("checkkk",LobbyActivity.Createcon.toString() +"/"+LobbyActivity.CreateMap.toString())
        LobbyActivity.Createcon!!.addView(LobbyActivity.CreateMap)
        LobbyActivity.CreateMap!!.currentLocationTrackingMode= MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeadingWithoutMapMoving
        LobbyActivity.refresh=true//Frag 재구성이 필요함을 표시
        finish()
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
        private val mFragmentList = ArrayList<Fragment>()   //MAKE FRAGMENT LIST
        private val mFragmentTitleList = ArrayList<String>()  //MAKE FRAGMENT TITLE LIST

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]       // RETURN THE ITEM OBJECT(mFragmentList)
        }

        override fun getCount(): Int {
            return mFragmentList.size       //FRAGMENT SIZE
        }

        fun addFrag(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }  //ADD FRAGMENT

        override fun getPageTitle(position: Int): CharSequence? {
            // return null to display only the icon
            return null
        }
    }

    companion object
    {
        var roomId : String?=null

    }
}