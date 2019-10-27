package com.example.wlgusdn.mobile

import android.os.Bundle
import android.content.Intent
import android.annotation.SuppressLint
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout


class MainActivity : AppCompatActivity() {

    var bu : Button? = null

    private lateinit var navbtn : Button
    private lateinit var createbtn : Button
    private lateinit var promiseroombtn : Button
    private lateinit var promiselistbtn : Button

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navbtn = findViewById(R.id.Main_Button_Navi)
        createbtn = findViewById(R.id.Main_Button_CreatePromise)
        promiseroombtn = findViewById(R.id.Main_Button_PromiseRoom)
        promiselistbtn = findViewById(R.id.Main_Button_PromiseList)

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

        promiselistbtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {

                val intent : Intent = Intent(this@MainActivity,PromiseList::class.java)

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
