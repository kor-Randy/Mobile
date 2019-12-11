package com.example.wlgusdn.mobile

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recommend.*
import java.net.HttpURLConnection
import java.net.URL
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.Base64
import android.view.View

import org.json.JSONObject
import java.io.*


@Suppress("DEPRECATION")
class Recommend : AppCompatActivity(){

    var RecommendList_place : MutableList<Recommend_Data> = arrayListOf()
    var RecommendList_rest : MutableList<Recommend_Data> = arrayListOf()
    var RecommendList_tour : MutableList<Recommend_Data> = arrayListOf()
    var RecommendList_fest : MutableList<Recommend_Data> = arrayListOf()

    //var position by lazy {intent.extras["location"] as String}

    val position by lazy {intent.extras["location"] as String}




    //관광지 12
    //문화시 14
    //공연 축제 15
    //음식점 39
    //쇼핑 38
    //레포츠 28
    //var type : Int = 76
    var apiurl : String = ""


    //var imagebitmap : Bitmap ?= null


    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_recommend)



        val place : Button = findViewById(R.id.Recommend_place)
        val restaurant : Button = findViewById(R.id.Recommend_rest)
        val tour : Button = findViewById(R.id.Recommend_tour)
        val festival : Button = findViewById(R.id.Recommend_festival)


        println("position ${position.split(",")[0]}   ${position.split(",")[1]}")


        val latitude : String = position.split(",")[0]
        val longitude : String = position.split(",")[1]

        //P 조회순 R 생성일 순
        apiurl = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/locationBasedList" +
        "?ServiceKey=HzlbF03lF%2B1P9hi9YUvJwHH4b0IMjI%2F2Ev4JwX1%2BNhSBqzYCBZbg4HluegctzanEHcxai9B9vWTPvciEupnjOA%3D%3D" +
                "&numOfRows=200" +
                "&pageSize=1" +
                "&pageNo=1" +
                "&startPage=1" +
                "&MobileOS=AND" +
                "&MobileApp=AppTest" +
                "&listYN=Y" +
                "&arrange=R" +
                "&mapX=" + longitude+
                "&mapY=" + latitude+
                //"&mapX=126.981611" +
                //"&mapY=37.568477" +
                "&radius=2000" +
                //"&contentId=336061" +
                //"&contentTypeId=" + type.toString() +
                "&_type=json"




        getlist()

        place.setOnClickListener {
            Recommend_viewplace.visibility = View.VISIBLE
            Recommend_viewrest.visibility = View.INVISIBLE
            Recommend_viewtour.visibility = View.INVISIBLE
            Recommend_viewfest.visibility = View.INVISIBLE
        }


        restaurant.setOnClickListener {
            Recommend_viewplace.visibility = View.INVISIBLE
            Recommend_viewrest.visibility = View.VISIBLE
            Recommend_viewtour.visibility = View.INVISIBLE
            Recommend_viewfest.visibility = View.INVISIBLE

        }

        tour.setOnClickListener {
            Recommend_viewplace.visibility = View.INVISIBLE
            Recommend_viewrest.visibility = View.INVISIBLE
            Recommend_viewtour.visibility = View.VISIBLE
            Recommend_viewfest.visibility = View.INVISIBLE

        }

        festival.setOnClickListener {
            Recommend_viewplace.visibility = View.INVISIBLE
            Recommend_viewrest.visibility = View.INVISIBLE
            Recommend_viewtour.visibility = View.INVISIBLE
            Recommend_viewfest.visibility = View.VISIBLE

        }



        val adapter_place = Recommend_Adapter(this, RecommendList_place)

        Recommend_viewplace.adapter = adapter_place
        Recommend_viewplace.layoutManager = LinearLayoutManager(this)

        val adapter_rest = Recommend_Adapter(this, RecommendList_rest)

        Recommend_viewrest.adapter = adapter_rest
        Recommend_viewrest.layoutManager = LinearLayoutManager(this)

        val adapter_tour = Recommend_Adapter(this, RecommendList_tour)

        Recommend_viewtour.adapter = adapter_tour
        Recommend_viewtour.layoutManager = LinearLayoutManager(this)

        val adapter_fest = Recommend_Adapter(this, RecommendList_fest)

        Recommend_viewfest.adapter = adapter_fest
        Recommend_viewfest.layoutManager = LinearLayoutManager(this)






    }



    fun getlist(){
        println("url ${apiurl}")
        val output = Download.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, apiurl).get()
        //Download.isCancelled
        println("output  ${output}")

        var json = JSONObject(output)
        json = json.getJSONObject("response")
        json = json.getJSONObject("body")
        json = json.getJSONObject("items")
        var jsonarray = json.getJSONArray("item")
        println("json ${jsonarray}")
        println("length ${jsonarray.length()}")


        for (x in 0 until jsonarray.length()) {
            //println("x ${x}")
            val jsonObject = jsonarray.getJSONObject(x)
            var address = ""
            var address2 : String = " "
            //+ jsonObject.getString("addr2")
            val name = jsonObject.getString("title")
            val type = jsonObject.getString("contenttypeid")
            val dist = jsonObject.getString("dist")
            var tel : String = " "
            var image = ""


            try{
                address = jsonObject.getString("addr1")

            }catch (e: Exception){
                println("no add")
            }

            try{
                address2 = address2 + jsonObject.getString("addr2")
                address = address + address2

            }catch (e: Exception){
                println("no add2")
            }


            try{
                tel = jsonObject.getString("tel")
            }catch (e: Exception){
                println("no tel")
            }


            //var bitmap : Bitmap ?= null
            try {

                image = jsonObject.getString("firstimage")





            }catch (e: Exception){
                println("image exception: ${e.printStackTrace()}")
            }






            when (type){
                "14" -> {
                    RecommendList_place.add(Recommend_Data(name, address, tel , image, dist))
                    //println("added ${type}")
                }
                "38" -> {
                    RecommendList_place.add(Recommend_Data(name, address, tel , image, dist))
                    //println("added ${type}")
                }
                "28" -> {
                    RecommendList_place.add(Recommend_Data(name, address, tel , image, dist))
                    //println("added ${type}")
                }

                "39" -> {
                    RecommendList_rest.add(Recommend_Data(name, address, tel , image, dist))
                    //println("added ${type}")
                }

                "12" -> {
                    RecommendList_tour.add(Recommend_Data(name, address, tel , image, dist))
                    //println("added ${type}")
                }
                "15" -> {
                    RecommendList_fest.add(Recommend_Data(name, address, tel , image, dist))
                    //println("added ${type}")
                }
            }




        }




    }




    object DownloadImage : AsyncTask<String, Int, Bitmap>() {
        override fun doInBackground(vararg params: String): Bitmap? {
            var bitmap : Bitmap ?= null
            //var image : String ?= null
            try {
                params.first().let {
                    val uri : URL = URL(it)
                    var connection : HttpURLConnection = uri.openConnection() as HttpURLConnection
                    connection.requestMethod //GET

                    val inputstream: InputStream? = connection.inputStream

                    bitmap = BitmapFactory.decodeStream(inputstream)

                    println("bitmap ${bitmap}")


                }
            }catch (e: Exception){
                println("exception: ${e.printStackTrace()}")
            }

            return bitmap
        }



    }




    object Download : AsyncTask<String, Int, String>()  {
        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
        }

        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }
        override fun doInBackground(vararg params: String): String? {
            var output : String ?= null
            try {
                params.first().let {
                    val uri : URL = URL(it)
                    var connection : HttpURLConnection = uri.openConnection() as HttpURLConnection
                    connection.requestMethod //GET
                    //connection.setRequestProperty("Content-type", "application/json")
                    //println("request code : ${connection.responseCode}")
                    var inputstream: InputStream? = connection.inputStream
                    val buffreader : BufferedReader = inputstream!!.bufferedReader(Charsets.UTF_8)

                    output = buffreader.readText()

                    //println("output ${output}")


                }

            }catch (e: Exception){
                println("exception: ${e.printStackTrace()}")
            }
            return output
        }


        override fun onCancelled() {
            super.onCancelled()

        }

    }




    fun BitmapeToString(bitmap: Bitmap) : String{
        var baos : ByteArrayOutputStream ?= null
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos)
        var bytes = baos?.toByteArray()
        val string : String = Base64.encodeToString(bytes, Base64.DEFAULT)

        return string
    }


    fun StringToBitmap(encodedString: String): Bitmap? {
        try {
            val encodeByte = Base64.decode(encodedString, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)


        } catch (e: Exception) {
            e.message
            return null
        }

    }


    fun getBitmapFromURL(src: String): Bitmap? {
        try {
            val url = java.net.URL(src)
            val connection = url
                .openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

    }




}

