package com.example.wlgusdn.mobile

class MainActivity_listData (name : String, date : String, time: String, roomId : String)
{

    var name : String = name
    var date : String = date
    var time : String = time
    var roomId : String = roomId

    override fun toString(): String {
        return time + "  " + name

    }



}