package com.example.location.Util

import android.content.Context
import android.util.Log
import com.example.location.Model.Station
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class StationFiller(val context: Context, val stops : Set<String>) {
    val stationList : ArrayList<Station> = arrayListOf()

    fun getStations() : ArrayList<Station>{
        addStations()
        return stationList
    }

    private fun addStations(){
        try {
            val obj = JSONObject(loadJSONFromAsset(context,"stations_loacations.json"))
            val stArr = obj.getJSONArray("data")
            for (i in 0 until stArr.length()) {
                val stJson = stArr.getJSONObject(i)

                val name = stJson.getString("name")
                val lat = stJson.getDouble("lat")
                val long = stJson.getDouble("long")

                if(stops.contains(name)) stationList.add(Station(name,lat,long))

            }

        }
        catch (e:JSONException){
            e.printStackTrace()
            Log.d("Rajib","json problem")
        }
    }



    private fun loadJSONFromAsset(context:Context, path:String): String {
        val json: String?
        try {
            val inputStream = context.assets.open(path)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            val charset: Charset = Charsets.UTF_8
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, charset)
        }
        catch (ex: IOException) {
            ex.printStackTrace()
            return ""
        }
        return json
    }
}