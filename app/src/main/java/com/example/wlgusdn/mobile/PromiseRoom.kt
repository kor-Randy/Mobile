package com.example.wlgusdn.mobile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class PromiseRoom : AppCompatActivity()
{
    private lateinit var adapter : ViewPagerAdapter
    internal val tabIcons = intArrayOf(R.drawable.dog,R.drawable.cat)
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var goPromiseRoom : Button? = null
    var intentt : Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promiseroom)

        intentt = intent
        viewPager = findViewById(R.id.viewpager1)
        setupViewPager(viewPager)
        tabLayout = findViewById(R.id.tabs1)
        tabLayout!!.setupWithViewPager(viewPager)
        setupTabIcons()


    }
    private fun setupTabIcons() {
        for(i in 0..1) {
            val view1 = layoutInflater.inflate(R.layout.customtab, null) as View
            view1.findViewById<ImageView>(R.id.icon).setBackgroundResource(tabIcons[i])
            tabLayout!!.getTabAt(i)!!.setCustomView(view1)
        }

    }

    private fun setupViewPager(viewPager: ViewPager?) {
        adapter = ViewPagerAdapter(supportFragmentManager)
        adapter.addFrag(PromiseRoomActivity(this), "Chat")
        adapter.addFrag(ChatRoom(this), "Chat")

        //adapter.addFrag(heart(), "HEART")
        viewPager!!.adapter = adapter
        viewPager.offscreenPageLimit= 7         // 한번에 5개의 ViewPager를 띄우겠다 -> 성능향상
    }    //ADAPT FRAGMENT

    override fun onBackPressed() {
        super.onBackPressed()
        LobbyActivity.Promisecon!!.removeView(LobbyActivity.PromiseMap)
        Log.d("checkkk",LobbyActivity.Createcon.toString() +"/"+LobbyActivity.CreateMap.toString())
        LobbyActivity.Createcon!!.addView(LobbyActivity.CreateMap)
        LobbyActivity.refresh=true
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
}