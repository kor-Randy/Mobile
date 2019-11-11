package com.example.wlgusdn.mobile

import android.graphics.Bitmap

class ChatRoom_Chat (who : String, text : String, time : String, image : Bitmap?= null){
    var who : String = who
    var text : String = text
    var time : String = time
    var image : Bitmap?= image
}