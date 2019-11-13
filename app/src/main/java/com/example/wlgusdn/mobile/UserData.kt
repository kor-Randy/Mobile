package com.example.wlgusdn.mobile

class UserData
{

    var Name : String? = null
    var Phone : String?=null
    var Freinds : ArrayList<FriendData>? = null
    var Promises : ArrayList<String>? = null
    var Token : String?=null
constructor(name : String , phone : String, freind : ArrayList<FriendData>,promise : ArrayList<String>,token:String)
{
    Name = name
    Phone =phone
     Freinds = freind
     Promises = promise
     Token=token
}
    constructor()
    {}

}