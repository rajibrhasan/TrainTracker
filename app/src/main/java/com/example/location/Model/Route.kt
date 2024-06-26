package com.example.location.Model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable
import java.sql.Time

class Route(val city : String, val arr_time:String, val dep_time : String, val halt: String,
            val duration: String,  val distance:Double, val coordInd : Int, val is_stop : Boolean) : Serializable{
}