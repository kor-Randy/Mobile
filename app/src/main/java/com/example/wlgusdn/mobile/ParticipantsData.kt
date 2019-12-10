package com.example.wlgusdn.mobile

import android.graphics.Bitmap

class ParticipantsData (name : String, id : String, image : Bitmap?= null, phone : String){
    var name : String = name
    var id : String = id
    var image : Bitmap?= image
    var phone : String = phone
}
