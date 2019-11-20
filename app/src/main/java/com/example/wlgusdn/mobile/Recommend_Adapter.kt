package com.example.wlgusdn.mobile

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class Recommend_Adapter (val context: Context, val RecommendList : MutableList<Recommend_Data>) : RecyclerView.Adapter<Recommend_Adapter.ListViewHolder>(){


    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_recommend_list, p0, false)
        return ListViewHolder(view)
    }

    override fun getItemCount(): Int {
        //return PromiseList.size
        return RecommendList.size
    }

    override fun onBindViewHolder(p0: ListViewHolder, position: Int) {
        p0.bind(RecommendList[position], context)

    }

    inner class ListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name : TextView = view.findViewById(R.id.Recommend_name)
        val image : ImageView = view.findViewById(R.id.Recommend_image)
        val address : TextView = view.findViewById(R.id.Recommend_address)
        val site : TextView = view.findViewById(R.id.Recommend_site)
        val dist : TextView = view.findViewById(R.id.Recommend_dist)


        fun bind (list: Recommend_Data, context: Context){
            name.text = list.name
            address.text = list.address
            site.text = list.site


            Glide.with(context).load(list.image)
                .override(200, 200)
                .centerCrop()
                .into(image)
            //image.setImageBitmap(list.image)

            dist.text = list.distance + "m"
        }

    }





}