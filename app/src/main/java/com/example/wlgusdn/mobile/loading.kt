package com.example.wlgusdn.mobile

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class loading: ProgressDialog {
    private var imgLogo: ImageView? = null

    var thiscontext : Context?=null

    constructor(context : Context) : super(context)
    {

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCanceledOnTouchOutside(false)
        thiscontext=context
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ld)
        imgLogo = findViewById(R.id.img_android)
        val anim = AnimationUtils.loadAnimation(thiscontext, R.anim.loading)
        imgLogo!!.animation = anim

    }

    override fun show() {
        super.show()
    }

    override fun dismiss() {


        super.dismiss()
    }

    override fun onBackPressed() {

    }
}
