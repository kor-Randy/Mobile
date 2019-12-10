package com.example.wlgusdn.mobile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wlgusdn.mobile.FriendList_Adapter.ItemClick



class FriendList_Adapter (val context: Context, val FriendList : MutableList<FriendData>) : RecyclerView.Adapter<FriendList_Adapter.ListViewHolder>(){


    interface ItemClick
    {
        fun onClick(view: View, position: Int)
    }

    var itemClick: ItemClick? = null




    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_friend, p0, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return FriendList.size
    }



    override fun onBindViewHolder(p0: ListViewHolder, position: Int) {
        p0.bind(FriendList[position], context)

        if(itemClick != null)
        {
            p0.itemView.setOnClickListener { v ->
                itemClick?.onClick(v, position)
            }
        }

    }



    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image : ImageView = view.findViewById(R.id.list_friend_iv)
        val name : TextView = view.findViewById(R.id.list_friend_tv)


        fun bind (list: FriendData, context: Context){
            name.text = list.Name
            image.setImageBitmap(list.Pic)

        }
    }



}