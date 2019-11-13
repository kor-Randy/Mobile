package com.example.wlgusdn.mobile

import android.graphics.Bitmap

class FriendData
{

    var Name :String? = null
    var Pic : Bitmap?= null
    var Id : String?=null


    constructor(name : String?,pic :Bitmap?,id:String?)
    {
        this.Name=name
        this.Pic = pic
        this.Id=id
    }
    constructor()
    {}

}