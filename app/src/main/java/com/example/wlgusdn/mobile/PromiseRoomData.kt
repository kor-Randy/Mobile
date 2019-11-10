package com.example.wlgusdn.mobile

class PromiseRoomData(name : String,date : String,time : String, address : String, etcaddress : String,content: String,participant: ArrayList<String>)
{

    var Name : String? = null
    var Date : String? = null
    var Time : String? = null
    var Address : String? = null
    var EtcAddress : String? = null
    var Content : String? = null
    var Participants : ArrayList<String>? = null

    init {
        Name=name
        Date=date
        Time=time
        Address=address
        EtcAddress=etcaddress
        Content=content
        Participants=participant
    }



}