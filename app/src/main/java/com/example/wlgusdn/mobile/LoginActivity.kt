package com.example.wlgusdn.mobile

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity()
{

    var Password : EditText?= null
    var Id : EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        Password=findViewById(R.id.Login_Edit_Password)
        Id = findViewById(R.id.Login_Edit_Id)


    }

}