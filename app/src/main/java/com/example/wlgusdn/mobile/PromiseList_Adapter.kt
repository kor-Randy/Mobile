package com.example.wlgusdn.mobile

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class PromiseList_Adapter (val context: Context, val PromiseList : MutableList<PromiseRoomData>) : RecyclerView.Adapter<PromiseList_Adapter.ListViewHolder>(){


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_promiselist_list, p0, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return PromiseList.size
    }

    override fun onBindViewHolder(p0: ListViewHolder, position: Int) {
        p0.bind(PromiseList[position], context)

    }

    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val roomName : TextView = view.findViewById(R.id.Promiselist_roomname)
        val date : TextView = view.findViewById(R.id.Promiselist_date)
        val participants : TextView = view.findViewById(R.id.Promiselist_participant)
        val address : TextView = view.findViewById(R.id.Promiselist_address)


        fun bind (list: PromiseRoomData, context: Context){
            roomName.text = "room"
            date.text = list.Date + ", " + list.Time
            participants.text = list.Participants.toString()
            address.text = list.Address + ' ' + list.EtcAddress

        }
    }



}
