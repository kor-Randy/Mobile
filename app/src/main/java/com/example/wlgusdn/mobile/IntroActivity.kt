package com.example.wlgusdn.mobile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro) //xml,java 소스 연결
        val handler = Handler()
        handler.postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) //다음 화면으로 넘어감
            finish()
        }, 3000) //3초 뒤에 Runne r객체 실행하도록 함
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}

