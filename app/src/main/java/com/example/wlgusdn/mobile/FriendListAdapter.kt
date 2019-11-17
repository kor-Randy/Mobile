package com.example.wlgusdn.mobile




import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView


import java.util.ArrayList

class FriendListAdapter(data: ArrayList<FriendData>?) : BaseAdapter() {


    private var CheckStringData : ArrayList<FriendData>? = ArrayList<FriendData>()
    private var CheckStringDataCount : Int? = 0
    internal var inflater: LayoutInflater? = null

    init {

        CheckStringData = data
        CheckStringDataCount = data?.size
        Log.d("checkkkk","aaa")

    }


    override fun getCount(): Int {

        return CheckStringDataCount!!
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val context = parent.context

            if (inflater == null) {
                inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            }
            convertView = inflater!!.inflate(R.layout.list_friend, parent, false)
        }

        //구매글들에 대한 정보들

        val pic_iv = convertView!!.findViewById<ImageView>(R.id.list_friend_iv)
        val name_tv = convertView.findViewById<TextView>(R.id.list_friend_tv)

        Log.d("checkcheck",position.toString())


        pic_iv.setImageBitmap(CheckStringData?.get(position)!!.Pic)
        name_tv.setText(CheckStringData?.get(position)!!.Name!!)



        return convertView!!
    }


}
