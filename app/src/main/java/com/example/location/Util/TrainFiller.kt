package com.example.location.Util

import android.content.Context
import android.util.Log
import com.example.location.Model.Coordinate
import com.example.location.Model.Route
import com.example.location.Model.Station
import com.example.location.Model.Step
import com.example.location.Model.Train
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Integer.max
import java.nio.charset.Charset

class TrainFiller(val context: Context) {
    val trainList : ArrayList<Train> = arrayListOf()
    val stops  = mutableSetOf<String>()

    fun getTrains(): Pair<ArrayList<Train>, Set<String>>{
        try {
            val obj = JSONObject(loadJSONFromAsset(context,"off_days.json"))
            context.assets.list("trains")?.forEach { file ->
                addTrain("trains/"+file, obj)
            }


        }
        catch (e:JSONException){
            e.printStackTrace()
            Log.d("Rajib","json problem")
        }

        return Pair(trainList, stops)
    }

    private fun addTrain(path: String, off_days : JSONObject) {
        val id:Int
        val name:String
        val src:String
        val dest:String
        val total_duration:String
        val total_distance : Double
        val route: ArrayList<Route> = arrayListOf()

        try {
            val obj = JSONObject(loadJSONFromAsset(context,path))
            name = obj.getString("name")
            id = obj.getInt("id")
            src = obj.getString("source")
            dest = obj.getString("destination")
            total_duration = obj.getString("total_duration")
            total_distance = obj.getDouble("total_distance")
            val routesArr = obj.getJSONArray("routes")
            val coords : MutableList<Coordinate> = arrayListOf()
            var distance = 0.00

            for (i in 0 until routesArr.length()) {
                val routeJson = routesArr.getJSONObject(i)
                val polyline : String = routeJson.getString("polyline")?:""

                if(polyline !="") {
                    coords.addAll(decode(polyline))
                }

                val city : String = routeJson.getString("city")?:""
                val arr_time : String = routeJson.getString("arrival_time")?:"NA"
                val dep_time : String = routeJson.getString("departure_time")?:"NA"
                var halt : String = routeJson.getString("halt")?:""
                val duration: String = routeJson.getString("duration")?:"NA"
                distance += routeJson.getDouble("distance")?:0.0

                var st_ind = coords.size - 1
                st_ind = max(st_ind,0)
                var is_stop = false
                if(arr_time != "null" || dep_time != "null") is_stop = true

                if(is_stop) stops.add(city)
                route.add(Route(city,cleanupTime(arr_time) ,cleanupTime(dep_time), halt, duration, distance,st_ind, is_stop))
            }

            val dep = route[0].dep_time
            val arr = route.last().arr_time
            val off_day = off_days.getString(id.toString())

            trainList.add(Train(id,name,src,dest, off_day,total_duration,dep,arr,total_distance,route,coords))
        }
        catch (e: JSONException) {
            e.printStackTrace()
            Log.e("Rajib","Problem in reading json")
        }

        catch (e:IOException){
            e.printStackTrace()
            Log.e("Rajib","Problem in io")
        }

    }

    fun cleanupTime(time : String ) : String{
        var newTime = time
        if(time == "null" || time == "N/A" || time == "NA") newTime = "---"
        return newTime.replace("BST","")
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