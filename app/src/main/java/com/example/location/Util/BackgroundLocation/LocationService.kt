package com.example.location.Util.BackgroundLocation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.location.Model.FirebaseTrain
import com.example.location.Model.Route
import com.example.location.Model.Train
import com.example.location.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private lateinit var ref: DatabaseReference
    private var timesIncorrect = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val train = intent?.extras?.customGetSerializable<Train>("train")
        timesIncorrect = 0
        ref = database.reference.child("trains").child(train?.id.toString())
        when (intent?.action) {
            ACTION_START -> train?.let { start(it) }
            ACTION_STOP -> stop()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(train : Train) {
        val builder = NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.train2)
            .setContentTitle(train.name)
            .setContentText("Location is being updated.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id", "Location Update", NotificationManager.IMPORTANCE_LOW)
            channel.description = "This channel is used to notify users about location update."
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(1, builder.build())

        val points : ArrayList<LatLng> = arrayListOf()
        for(c in train.coords) points.add(LatLng(c.latitude, c.longitude))


        locationClient.getLocationUpdates(30000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val lastCoordInd = getNearestPoint(points, LatLng(location.latitude, location.longitude))
                val lastStationInd = getLastStation(train.route,lastCoordInd)
                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val fb_train  = snapshot.getValue(FirebaseTrain::class.java)!!
                        if(lastCoordInd > fb_train.lastCoordInd) {
                            val zonedTimeNow = ZonedDateTime.now( ZoneId.of("GMT+06:00") )
                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss a z")
                            val timeNow_string = zonedTimeNow.format(formatter)

                            ref.child("lastCoordInd").setValue(lastCoordInd)
                            ref.child("lastCoordUpdate").setValue(timeNow_string)

                            if(lastStationInd > fb_train.lastStationInd){
                                if(fb_train.lastStationInd == -1)
                                    ref.child("startTime").setValue(timeNow_string)
                                ref.child("lastStationInd").setValue(lastStationInd)
                                ref.child("lastStationUpdate").setValue(timeNow_string)
                            }



                            timesIncorrect = 0
                            if(lastCoordInd == points.size - 1) stop()
                        }
                        else if(lastCoordInd == -1){
                            timesIncorrect += 1
                            if(timesIncorrect == 5) stop()
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {}
                })

            }.launchIn(serviceScope)
    }

    private fun getNearestPoint(points : List<LatLng>,currentPoint : LatLng) : Int{
        var ind = -1
        var dist = 1000.00
        for(i in 0..points.size-1){
            val temp_dist = SphericalUtil.computeDistanceBetween(points[i], currentPoint)/1000
            if( temp_dist < dist){
                dist = temp_dist
                ind = i
            }
        }

        if(dist < 2.00) return ind

        return -1
    }

    private fun getLastStation(routes : List<Route>, lastCoordInd : Int) : Int{
        var lastStationInd = -1
        for(i in 0..routes.size-1){
            val current_route = routes[i]
            if(current_route.coordInd < lastCoordInd){
                lastStationInd = i
            }
        }
        return lastStationInd
    }

    private fun stop() {
        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    @Suppress("DEPRECATION")
    inline fun <reified T : Serializable> Bundle.customGetSerializable(key: String): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSerializable(key, T::class.java)
        } else {
            getSerializable(key) as? T
        }
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}