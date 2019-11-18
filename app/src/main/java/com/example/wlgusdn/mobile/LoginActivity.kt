package com.example.wlgusdn.mobile

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.login.widget.LoginButton
import java.util.*
import java.util.Arrays.asList
import android.content.Intent
import android.util.Log
import android.view.View
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
import com.kakao.auth.ISessionCallback
import com.kakao.auth.Session
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.LinkObject
import com.kakao.message.template.TextTemplate
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.MeV2ResponseCallback
import com.kakao.usermgmt.response.MeV2Response
import com.kakao.util.exception.KakaoException


class LoginActivity : AppCompatActivity()
{
    var Password : EditText?= null
    var Id : EditText? = null
    var btn_facebook_login : LoginButton?=null
    var btn_kakao_login : com.kakao.usermgmt.LoginButton?=null
    var mLoginCallback : LoginCallback?=null
    var mCallbackManager : CallbackManager?=null

    private var callback: SessionCallback = SessionCallback()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        logincontext=this@LoginActivity
        Password=findViewById(R.id.Login_Edit_Password)
        Id = findViewById(R.id.Login_Edit_Id)
        mCallbackManager = CallbackManager.Factory.create()

        mLoginCallback = LoginCallback()
        auth=FirebaseAuth.getInstance()

        Session.getCurrentSession().addCallback(callback)

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



        super.onActivityResult(requestCode, resultCode, data)

        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            Log.d("kkaaoo", "session get current session")
            return
        }
        else
        {
            mCallbackManager!!.onActivityResult(requestCode, resultCode, data)
        }



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


    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback);
    }



    private class SessionCallback : ISessionCallback {
        override fun onSessionOpenFailed(exception: KakaoException?) {
            Log.d("kkaaoo","Session Call back :: onSessionOpenFailed: ${exception?.message}")
        }

        override fun onSessionOpened() {
            UserManagement.getInstance().me(object : MeV2ResponseCallback() {

                override fun onFailure(errorResult: ErrorResult?) {
                    Log.d("kkaaoo","Session Call back :: on failed ${errorResult?.errorMessage}")
                }

                override fun onSessionClosed(errorResult: ErrorResult?) {
                    Log.d("kkaaoo","Session Call back :: onSessionClosed ${errorResult?.errorMessage}")

                }

                override fun onSuccess(result: MeV2Response?) {
                    checkNotNull(result) {

                        "session response null" }
                    Log.d("kkaaoo","Success"+result!!.id)

                    var id = Session.getCurrentSession().accessToken



                   /* auth!!.signInWithCustomToken(id).addOnCompleteListener { task ->


                         Log.d("kkaaoo","signinwithcustomtokensussess")
                            val intent = Intent(logincontext, AccountActivity::class.java)
                            logincontext!!.startActivity(intent)


                    }*/
                    auth!!.signInAnonymously().addOnCompleteListener { task ->
                        val user=auth!!.currentUser

                        Log.d("kkaaoo",user!!.uid)
                        val intent  = Intent(logincontext,AccountActivity::class.java)
                        logincontext!!.startActivity(intent)

                    }

                    // register or login
                }

            })








        }

    }
companion object
{
    var logincontext : Context?=null
}
}