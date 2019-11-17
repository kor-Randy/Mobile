package com.example.wlgusdn.mobile

import android.app.Activity
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

class FriendData() : Parcelable
{

    var Name :String? = null
    var Pic : Bitmap?= null
    var Id : String?=null

    constructor(parcel: Parcel) : this() {
        Name = parcel.readString()
        Pic = parcel.readParcelable(Bitmap::class.java.classLoader)
        Id = parcel.readString()
    }


    constructor(name : String?,pic :Bitmap?,id:String?) : this() {
        this.Name=name
        this.Pic = pic
        this.Id=id
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(Name)
        parcel.writeParcelable(Pic, flags)
        parcel.writeString(Id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FriendData> {
        override fun createFromParcel(parcel: Parcel): FriendData {
            return FriendData(parcel)
        }

        override fun newArray(size: Int): Array<FriendData?> {
            return arrayOfNulls(size)
        }
    }

}