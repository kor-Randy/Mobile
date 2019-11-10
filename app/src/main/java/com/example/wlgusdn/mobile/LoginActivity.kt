package com.example.wlgusdn.mobile

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import java.util.*
import java.util.Arrays.asList
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.wlgusdn.mobile.LobbyActivity.Companion.auth
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class LoginActivity : AppCompatActivity()
{
    var Password : EditText?= null
    var Id : EditText? = null
    var btn_facebook_login : LoginButton?=null
    var mLoginCallback : LoginCallback?=null
    var mCallbackManager : CallbackManager?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        Password=findViewById(R.id.Login_Edit_Password)
        Id = findViewById(R.id.Login_Edit_Id)
        mCallbackManager = CallbackManager.Factory.create()

        mLoginCallback = LoginCallback()
        auth=FirebaseAuth.getInstance()



        btn_facebook_login = findViewById(R.id.Login_FaceLogin) as LoginButton

        btn_facebook_login!!.setReadPermissions(Arrays.asList("public_profile", "email"))

        btn_facebook_login!!.registerCallback(mCallbackManager,object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                //페이스북 로그인 성공
                handleFacebookAccessToken(result?.accessToken)
                Toast.makeText(this@LoginActivity,"로그인 성공",Toast.LENGTH_LONG).show()
            }
            override fun onCancel() {
                //페이스북 로그인 취소
                gogo(null)
            }

            override fun onError(error: FacebookException?) {
                //페이스북 로그인 실패
               gogo(null)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        mCallbackManager!!.onActivityResult(requestCode, resultCode, data)

        super.onActivityResult(requestCode, resultCode, data)



    }

    fun handleFacebookAccessToken(token : AccessToken?)
    {
        Log.d("MainActivity", "handleFacebookAccessToken:$token")

        if (token != null) {
            val credential = FacebookAuthProvider.getCredential(token.token)
            auth!!.signInWithCredential(credential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("MainActivity", "signInWithCredential:success")
                            val user = auth!!.currentUser

                            gogo(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MainActivity", "signInWithCredential:failure", task.exception)
                           Toast.makeText(this@LoginActivity,"Authentication failed.",Toast.LENGTH_LONG).show()

                            gogo(null)
                        }
                    }
        }

    }

    override fun onStart() { //로그인유저되있는 유저를 확인함
        super.onStart()
        val currentUser = auth!!.currentUser

        if(currentUser!=null)
        {
           gogo(currentUser)
        }


    }

    fun gogo(current : FirebaseUser?)
    {
        if(current!=null)
        {
            val intent : Intent = Intent(this@LoginActivity,AccountActivity::class.java)

            startActivity(intent)
        }
    }



}