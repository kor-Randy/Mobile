package com.example.wlgusdn.mobile

import com.kakao.usermgmt.StringSet
import com.kakao.usermgmt.StringSet.name

class PromiseRoomData
{

    var Name : String? = null
    var Date : String? = null
    var Time : String? = null
    var Address : String? = null
    var EtcAddress : String? = null
    var Content : String? = null
    var Participants : ArrayList<FriendData>? = null
    var Lati : Double?=null
    var Long : Double?=null

    constructor(name : String,date : String,time : String, address : String, etcaddress : String,content: String,participant: ArrayList<FriendData>,lati : Double, long:Double)
    {
        Name= name
        Date=date
        Time=time
        Address=address
        EtcAddress=etcaddress
        Content=content
        Participants=participant
        Lati=lati
        Long = long
    }


    constructor()
    {}



}