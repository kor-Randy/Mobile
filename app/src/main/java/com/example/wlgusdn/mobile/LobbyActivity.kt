package com.example.wlgusdn.mobile

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import net.daum.mf.map.api.MapView
import java.lang.Exception
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.HashMap

class LobbyActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener
{
    lateinit var navbtn : Button
    private lateinit var adapter : ViewPagerAdapter
    internal val tabIcons = intArrayOf(R.drawable.calendar, R.drawable.cat)
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var goPromiseRoom : Button? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)


        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        navbtn = findViewById(R.id.Main_Button_Navi)

        navbtn.setOnClickListener {
            val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
            if(!drawer.isDrawerOpen(GravityCompat.START)){drawer.openDrawer(GravityCompat.START)}
            else drawer.closeDrawer(GravityCompat.END)
        }

        AccountActivity.Accountac!!.finish()

        viewPager = findViewById(R.id.viewpager)
        setupViewPager(viewPager)
        tabLayout = findViewById(R.id.tabs)
        tabLayout!!.setupWithViewPager(viewPager)
        setupTabIcons()



    }
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        val eid = p0.itemId

        when(eid)
        {
            R.id.Nav_FriendList ->
            {

            }
            R.id.Nav_Recommend ->
            {
                var params = TextTemplate
                        .newBuilder("약속방 만들었다", LinkObject.newBuilder().setAndroidExecutionParams("https://play.google.com/store/apps/details?id=com.test.moon.bblind").build()).build()

                var serverCallbackArgs  = HashMap<String, String>();
                var aa : Map<Any,Any> = HashMap<Any,Any>()

                //serverCallbackArgs.put("user_id", MainActivity.Myuid!!);

                var aaa  = object : ResponseCallback<KakaoLinkResponse>(){
                    override fun onSuccess(result: KakaoLinkResponse?) {

                        Log.d("ststst","성공")

                    }

                    override fun onFailure(errorResult: ErrorResult?) {

                    }

                }

                KakaoLinkService.getInstance().sendDefault(LoginActivity.logincontext, params, serverCallbackArgs,aaa)

            }
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true

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
        adapter.addFrag(MainActivity(this), "MAIN")
        adapter.addFrag(CreatePromiseFragment(this), "Create")

        //adapter.addFrag(heart(), "HEART")
        viewPager!!.adapter = adapter
        viewPager.offscreenPageLimit= 7         // 한번에 5개의 ViewPager를 띄우겠다 -> 성능향상
    }    //ADAPT FRAGMENT


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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0)
        {

            if(data!!.getIntExtra("res",0)==2)
            {
                Log.d("checkkk","재구성")
                val ft = fragmentManager!!.beginTransaction()
                if (Build.VERSION.SDK_INT >= 26) {
                    ft.setReorderingAllowed(false);
                }
                ft.detach(CreatFrag as android.app.Fragment).attach(CreatFrag as android.app.Fragment).commit();
            }

        }
    }

    companion object
    {
        var CreatFrag : Fragment?=null
        var Createcon : ConstraintLayout?=null
        var Promisecon : ConstraintLayout?=null
        var CreateMap : MapView? = null
        var PromiseMap : MapView? = null
        var refresh : Boolean = false

        var mylati : String?=null
        var mylong : String?=null
    }
}